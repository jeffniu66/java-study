package com.jeffrey.gameserver.protocol.messages;

import com.jeffrey.gameserver.protocol.Message;
import com.jeffrey.gameserver.protocol.MessageType;

/**
 * 聊天消息
 * 
 * @author jeffrey
 */
public class ChatMessage extends Message {
    
    /** 发送者ID */
    private String senderId;
    
    /** 发送者昵称 */
    private String senderName;
    
    /** 接收者ID (私聊时使用，为空表示世界聊天) */
    private String receiverId;
    
    /** 消息内容 */
    private String content;
    
    /** 聊天频道类型 */
    private ChatChannel channel;
    
    public ChatMessage() {
        super();
    }
    
    public ChatMessage(String senderId, String senderName, String content, ChatChannel channel) {
        super();
        this.senderId = senderId;
        this.senderName = senderName;
        this.content = content;
        this.channel = channel;
    }
    
    @Override
    public MessageType getType() {
        return MessageType.CHAT;
    }
    
    /**
     * 聊天频道类型
     */
    public enum ChatChannel {
        /** 世界聊天 */
        WORLD,
        /** 私聊 */
        PRIVATE,
        /** 公会聊天 */
        GUILD,
        /** 队伍聊天 */
        TEAM
    }
    
    // Getter和Setter方法
    public String getSenderId() {
        return senderId;
    }
    
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
    
    public String getSenderName() {
        return senderName;
    }
    
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
    
    public String getReceiverId() {
        return receiverId;
    }
    
    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public ChatChannel getChannel() {
        return channel;
    }
    
    public void setChannel(ChatChannel channel) {
        this.channel = channel;
    }
    
    @Override
    public String toString() {
        return "ChatMessage{" +
                "senderId='" + senderId + '\'' +
                ", senderName='" + senderName + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", content='" + content + '\'' +
                ", channel=" + channel +
                ", messageId='" + getMessageId() + '\'' +
                ", timestamp=" + getTimestamp() +
                '}';
    }
}
