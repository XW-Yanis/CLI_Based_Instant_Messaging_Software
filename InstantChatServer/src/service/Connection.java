package service;

import common.Message;
import common.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Xiang Weng
 * 该类是线程类，负责持有socket
 * 并且保持和某个用户的客户端的通讯
 */
public class Connection extends Thread {
    private Socket socket;
    private String userID;

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    private boolean loop = true;

    public Connection(Socket socket, String userID) {
        this.socket = socket;
        this.userID = userID;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        System.out.println(userID + " 上线了。");
        while (loop) {
            // 接发消息
            try {
                // 如果没有消息，线程会堵塞在这里
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message msg = (Message) ois.readObject();
                Message reply = new Message();
                switch (msg.getMsgType()) {
                    // 请求获取在线用户
                    case MessageType.REQUEST_ONLINE_USERS:
                        System.out.println(msg.getSender() + " 获取了在线用户列表。");
                        reply.setMsgType(MessageType.REPLY_ONLINE_USERS);
                        reply.setContent(ConnectionsManager.getOnlineUser());
                        // 将reply收信人 设 为 请求发起人
                        reply.setReceiver(msg.getSender());
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        oos.writeObject(reply);
                        break;
                    // 请求登出
                    case MessageType.LOGOUT:
                        System.out.println(msg.getSender() + " 登出了。");
                        ConnectionsManager.logout(msg.getSender());
                        break;
                    // 群聊
                    case MessageType.ALL_MSG:
                        System.out.println(msg.getSender() + " 对 全体 说了话。");
                        Set<String> userIDs = ConnectionsManager.getCons().keySet();
                        for (String user : userIDs) {
                            if (user.equals(msg.getSender())) continue;
                            ObjectOutputStream oos1 = new ObjectOutputStream
                                    (ConnectionsManager.getConn(user).getSocket().getOutputStream());
                            oos1.writeObject(msg);
                        }
                        break;
                    // 私聊
                    case MessageType.COMMON_MSG:
                        System.out.println(msg.getSender() + " 和 " + msg.getReceiver() + " 私聊。");
                        if (ConnectionsManager.getConn(msg.getReceiver()) != null) {
                            Socket targetSocket = ConnectionsManager
                                    .getConn(msg.getReceiver()).getSocket();
                            ObjectOutputStream targetOOS = new ObjectOutputStream(targetSocket.getOutputStream());
                            targetOOS.writeObject(msg);
                        } else {
                            // 如果用户离线
                            ConcurrentHashMap<String, List<Message>> msgs =
                                    Server.getDisconnected_msgs();
                            // 如果是第一次收到留言
                            if (msgs.get(msg.getReceiver()) == null) {
                                msgs.put(msg.getReceiver(), new ArrayList<>());
                            }
                            msgs.get(msg.getReceiver()).add(msg);
                        }
                        break;
                    // 发送文件
                    case MessageType.SENDING_FILE:
                        System.out.println(msg.getSender() + " 给 " + msg.getReceiver() + " 发文件。");
                        ObjectOutputStream oosForFile = new ObjectOutputStream
                                (ConnectionsManager.getConn(msg.getReceiver()).getSocket().getOutputStream());
                        oosForFile.writeObject(msg);
                        break;
                }

            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
