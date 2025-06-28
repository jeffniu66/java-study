package com.jeffrey.gameserver;

import com.jeffrey.gameserver.protocol.messages.LoginMessage;
import com.jeffrey.gameserver.util.JsonUtil;
import org.junit.Test;

/**
 * 简单测试类
 * 
 * @author jeffrey
 */
public class SimpleTest {
    
    @Test
    public void testJsonSerialization() {
        // 测试简单的JSON序列化
        LoginMessage loginMessage = new LoginMessage("testuser", "123456");
        loginMessage.setClientVersion("1.0.0");

        String json = JsonUtil.toJson(loginMessage);
        System.out.println("Serialized JSON: " + json);

        // 简单验证JSON包含必要字段
        assert json.contains("testuser");
        assert json.contains("123456");
        assert json.contains("1.0.0");

        System.out.println("JSON serialization test passed!");
    }
    
    @Test
    public void testMessageTypes() {
        // 测试消息类型
        LoginMessage loginMessage = new LoginMessage();
        System.out.println("Login message type: " + loginMessage.getType());
        System.out.println("Login message type code: " + loginMessage.getType().getCode());
        
        assert loginMessage.getType().getCode() == 1001;
        
        System.out.println("Message types test passed!");
    }
}
