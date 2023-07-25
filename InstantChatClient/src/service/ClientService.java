package service;

import common.Message;
import common.MessageType;
import common.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * @author Xiang Weng
 * 负责登录校验、注册等功能
 */
public class ClientService {

    private User user = new User();
    private Socket socket;

    // 根据id 和pwd去服务器验证
    public boolean checkUser(String id, String pwd) {
        boolean login = false;
        user.setId(id);
        user.setPwd(pwd);
        // 链接服务器
        try {
            socket = new Socket(InetAddress.getLocalHost(), 9999);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(user);
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message result = (Message) ois.readObject();
            if (result.getMsgType().equals(MessageType.LOGIN_SUCCEED)) {
                login = true;
                // 开启线程，用来维持通讯状态 -> 需要一个线程类
                Connection conn = new Connection(socket);
                conn.start();
                // 将线程放在集合里方便之后访问
                ConnectionsManager.add(user.getId(), conn);
            } else {
                // 登陆失败，关闭socket
                socket.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        //读取验证结果
        return login;
    }

    // 发送获取在线用户的请求
    public void getOnlineUsers() {
        Message message = new Message();
        message.setMsgType(MessageType.REQUEST_ONLINE_USERS);
        message.setSender(user.getId());

        // 将socket重新赋值（但我不确定是否必须。理论上来socket应该就是登录时的user的socket
        socket = ConnectionsManager.getConn(user.getId()).getSocket();

        try {
            // 将请求通过socket发送给服务器
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);
            // 读取服务器返回的message对象，这个已经在Connection里的run方法写过了，所以不需要在这里接收

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 发送登出请求
    public void logout() {
        Message message = new Message();
        message.setSender(user.getId());
        message.setMsgType(MessageType.LOGOUT);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);
            System.out.println("=========下次再见=========");
            System.exit(0);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
