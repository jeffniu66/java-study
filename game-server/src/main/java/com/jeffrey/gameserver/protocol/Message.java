package com.jeffrey.gameserver.protocol;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.jeffrey.gameserver.protocol.messages.ChatMessage;
import com.jeffrey.gameserver.protocol.messages.HeartbeatMessage;
import com.jeffrey.gameserver.protocol.messages.LoginMessage;

/**
 * 消息基类
 * 所有游戏消息都继承自此类
 * 
 * @author jeffrey
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "messageType")
@JsonSubTypes({
    @JsonSubTypes.Type(value = LoginMessage.class, name = "LOGIN"),
    @JsonSubTypes.Type(value = ChatMessage.class, name = "CHAT"),
    @JsonSubTypes.Type(value = HeartbeatMessage.class, name = "HEARTBEAT")
})
public abstract class Message {
    
    /** 消息ID */
    private String messageId;
    
    /** 时间戳 */
    private long timestamp;
    
    public Message() {
        this.messageId = generateMessageId();
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * 获取消息类型
     */
    public abstract MessageType getType();
    
    /**
     * 生成消息ID
     */
    private String generateMessageId() {
        return System.currentTimeMillis() + "-" + Thread.currentThread().getId();
    }
    
    // Getter和Setter方法
    public String getMessageId() {
        return messageId;
    }
    
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "Message{" +
                "messageId='" + messageId + '\'' +
                ", timestamp=" + timestamp +
                ", type=" + getType() +
                '}';
    }
}
