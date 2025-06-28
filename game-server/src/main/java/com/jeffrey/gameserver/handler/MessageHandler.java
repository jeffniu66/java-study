package com.jeffrey.gameserver.handler;

import com.jeffrey.gameserver.protocol.Message;
import com.jeffrey.gameserver.session.GameSession;

/**
 * 消息处理器接口
 * 所有消息处理器都需要实现此接口
 * 
 * @author jeffrey
 */
public interface MessageHandler {
    
    /**
     * 处理消息
     * 
     * @param session 游戏会话
     * @param message 消息对象
     */
    void handle(GameSession session, Message message);
}
