package com.jeffrey.gameserver.protocol.messages;

import com.jeffrey.gameserver.protocol.Message;
import com.jeffrey.gameserver.protocol.MessageType;

/**
 * 登录消息
 * 
 * @author jeffrey
 */
public class LoginMessage extends Message {
    
    /** 用户名 */
    private String username;
    
    /** 密码 */
    private String password;
    
    /** 客户端版本 */
    private String clientVersion;
    
    public LoginMessage() {
        super();
    }
    
    public LoginMessage(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }
    
    @Override
    public MessageType getType() {
        return MessageType.LOGIN;
    }
    
    // Getter和Setter方法
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getClientVersion() {
        return clientVersion;
    }
    
    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }
    
    @Override
    public String toString() {
        return "LoginMessage{" +
                "username='" + username + '\'' +
                ", password='***'" +
                ", clientVersion='" + clientVersion + '\'' +
                ", messageId='" + getMessageId() + '\'' +
                ", timestamp=" + getTimestamp() +
                '}';
    }
}
