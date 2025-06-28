# 游戏服务器框架

这是一个基于Java和Netty的游戏服务器框架，提供了完整的网络通信、消息处理、会话管理等功能。

## 功能特性

- **高性能网络通信**: 基于Netty NIO框架，支持高并发连接
- **消息协议**: 支持JSON格式的消息序列化，易于扩展
- **会话管理**: 完整的玩家会话生命周期管理
- **消息处理**: 可扩展的消息处理器架构
- **心跳检测**: 自动检测和清理超时连接
- **日志系统**: 完整的日志记录和文件输出

## 项目结构

```
game-server/
├── src/main/java/com/jeffrey/gameserver/
│   ├── GameServer.java                 # 主启动类
│   ├── config/
│   │   └── ServerConfig.java          # 服务器配置
│   ├── network/
│   │   ├── NettyServer.java           # Netty服务器
│   │   ├── GameChannelHandler.java    # 网络事件处理
│   │   └── codec/
│   │       ├── MessageDecoder.java    # 消息解码器
│   │       └── MessageEncoder.java    # 消息编码器
│   ├── protocol/
│   │   ├── Message.java               # 消息基类
│   │   ├── MessageType.java           # 消息类型枚举
│   │   └── messages/
│   │       ├── LoginMessage.java      # 登录消息
│   │       ├── ChatMessage.java       # 聊天消息
│   │       └── HeartbeatMessage.java  # 心跳消息
│   ├── session/
│   │   ├── GameSession.java           # 游戏会话
│   │   └── SessionManager.java        # 会话管理器
│   ├── handler/
│   │   ├── MessageHandler.java        # 消息处理器接口
│   │   ├── LoginHandler.java          # 登录处理器
│   │   └── ChatHandler.java           # 聊天处理器
│   └── util/
│       └── JsonUtil.java              # JSON工具类
├── src/main/resources/
│   └── logback.xml                    # 日志配置
└── src/test/java/
    └── TestClient.java                # 测试客户端
```

## 快速开始

### 1. 编译项目

```bash
cd game-server
mvn clean compile
```

### 2. 启动服务器

```bash
mvn exec:java -Dexec.mainClass="com.jeffrey.gameserver.GameServer"
```

或者直接运行主类：
```bash
java -cp target/classes:target/dependency/* com.jeffrey.gameserver.GameServer
```

### 3. 使用测试客户端

编译并运行测试客户端：
```bash
mvn test-compile exec:java -Dexec.mainClass="com.jeffrey.gameserver.TestClient"
```

### 4. 客户端命令

连接成功后，可以使用以下命令：

- `login <username> <password>` - 登录服务器
- `chat <message>` - 发送世界聊天消息
- `private <playerId> <message>` - 发送私聊消息
- `heartbeat` - 发送心跳
- `quit` - 断开连接

## 配置说明

### 服务器配置

可以通过系统属性配置服务器参数：

```bash
java -Dgame.server.host=0.0.0.0 -Dgame.server.port=9999 -cp ... com.jeffrey.gameserver.GameServer
```

支持的配置项：
- `game.server.host`: 服务器监听地址（默认：localhost）
- `game.server.port`: 服务器监听端口（默认：8888）

### 日志配置

日志文件输出到 `logs/` 目录：
- `game-server.log`: 所有日志
- `game-server-error.log`: 错误日志

## 消息协议

### 消息格式

所有消息都采用以下格式：
```
[4字节长度][4字节消息类型][JSON消息内容]
```

### 消息类型

- `1001`: 登录请求
- `1002`: 登录响应
- `2001`: 聊天消息
- `2002`: 聊天响应
- `9001`: 心跳消息

### 示例消息

登录消息：
```json
{
  "messageId": "1703123456789-1",
  "timestamp": 1703123456789,
  "username": "player1",
  "password": "123456",
  "clientVersion": "1.0.0"
}
```

聊天消息：
```json
{
  "messageId": "1703123456790-1",
  "timestamp": 1703123456790,
  "senderId": "player_player1",
  "senderName": "player1",
  "content": "Hello, world!",
  "channel": "WORLD"
}
```

## 扩展开发

### 添加新的消息类型

1. 在 `MessageType` 枚举中添加新类型
2. 创建对应的消息类继承 `Message`
3. 在 `MessageDecoder` 和 `MessageEncoder` 中添加处理逻辑
4. 创建对应的 `MessageHandler` 实现
5. 在 `GameServer` 中注册处理器

### 添加新的业务功能

1. 创建新的 `MessageHandler` 实现
2. 在 `GameServer.registerMessageHandlers()` 中注册
3. 根据需要扩展 `GameSession` 添加状态信息

## 性能特性

- 支持数万并发连接
- 基于NIO的异步网络处理
- 内存池化减少GC压力
- 可配置的线程池大小
- 自动的连接超时清理

## 注意事项

1. 默认配置适合开发环境，生产环境需要调整参数
2. 消息大小限制为1MB，可在配置中调整
3. 会话超时时间为5分钟，可根据需要调整
4. 建议在生产环境中使用数据库存储用户信息

## 许可证

MIT License
