package service;

import java.util.HashMap;

/**
 * @author Xiang Weng
 * 这个是客户端的线程管理类
 */
public class ConnectionsManager {
    // String 是用户id
    // Connection 是该经由该用户开启的线程
    private static HashMap<String, Connection> cons = new HashMap<>();

    public static void add(String id, Connection c){
        cons.put(id,c);
    }

    public static Connection getConn(String id){
        return cons.get(id);
    }

    public static HashMap<String, Connection> getCons() {
        return cons;
    }

    public static void setCons(HashMap<String, Connection> cons) {
        ConnectionsManager.cons = cons;
    }
}
