package com.jeffrey.gameserver.protocol;

/**
 * 消息类型枚举
 * 定义所有支持的消息类型
 * 
 * @author jeffrey
 */
public enum MessageType {
    
    /** 登录消息 */
    LOGIN(1001, "登录"),
    
    /** 登录响应 */
    LOGIN_RESPONSE(1002, "登录响应"),
    
    /** 聊天消息 */
    CHAT(2001, "聊天"),
    
    /** 聊天响应 */
    CHAT_RESPONSE(2002, "聊天响应"),
    
    /** 心跳消息 */
    HEARTBEAT(9001, "心跳"),
    
    /** 错误消息 */
    ERROR(9999, "错误");
    
    private final int code;
    private final String description;
    
    MessageType(int code, String description) {
        this.code = code;
        this.description = description;
    }
    
    /**
     * 根据代码获取消息类型
     */
    public static MessageType fromCode(int code) {
        for (MessageType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return name() + "(" + code + ", " + description + ")";
    }
}
