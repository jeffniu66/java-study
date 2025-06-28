package com.jeffrey.gameserver.network.codec;

import com.jeffrey.gameserver.protocol.Message;
import com.jeffrey.gameserver.util.JsonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * 消息编码器
 * 将Message对象编码为字节流
 * 
 * @author jeffrey
 */
public class MessageEncoder extends MessageToByteEncoder<Message> {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageEncoder.class);
    
    @Override
    protected void encode(ChannelHandlerContext ctx, Message message, ByteBuf out) throws Exception {
        try {
            // 写入消息类型
            out.writeInt(message.getType().getCode());
            
            // 将消息对象序列化为JSON
            String content = JsonUtil.toJson(message);
            byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
            
            // 写入消息内容
            out.writeBytes(contentBytes);
            
            logger.debug("Encoded message: type={}, size={}", message.getType(), contentBytes.length);
            
        } catch (Exception e) {
            logger.error("Failed to encode message: {}", message, e);
            throw e;
        }
    }
}
