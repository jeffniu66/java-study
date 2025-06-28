package com.jeffrey.gameserver.config;

/**
 * 服务器配置类
 * 
 * @author jeffrey
 */
public class ServerConfig {
    
    /** 服务器主机地址 */
    private String host = "localhost";
    
    /** 服务器端口 */
    private int port = 8888;
    
    /** Boss线程数 */
    private int bossThreads = 1;
    
    /** Worker线程数 */
    private int workerThreads = Runtime.getRuntime().availableProcessors() * 2;
    
    /** 连接超时时间(秒) */
    private int connectTimeout = 30;
    
    /** 心跳间隔(秒) */
    private int heartbeatInterval = 30;
    
    /** 最大连接数 */
    private int maxConnections = 10000;
    
    /** 消息最大长度 */
    private int maxMessageLength = 1024 * 1024; // 1MB
    
    /** 是否启用TCP_NODELAY */
    private boolean tcpNoDelay = true;
    
    /** 是否启用SO_KEEPALIVE */
    private boolean keepAlive = true;
    
    /** SO_BACKLOG大小 */
    private int backlog = 1024;
    
    public ServerConfig() {
        // 可以从配置文件或环境变量中读取配置
        loadFromEnvironment();
    }
    
    /**
     * 从环境变量加载配置
     */
    private void loadFromEnvironment() {
        String envHost = System.getProperty("game.server.host");
        if (envHost != null && !envHost.trim().isEmpty()) {
            this.host = envHost.trim();
        }
        
        String envPort = System.getProperty("game.server.port");
        if (envPort != null && !envPort.trim().isEmpty()) {
            try {
                this.port = Integer.parseInt(envPort.trim());
            } catch (NumberFormatException e) {
                // 使用默认端口
            }
        }
    }
    
    // Getter和Setter方法
    public String getHost() {
        return host;
    }
    
    public void setHost(String host) {
        this.host = host;
    }
    
    public int getPort() {
        return port;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public int getBossThreads() {
        return bossThreads;
    }
    
    public void setBossThreads(int bossThreads) {
        this.bossThreads = bossThreads;
    }
    
    public int getWorkerThreads() {
        return workerThreads;
    }
    
    public void setWorkerThreads(int workerThreads) {
        this.workerThreads = workerThreads;
    }
    
    public int getConnectTimeout() {
        return connectTimeout;
    }
    
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
    
    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }
    
    public void setHeartbeatInterval(int heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }
    
    public int getMaxConnections() {
        return maxConnections;
    }
    
    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }
    
    public int getMaxMessageLength() {
        return maxMessageLength;
    }
    
    public void setMaxMessageLength(int maxMessageLength) {
        this.maxMessageLength = maxMessageLength;
    }
    
    public boolean isTcpNoDelay() {
        return tcpNoDelay;
    }
    
    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }
    
    public boolean isKeepAlive() {
        return keepAlive;
    }
    
    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }
    
    public int getBacklog() {
        return backlog;
    }
    
    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }
    
    @Override
    public String toString() {
        return "ServerConfig{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", bossThreads=" + bossThreads +
                ", workerThreads=" + workerThreads +
                ", connectTimeout=" + connectTimeout +
                ", heartbeatInterval=" + heartbeatInterval +
                ", maxConnections=" + maxConnections +
                ", maxMessageLength=" + maxMessageLength +
                ", tcpNoDelay=" + tcpNoDelay +
                ", keepAlive=" + keepAlive +
                ", backlog=" + backlog +
                '}';
    }
}
