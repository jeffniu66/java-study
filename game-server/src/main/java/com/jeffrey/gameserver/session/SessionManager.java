package com.jeffrey.gameserver.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 会话管理器
 * 管理所有客户端会话
 * 
 * @author jeffrey
 */
public class SessionManager {
    
    private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);
    
    /** 会话超时时间(毫秒) */
    private static final long SESSION_TIMEOUT = 5 * 60 * 1000; // 5分钟
    
    /** 会话清理间隔(秒) */
    private static final long CLEANUP_INTERVAL = 60; // 1分钟
    
    /** 会话存储 - sessionId -> GameSession */
    private final ConcurrentHashMap<String, GameSession> sessions = new ConcurrentHashMap<>();
    
    /** 玩家会话映射 - playerId -> GameSession */
    private final ConcurrentHashMap<String, GameSession> playerSessions = new ConcurrentHashMap<>();
    
    /** 定时任务执行器 */
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread thread = new Thread(r, "SessionManager-Cleanup");
        thread.setDaemon(true);
        return thread;
    });
    
    public SessionManager() {
        // 启动会话清理任务
        startCleanupTask();
    }
    
    /**
     * 添加会话
     */
    public void addSession(GameSession session) {
        sessions.put(session.getSessionId(), session);
        logger.info("Session added: {}, total sessions: {}", session.getSessionId(), sessions.size());
    }
    
    /**
     * 移除会话
     */
    public GameSession removeSession(String sessionId) {
        GameSession session = sessions.remove(sessionId);
        if (session != null) {
            // 同时从玩家会话映射中移除
            if (session.getPlayerId() != null) {
                playerSessions.remove(session.getPlayerId());
            }
            logger.info("Session removed: {}, total sessions: {}", sessionId, sessions.size());
        }
        return session;
    }
    
    /**
     * 根据会话ID获取会话
     */
    public GameSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }
    
    /**
     * 根据玩家ID获取会话
     */
    public GameSession getSessionByPlayerId(String playerId) {
        return playerSessions.get(playerId);
    }
    
    /**
     * 绑定玩家到会话
     */
    public void bindPlayer(GameSession session, String playerId, String playerName) {
        session.setPlayerId(playerId);
        session.setPlayerName(playerName);
        session.setAuthenticated(true);
        
        // 检查是否有重复登录
        GameSession existingSession = playerSessions.get(playerId);
        if (existingSession != null && !existingSession.getSessionId().equals(session.getSessionId())) {
            logger.warn("Player {} already logged in, closing existing session: {}", 
                    playerId, existingSession.getSessionId());
            existingSession.close();
            removeSession(existingSession.getSessionId());
        }
        
        playerSessions.put(playerId, session);
        logger.info("Player bound to session: playerId={}, sessionId={}", playerId, session.getSessionId());
    }
    
    /**
     * 解绑玩家
     */
    public void unbindPlayer(String playerId) {
        GameSession session = playerSessions.remove(playerId);
        if (session != null) {
            session.setPlayerId(null);
            session.setPlayerName(null);
            session.setAuthenticated(false);
            logger.info("Player unbound from session: playerId={}, sessionId={}", playerId, session.getSessionId());
        }
    }
    
    /**
     * 获取所有会话
     */
    public Collection<GameSession> getAllSessions() {
        return sessions.values();
    }
    
    /**
     * 获取在线玩家数量
     */
    public int getOnlinePlayerCount() {
        return playerSessions.size();
    }
    
    /**
     * 获取总会话数量
     */
    public int getTotalSessionCount() {
        return sessions.size();
    }
    
    /**
     * 启动会话清理任务
     */
    private void startCleanupTask() {
        scheduler.scheduleAtFixedRate(this::cleanupTimeoutSessions, 
                CLEANUP_INTERVAL, CLEANUP_INTERVAL, TimeUnit.SECONDS);
        logger.info("Session cleanup task started, interval: {} seconds", CLEANUP_INTERVAL);
    }
    
    /**
     * 清理超时会话
     */
    private void cleanupTimeoutSessions() {
        try {
            int cleanedCount = 0;
            for (GameSession session : sessions.values()) {
                if (session.isTimeout(SESSION_TIMEOUT)) {
                    logger.info("Cleaning up timeout session: {}", session.getSessionId());
                    session.close();
                    removeSession(session.getSessionId());
                    cleanedCount++;
                }
            }
            
            if (cleanedCount > 0) {
                logger.info("Cleaned up {} timeout sessions", cleanedCount);
            }
        } catch (Exception e) {
            logger.error("Error during session cleanup", e);
        }
    }
    
    /**
     * 关闭会话管理器
     */
    public void shutdown() {
        logger.info("Shutting down SessionManager...");
        
        // 关闭所有会话
        for (GameSession session : sessions.values()) {
            session.close();
        }
        sessions.clear();
        playerSessions.clear();
        
        // 关闭定时任务
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
        
        logger.info("SessionManager shutdown completed.");
    }
}
