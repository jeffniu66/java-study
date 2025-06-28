package com.jeffrey.gameserver.handler;

import com.jeffrey.gameserver.protocol.Message;
import com.jeffrey.gameserver.protocol.messages.ChatMessage;
import com.jeffrey.gameserver.session.GameSession;
import com.jeffrey.gameserver.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 聊天消息处理器
 * 
 * @author jeffrey
 */
public class ChatHandler implements MessageHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(ChatHandler.class);
    
    private final SessionManager sessionManager;
    
    public ChatHandler(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }
    
    @Override
    public void handle(GameSession session, Message message) {
        if (!(message instanceof ChatMessage)) {
            logger.warn("Invalid message type for ChatHandler: {}", message.getClass());
            return;
        }
        
        ChatMessage chatMessage = (ChatMessage) message;
        
        // 检查会话是否已认证
        if (!session.isAuthenticated()) {
            logger.warn("Unauthenticated session trying to send chat message: {}", session.getSessionId());
            sendErrorResponse(session, "Please login first");
            return;
        }
        
        // 设置发送者信息
        chatMessage.setSenderId(session.getPlayerId());
        chatMessage.setSenderName(session.getPlayerName());
        
        logger.info("Processing chat message: from={}, channel={}, content={}", 
                session.getPlayerName(), chatMessage.getChannel(), chatMessage.getContent());
        
        try {
            // 根据聊天频道类型处理消息
            switch (chatMessage.getChannel()) {
                case WORLD:
                    handleWorldChat(session, chatMessage);
                    break;
                case PRIVATE:
                    handlePrivateChat(session, chatMessage);
                    break;
                case GUILD:
                    handleGuildChat(session, chatMessage);
                    break;
                case TEAM:
                    handleTeamChat(session, chatMessage);
                    break;
                default:
                    logger.warn("Unsupported chat channel: {}", chatMessage.getChannel());
                    sendErrorResponse(session, "Unsupported chat channel");
            }
            
        } catch (Exception e) {
            logger.error("Error processing chat message", e);
            sendErrorResponse(session, "Failed to send message");
        }
    }
    
    /**
     * 处理世界聊天
     */
    private void handleWorldChat(GameSession sender, ChatMessage chatMessage) {
        // 广播给所有在线玩家
        int sentCount = 0;
        for (GameSession session : sessionManager.getAllSessions()) {
            if (session.isAuthenticated() && !session.getSessionId().equals(sender.getSessionId())) {
                session.sendMessage(chatMessage);
                sentCount++;
            }
        }
        
        // 发送确认响应给发送者
        sendChatResponse(sender, true, "Message sent to " + sentCount + " players");
        
        logger.info("World chat message broadcasted: from={}, to={} players", 
                sender.getPlayerName(), sentCount);
    }
    
    /**
     * 处理私聊
     */
    private void handlePrivateChat(GameSession sender, ChatMessage chatMessage) {
        String receiverId = chatMessage.getReceiverId();
        if (receiverId == null || receiverId.trim().isEmpty()) {
            sendErrorResponse(sender, "Receiver ID is required for private chat");
            return;
        }
        
        // 查找接收者会话
        GameSession receiverSession = sessionManager.getSessionByPlayerId(receiverId);
        if (receiverSession == null || !receiverSession.isAuthenticated()) {
            sendErrorResponse(sender, "Player not found or offline: " + receiverId);
            return;
        }
        
        // 发送消息给接收者
        receiverSession.sendMessage(chatMessage);
        
        // 发送确认响应给发送者
        sendChatResponse(sender, true, "Private message sent to " + receiverSession.getPlayerName());
        
        logger.info("Private chat message sent: from={} to={}", 
                sender.getPlayerName(), receiverSession.getPlayerName());
    }
    
    /**
     * 处理公会聊天
     */
    private void handleGuildChat(GameSession sender, ChatMessage chatMessage) {
        // 这里可以实现公会聊天逻辑
        // 目前简单地发送错误响应
        sendErrorResponse(sender, "Guild chat not implemented yet");
        logger.info("Guild chat attempted by: {}", sender.getPlayerName());
    }
    
    /**
     * 处理队伍聊天
     */
    private void handleTeamChat(GameSession sender, ChatMessage chatMessage) {
        // 这里可以实现队伍聊天逻辑
        // 目前简单地发送错误响应
        sendErrorResponse(sender, "Team chat not implemented yet");
        logger.info("Team chat attempted by: {}", sender.getPlayerName());
    }
    
    /**
     * 发送聊天响应
     */
    private void sendChatResponse(GameSession session, boolean success, String message) {
        ChatResponseMessage response = new ChatResponseMessage();
        response.setSuccess(success);
        response.setMessage(message);
        session.sendMessage(response);
    }
    
    /**
     * 发送错误响应
     */
    private void sendErrorResponse(GameSession session, String errorMessage) {
        sendChatResponse(session, false, errorMessage);
    }
    
    /**
     * 聊天响应消息
     * 内部类，用于发送聊天结果
     */
    public static class ChatResponseMessage extends Message {
        private boolean success;
        private String message;
        
        @Override
        public com.jeffrey.gameserver.protocol.MessageType getType() {
            return com.jeffrey.gameserver.protocol.MessageType.CHAT_RESPONSE;
        }
        
        // Getter和Setter方法
        public boolean isSuccess() {
            return success;
        }
        
        public void setSuccess(boolean success) {
            this.success = success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        @Override
        public String toString() {
            return "ChatResponseMessage{" +
                    "success=" + success +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
}
