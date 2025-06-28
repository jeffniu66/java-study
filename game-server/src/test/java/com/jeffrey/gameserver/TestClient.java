package com.jeffrey.gameserver;

import com.jeffrey.gameserver.protocol.messages.ChatMessage;
import com.jeffrey.gameserver.protocol.messages.HeartbeatMessage;
import com.jeffrey.gameserver.protocol.messages.LoginMessage;
import com.jeffrey.gameserver.util.JsonUtil;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * 测试客户端
 * 用于测试游戏服务器功能
 * 
 * @author jeffrey
 */
public class TestClient {
    
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8888;
    
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private boolean running = false;
    
    public static void main(String[] args) {
        TestClient client = new TestClient();
        client.start();
    }
    
    public void start() {
        try {
            // 连接服务器
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            running = true;
            
            System.out.println("Connected to game server: " + SERVER_HOST + ":" + SERVER_PORT);
            
            // 启动消息接收线程
            Thread receiveThread = new Thread(this::receiveMessages);
            receiveThread.setDaemon(true);
            receiveThread.start();
            
            // 主线程处理用户输入
            handleUserInput();
            
        } catch (IOException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
        } finally {
            disconnect();
        }
    }
    
    private void handleUserInput() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\n=== Game Client Commands ===");
        System.out.println("login <username> <password> - Login to server");
        System.out.println("chat <message> - Send world chat message");
        System.out.println("private <playerId> <message> - Send private message");
        System.out.println("heartbeat - Send heartbeat");
        System.out.println("quit - Disconnect from server");
        System.out.println("=============================\n");
        
        while (running && scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                continue;
            }
            
            String[] parts = input.split("\\s+", 3);
            String command = parts[0].toLowerCase();
            
            try {
                switch (command) {
                    case "login":
                        if (parts.length >= 3) {
                            sendLoginMessage(parts[1], parts[2]);
                        } else {
                            System.out.println("Usage: login <username> <password>");
                        }
                        break;
                        
                    case "chat":
                        if (parts.length >= 2) {
                            sendChatMessage(input.substring(5)); // 去掉"chat "
                        } else {
                            System.out.println("Usage: chat <message>");
                        }
                        break;
                        
                    case "private":
                        if (parts.length >= 3) {
                            sendPrivateMessage(parts[1], input.substring(8 + parts[1].length())); // 去掉"private <playerId> "
                        } else {
                            System.out.println("Usage: private <playerId> <message>");
                        }
                        break;
                        
                    case "heartbeat":
                        sendHeartbeat();
                        break;
                        
                    case "quit":
                        running = false;
                        break;
                        
                    default:
                        System.out.println("Unknown command: " + command);
                }
            } catch (Exception e) {
                System.err.println("Error executing command: " + e.getMessage());
            }
        }
    }
    
    private void sendLoginMessage(String username, String password) throws IOException {
        LoginMessage loginMessage = new LoginMessage(username, password);
        loginMessage.setClientVersion("1.0.0");
        sendMessage(loginMessage.getType().getCode(), JsonUtil.toJson(loginMessage));
        System.out.println("Login request sent: " + username);
    }
    
    private void sendChatMessage(String content) throws IOException {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(content);
        chatMessage.setChannel(ChatMessage.ChatChannel.WORLD);
        sendMessage(chatMessage.getType().getCode(), JsonUtil.toJson(chatMessage));
        System.out.println("Chat message sent: " + content);
    }
    
    private void sendPrivateMessage(String receiverId, String content) throws IOException {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(content);
        chatMessage.setReceiverId(receiverId);
        chatMessage.setChannel(ChatMessage.ChatChannel.PRIVATE);
        sendMessage(chatMessage.getType().getCode(), JsonUtil.toJson(chatMessage));
        System.out.println("Private message sent to " + receiverId + ": " + content);
    }
    
    private void sendHeartbeat() throws IOException {
        HeartbeatMessage heartbeat = new HeartbeatMessage();
        sendMessage(heartbeat.getType().getCode(), JsonUtil.toJson(heartbeat));
        System.out.println("Heartbeat sent");
    }
    
    private void sendMessage(int messageType, String content) throws IOException {
        byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
        int totalLength = 4 + contentBytes.length; // 4字节消息类型 + 内容长度
        
        // 发送长度前缀
        out.writeInt(totalLength);
        // 发送消息类型
        out.writeInt(messageType);
        // 发送消息内容
        out.write(contentBytes);
        out.flush();
    }
    
    private void receiveMessages() {
        try {
            while (running && !socket.isClosed()) {
                // 读取消息长度
                int messageLength = in.readInt();
                if (messageLength <= 0) {
                    continue;
                }
                
                // 读取消息类型
                int messageType = in.readInt();
                
                // 读取消息内容
                int contentLength = messageLength - 4; // 减去消息类型的4字节
                byte[] contentBytes = new byte[contentLength];
                in.readFully(contentBytes);
                String content = new String(contentBytes, StandardCharsets.UTF_8);
                
                System.out.println("\n[Server] Type: " + messageType + ", Content: " + content);
                System.out.print("> ");
            }
        } catch (IOException e) {
            if (running) {
                System.err.println("Error receiving message: " + e.getMessage());
            }
        }
    }
    
    private void disconnect() {
        running = false;
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
        System.out.println("Disconnected from server");
    }
}
