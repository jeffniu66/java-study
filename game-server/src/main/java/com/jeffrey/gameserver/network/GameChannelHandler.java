package com.jeffrey.gameserver.network;

import com.jeffrey.gameserver.handler.MessageHandler;
import com.jeffrey.gameserver.protocol.Message;
import com.jeffrey.gameserver.protocol.MessageType;
import com.jeffrey.gameserver.protocol.messages.HeartbeatMessage;
import com.jeffrey.gameserver.session.GameSession;
import com.jeffrey.gameserver.session.SessionManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 游戏频道处理器
 * 处理客户端连接、断开、消息接收等事件
 * 
 * @author jeffrey
 */
public class GameChannelHandler extends SimpleChannelInboundHandler<Message> {
    
    private static final Logger logger = LoggerFactory.getLogger(GameChannelHandler.class);
    
    private final SessionManager sessionManager;
    private final Map<MessageType, MessageHandler> messageHandlers;
    
    public GameChannelHandler(SessionManager sessionManager, 
                             Map<MessageType, MessageHandler> messageHandlers) {
        this.sessionManager = sessionManager;
        this.messageHandlers = messageHandlers;
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 客户端连接建立
        String remoteAddress = ctx.channel().remoteAddress().toString();
        logger.info("Client connected: {}", remoteAddress);
        
        // 创建游戏会话
        GameSession session = new GameSession(ctx.channel());
        sessionManager.addSession(session);
        
        // 将会话绑定到Channel
        ctx.channel().attr(GameSession.SESSION_KEY).set(session);
        
        super.channelActive(ctx);
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 客户端连接断开
        GameSession session = ctx.channel().attr(GameSession.SESSION_KEY).get();
        if (session != null) {
            logger.info("Client disconnected: {}, playerId: {}", 
                    ctx.channel().remoteAddress(), session.getPlayerId());
            
            // 移除会话
            sessionManager.removeSession(session.getSessionId());
        }
        
        super.channelInactive(ctx);
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {
        GameSession session = ctx.channel().attr(GameSession.SESSION_KEY).get();
        if (session == null) {
            logger.warn("Received message from unknown session: {}", ctx.channel().remoteAddress());
            ctx.close();
            return;
        }
        
        // 更新会话最后活跃时间
        session.updateLastActiveTime();
        
        // 处理心跳消息
        if (message.getType() == MessageType.HEARTBEAT) {
            handleHeartbeat(ctx, session, (HeartbeatMessage) message);
            return;
        }
        
        // 查找消息处理器
        MessageHandler handler = messageHandlers.get(message.getType());
        if (handler == null) {
            logger.warn("No handler found for message type: {}", message.getType());
            return;
        }
        
        try {
            // 处理消息
            handler.handle(session, message);
        } catch (Exception e) {
            logger.error("Error handling message type: {}, session: {}", 
                    message.getType(), session.getSessionId(), e);
        }
    }
    
    /**
     * 处理心跳消息
     */
    private void handleHeartbeat(ChannelHandlerContext ctx, GameSession session, HeartbeatMessage message) {
        logger.debug("Received heartbeat from session: {}", session.getSessionId());
        
        // 回复心跳
        HeartbeatMessage response = new HeartbeatMessage();
        response.setTimestamp(System.currentTimeMillis());
        session.sendMessage(response);
    }
    
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                // 读空闲，客户端可能已断开连接
                GameSession session = ctx.channel().attr(GameSession.SESSION_KEY).get();
                logger.warn("Client read idle timeout, closing connection. Session: {}", 
                        session != null ? session.getSessionId() : "unknown");
                ctx.close();
            }
        }
        super.userEventTriggered(ctx, evt);
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        GameSession session = ctx.channel().attr(GameSession.SESSION_KEY).get();
        logger.error("Exception in channel handler, session: {}", 
                session != null ? session.getSessionId() : "unknown", cause);
        
        // 关闭连接
        ctx.close();
    }
}
