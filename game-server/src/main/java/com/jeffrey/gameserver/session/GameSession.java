package com.jeffrey.gameserver.session;

import com.jeffrey.gameserver.protocol.Message;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 游戏会话
 * 表示一个客户端连接的会话信息
 * 
 * @author jeffrey
 */
public class GameSession {
    
    private static final Logger logger = LoggerFactory.getLogger(GameSession.class);
    
    /** 会话属性键 */
    public static final AttributeKey<GameSession> SESSION_KEY = AttributeKey.valueOf("gameSession");
    
    /** 会话ID生成器 */
    private static final AtomicLong SESSION_ID_GENERATOR = new AtomicLong(1);
    
    /** 会话ID */
    private final String sessionId;
    
    /** 网络通道 */
    private final Channel channel;
    
    /** 玩家ID */
    private String playerId;
    
    /** 玩家昵称 */
    private String playerName;
    
    /** 是否已认证 */
    private boolean authenticated;
    
    /** 创建时间 */
    private final long createTime;
    
    /** 最后活跃时间 */
    private volatile long lastActiveTime;
    
    /** 会话状态 */
    private SessionState state;
    
    public GameSession(Channel channel) {
        this.sessionId = "session-" + SESSION_ID_GENERATOR.getAndIncrement();
        this.channel = channel;
        this.createTime = System.currentTimeMillis();
        this.lastActiveTime = this.createTime;
        this.state = SessionState.CONNECTED;
        this.authenticated = false;
    }
    
    /**
     * 发送消息给客户端
     */
    public void sendMessage(Message message) {
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(message).addListener(future -> {
                if (!future.isSuccess()) {
                    logger.error("Failed to send message to session: {}, message: {}", 
                            sessionId, message, future.cause());
                }
            });
        } else {
            logger.warn("Cannot send message to inactive session: {}", sessionId);
        }
    }
    
    /**
     * 关闭会话
     */
    public void close() {
        if (channel != null && channel.isActive()) {
            channel.close();
        }
        this.state = SessionState.CLOSED;
    }
    
    /**
     * 更新最后活跃时间
     */
    public void updateLastActiveTime() {
        this.lastActiveTime = System.currentTimeMillis();
    }
    
    /**
     * 检查会话是否超时
     */
    public boolean isTimeout(long timeoutMillis) {
        return System.currentTimeMillis() - lastActiveTime > timeoutMillis;
    }
    
    /**
     * 会话状态枚举
     */
    public enum SessionState {
        /** 已连接 */
        CONNECTED,
        /** 已认证 */
        AUTHENTICATED,
        /** 游戏中 */
        IN_GAME,
        /** 已关闭 */
        CLOSED
    }
    
    // Getter和Setter方法
    public String getSessionId() {
        return sessionId;
    }
    
    public Channel getChannel() {
        return channel;
    }
    
    public String getPlayerId() {
        return playerId;
    }
    
    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    
    public boolean isAuthenticated() {
        return authenticated;
    }
    
    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
        if (authenticated) {
            this.state = SessionState.AUTHENTICATED;
        }
    }
    
    public long getCreateTime() {
        return createTime;
    }
    
    public long getLastActiveTime() {
        return lastActiveTime;
    }
    
    public SessionState getState() {
        return state;
    }
    
    public void setState(SessionState state) {
        this.state = state;
    }
    
    @Override
    public String toString() {
        return "GameSession{" +
                "sessionId='" + sessionId + '\'' +
                ", playerId='" + playerId + '\'' +
                ", playerName='" + playerName + '\'' +
                ", authenticated=" + authenticated +
                ", state=" + state +
                ", createTime=" + createTime +
                ", lastActiveTime=" + lastActiveTime +
                '}';
    }
}
