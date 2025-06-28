package com.jeffrey.gameserver.network.codec;

import com.jeffrey.gameserver.protocol.Message;
import com.jeffrey.gameserver.protocol.MessageType;
import com.jeffrey.gameserver.protocol.messages.ChatMessage;
import com.jeffrey.gameserver.protocol.messages.HeartbeatMessage;
import com.jeffrey.gameserver.protocol.messages.LoginMessage;
import com.jeffrey.gameserver.util.JsonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 消息解码器
 * 将字节流解码为Message对象
 * 
 * @author jeffrey
 */
public class MessageDecoder extends MessageToMessageDecoder<ByteBuf> {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageDecoder.class);
    
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try {
            // 读取消息类型
            int typeCode = in.readInt();
            MessageType messageType = MessageType.fromCode(typeCode);
            
            if (messageType == null) {
                logger.warn("Unknown message type code: {}", typeCode);
                return;
            }
            
            // 读取消息内容长度
            int contentLength = in.readableBytes();
            if (contentLength <= 0) {
                logger.warn("Empty message content for type: {}", messageType);
                return;
            }
            
            // 读取消息内容
            byte[] contentBytes = new byte[contentLength];
            in.readBytes(contentBytes);
            String content = new String(contentBytes, StandardCharsets.UTF_8);
            
            // 根据消息类型解码
            Message message = decodeMessage(messageType, content);
            if (message != null) {
                out.add(message);
            }
            
        } catch (Exception e) {
            logger.error("Failed to decode message", e);
            // 不抛出异常，避免影响其他消息处理
        }
    }
    
    /**
     * 根据消息类型解码消息
     */
    private Message decodeMessage(MessageType messageType, String content) {
        try {
            switch (messageType) {
                case LOGIN:
                    return JsonUtil.fromJson(content, LoginMessage.class);
                case CHAT:
                    return JsonUtil.fromJson(content, ChatMessage.class);
                case HEARTBEAT:
                    return JsonUtil.fromJson(content, HeartbeatMessage.class);
                default:
                    logger.warn("Unsupported message type for decoding: {}", messageType);
                    return null;
            }
        } catch (Exception e) {
            logger.error("Failed to decode message content for type: {}, content: {}", 
                    messageType, content, e);
            return null;
        }
    }
}
