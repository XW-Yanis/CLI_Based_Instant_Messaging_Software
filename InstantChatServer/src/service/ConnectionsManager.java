package service;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Set;

/**
 * @author Xiang Weng
 * 服务器端的线程管理类
 */
public class ConnectionsManager {
    // String 是用户id
    // Connection 是该经由该用户开启的线程
    private static HashMap<String, Connection> cons = new HashMap<>();

    public static void add(String id, Connection c) {
        cons.put(id, c);
    }

    public static Connection getConn(String id) {
        return cons.get(id);
    }

    public static HashMap<String, Connection> getCons() {
        return cons;
    }

    public static void setCons(HashMap<String, Connection> cons) {
        ConnectionsManager.cons = cons;
    }

    public static String getOnlineUser() {
        Set<String> strings = cons.keySet();
        String onlineUser = "";
        for (String s : strings) {
            onlineUser += s + " ";
        }
        return onlineUser;
    }

    public static void logout(String id) {
        Connection conn = cons.get(id);
        Socket socket = conn.getSocket();
        try {
            conn.setLoop(false);
            if (socket.isConnected()) {
                socket.close();
            }
            cons.remove(id);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
