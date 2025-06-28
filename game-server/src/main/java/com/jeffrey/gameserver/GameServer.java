package com.jeffrey.gameserver;

import com.jeffrey.gameserver.config.ServerConfig;
import com.jeffrey.gameserver.handler.ChatHandler;
import com.jeffrey.gameserver.handler.LoginHandler;
import com.jeffrey.gameserver.handler.MessageHandler;
import com.jeffrey.gameserver.network.NettyServer;
import com.jeffrey.gameserver.protocol.MessageType;
import com.jeffrey.gameserver.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 游戏服务器主启动类
 * 
 * @author jeffrey
 */
public class GameServer {
    
    private static final Logger logger = LoggerFactory.getLogger(GameServer.class);
    
    private NettyServer nettyServer;
    private SessionManager sessionManager;
    private Map<MessageType, MessageHandler> messageHandlers;
    private ServerConfig config;
    
    public GameServer() {
        this.config = new ServerConfig();
        this.sessionManager = new SessionManager();
        this.messageHandlers = new HashMap<>();
        this.nettyServer = new NettyServer(config, sessionManager, messageHandlers);
        
        // 注册消息处理器
        registerMessageHandlers();
    }
    
    /**
     * 注册消息处理器
     */
    private void registerMessageHandlers() {
        messageHandlers.put(MessageType.LOGIN, new LoginHandler(sessionManager));
        messageHandlers.put(MessageType.CHAT, new ChatHandler(sessionManager));
        
        logger.info("Message handlers registered: {}", messageHandlers.keySet());
    }
    
    /**
     * 启动服务器
     */
    public void start() {
        try {
            logger.info("Starting Game Server...");
            logger.info("Server Config: host={}, port={}", config.getHost(), config.getPort());
            
            nettyServer.start();
            
            logger.info("Game Server started successfully!");
            
            // 添加关闭钩子
            Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
            
        } catch (Exception e) {
            logger.error("Failed to start Game Server", e);
            System.exit(1);
        }
    }
    
    /**
     * 停止服务器
     */
    public void stop() {
        logger.info("Stopping Game Server...");
        
        if (nettyServer != null) {
            nettyServer.stop();
        }
        
        if (sessionManager != null) {
            sessionManager.shutdown();
        }
        
        logger.info("Game Server stopped.");
    }
    
    /**
     * 获取会话管理器
     */
    public SessionManager getSessionManager() {
        return sessionManager;
    }
    
    /**
     * 获取服务器配置
     */
    public ServerConfig getConfig() {
        return config;
    }
    
    /**
     * 主方法
     */
    public static void main(String[] args) {
        GameServer server = new GameServer();
        server.start();
        
        // 保持主线程运行
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            logger.warn("Main thread interrupted", e);
        }
    }
}
