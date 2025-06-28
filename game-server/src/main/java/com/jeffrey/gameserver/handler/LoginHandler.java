package com.jeffrey.gameserver.handler;

import com.jeffrey.gameserver.protocol.Message;
import com.jeffrey.gameserver.protocol.messages.LoginMessage;
import com.jeffrey.gameserver.session.GameSession;
import com.jeffrey.gameserver.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 登录消息处理器
 * 
 * @author jeffrey
 */
public class LoginHandler implements MessageHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(LoginHandler.class);
    
    private final SessionManager sessionManager;
    
    public LoginHandler(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }
    
    @Override
    public void handle(GameSession session, Message message) {
        if (!(message instanceof LoginMessage)) {
            logger.warn("Invalid message type for LoginHandler: {}", message.getClass());
            return;
        }
        
        LoginMessage loginMessage = (LoginMessage) message;
        logger.info("Processing login request: username={}, session={}", 
                loginMessage.getUsername(), session.getSessionId());
        
        try {
            // 验证登录信息
            boolean loginSuccess = validateLogin(loginMessage);
            
            if (loginSuccess) {
                // 登录成功
                handleLoginSuccess(session, loginMessage);
            } else {
                // 登录失败
                handleLoginFailure(session, loginMessage, "Invalid username or password");
            }
            
        } catch (Exception e) {
            logger.error("Error processing login request", e);
            handleLoginFailure(session, loginMessage, "Internal server error");
        }
    }
    
    /**
     * 验证登录信息
     * 这里可以连接数据库进行验证，目前使用简单的验证逻辑
     */
    private boolean validateLogin(LoginMessage loginMessage) {
        String username = loginMessage.getUsername();
        String password = loginMessage.getPassword();
        
        // 简单验证：用户名和密码不能为空，且密码长度至少6位
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        
        if (password == null || password.length() < 6) {
            return false;
        }
        
        // 这里可以添加更复杂的验证逻辑，比如查询数据库
        // 目前简单地认为所有符合基本条件的登录都是成功的
        return true;
    }
    
    /**
     * 处理登录成功
     */
    private void handleLoginSuccess(GameSession session, LoginMessage loginMessage) {
        String username = loginMessage.getUsername();
        String playerId = "player_" + username; // 简单生成玩家ID
        
        // 绑定玩家到会话
        sessionManager.bindPlayer(session, playerId, username);
        
        // 发送登录成功响应
        LoginResponseMessage response = new LoginResponseMessage();
        response.setSuccess(true);
        response.setPlayerId(playerId);
        response.setPlayerName(username);
        response.setMessage("Login successful");
        
        session.sendMessage(response);
        
        logger.info("Login successful: username={}, playerId={}, session={}", 
                username, playerId, session.getSessionId());
    }
    
    /**
     * 处理登录失败
     */
    private void handleLoginFailure(GameSession session, LoginMessage loginMessage, String reason) {
        // 发送登录失败响应
        LoginResponseMessage response = new LoginResponseMessage();
        response.setSuccess(false);
        response.setMessage(reason);
        
        session.sendMessage(response);
        
        logger.warn("Login failed: username={}, reason={}, session={}", 
                loginMessage.getUsername(), reason, session.getSessionId());
    }
    
    /**
     * 登录响应消息
     * 内部类，用于发送登录结果
     */
    public static class LoginResponseMessage extends Message {
        private boolean success;
        private String playerId;
        private String playerName;
        private String message;
        
        @Override
        public com.jeffrey.gameserver.protocol.MessageType getType() {
            return com.jeffrey.gameserver.protocol.MessageType.LOGIN_RESPONSE;
        }
        
        // Getter和Setter方法
        public boolean isSuccess() {
            return success;
        }
        
        public void setSuccess(boolean success) {
            this.success = success;
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
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        @Override
        public String toString() {
            return "LoginResponseMessage{" +
                    "success=" + success +
                    ", playerId='" + playerId + '\'' +
                    ", playerName='" + playerName + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
}
