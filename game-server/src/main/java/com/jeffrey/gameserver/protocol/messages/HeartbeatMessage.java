package com.jeffrey.gameserver.protocol.messages;

import com.jeffrey.gameserver.protocol.Message;
import com.jeffrey.gameserver.protocol.MessageType;

/**
 * 心跳消息
 * 用于保持客户端与服务器的连接
 * 
 * @author jeffrey
 */
public class HeartbeatMessage extends Message {
    
    /** 客户端时间戳 */
    private long clientTimestamp;
    
    /** 服务器时间戳 */
    private long serverTimestamp;
    
    public HeartbeatMessage() {
        super();
        this.clientTimestamp = System.currentTimeMillis();
    }
    
    @Override
    public MessageType getType() {
        return MessageType.HEARTBEAT;
    }
    
    // Getter和Setter方法
    public long getClientTimestamp() {
        return clientTimestamp;
    }
    
    public void setClientTimestamp(long clientTimestamp) {
        this.clientTimestamp = clientTimestamp;
    }
    
    public long getServerTimestamp() {
        return serverTimestamp;
    }
    
    public void setServerTimestamp(long serverTimestamp) {
        this.serverTimestamp = serverTimestamp;
    }
    
    @Override
    public String toString() {
        return "HeartbeatMessage{" +
                "clientTimestamp=" + clientTimestamp +
                ", serverTimestamp=" + serverTimestamp +
                ", messageId='" + getMessageId() + '\'' +
                ", timestamp=" + getTimestamp() +
                '}';
    }
}
