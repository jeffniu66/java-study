package com.jeffrey.gameserver.network;

import com.jeffrey.gameserver.config.ServerConfig;
import com.jeffrey.gameserver.handler.MessageHandler;
import com.jeffrey.gameserver.network.codec.MessageDecoder;
import com.jeffrey.gameserver.network.codec.MessageEncoder;
import com.jeffrey.gameserver.protocol.MessageType;
import com.jeffrey.gameserver.session.SessionManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Netty服务器
 * 
 * @author jeffrey
 */
public class NettyServer {
    
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
    
    private final ServerConfig config;
    private final SessionManager sessionManager;
    private final Map<MessageType, MessageHandler> messageHandlers;
    
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;
    
    public NettyServer(ServerConfig config, SessionManager sessionManager, 
                      Map<MessageType, MessageHandler> messageHandlers) {
        this.config = config;
        this.sessionManager = sessionManager;
        this.messageHandlers = messageHandlers;
    }
    
    /**
     * 启动服务器
     */
    public void start() throws InterruptedException {
        bossGroup = new NioEventLoopGroup(config.getBossThreads());
        workerGroup = new NioEventLoopGroup(config.getWorkerThreads());
        
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, config.getBacklog())
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .childOption(ChannelOption.TCP_NODELAY, config.isTcpNoDelay())
                    .childOption(ChannelOption.SO_KEEPALIVE, config.isKeepAlive())
                    .childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, config.getConnectTimeout() * 1000)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            
                            // 空闲检测
                            pipeline.addLast("idleStateHandler", 
                                    new IdleStateHandler(config.getHeartbeatInterval() * 2, 0, 0, TimeUnit.SECONDS));
                            
                            // 长度字段解码器 - 解决TCP粘包问题
                            pipeline.addLast("frameDecoder", 
                                    new LengthFieldBasedFrameDecoder(config.getMaxMessageLength(), 0, 4, 0, 4));
                            
                            // 长度字段编码器
                            pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                            
                            // 消息解码器
                            pipeline.addLast("messageDecoder", new MessageDecoder());
                            
                            // 消息编码器
                            pipeline.addLast("messageEncoder", new MessageEncoder());
                            
                            // 游戏业务处理器
                            pipeline.addLast("gameHandler", 
                                    new GameChannelHandler(sessionManager, messageHandlers));
                        }
                    });
            
            // 绑定端口并启动服务器
            ChannelFuture future = bootstrap.bind(config.getHost(), config.getPort()).sync();
            serverChannel = future.channel();
            
            logger.info("Netty server started on {}:{}", config.getHost(), config.getPort());
            
        } catch (Exception e) {
            logger.error("Failed to start Netty server", e);
            stop();
            throw e;
        }
    }
    
    /**
     * 停止服务器
     */
    public void stop() {
        logger.info("Stopping Netty server...");
        
        if (serverChannel != null) {
            serverChannel.close();
        }
        
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        
        logger.info("Netty server stopped.");
    }
    
    /**
     * 获取服务器配置
     */
    public ServerConfig getConfig() {
        return config;
    }
}
