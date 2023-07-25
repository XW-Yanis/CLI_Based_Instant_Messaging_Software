package service;

import common.Message;
import common.MessageType;
import common.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Xiang Weng
 * 服务器类，负责监听9999，并在连接开始后启动线程
 */
public class Server {
    private ServerSocket ss;

    private static Map<String, User> validUsers = new HashMap<>();

    public static ConcurrentHashMap<String, List<Message>> getDisconnected_msgs() {
        return disconnected_msgs;
    }

    private static ConcurrentHashMap<String, List<Message>> disconnected_msgs =
            new ConcurrentHashMap<>();


    static {
        validUsers.put("100", new User("100", "qq"));
        validUsers.put("200", new User("200", "qq"));
        validUsers.put("300", new User("300", "qq"));
        validUsers.put("至尊宝", new User("至尊宝", "qq"));
        validUsers.put("紫霞", new User("紫霞", "qq"));
        validUsers.put("菩提老祖", new User("菩提老祖", "qq"));
    }

    private boolean verify(String id, String pwd) {
        User user = validUsers.get(id);
        if (user == null) {
            System.out.println("查无此人。");
            return false;
        }
        if (!user.getPwd().equals(pwd)) {
            System.out.println("密码不对。");
            return false;
        }
        return true;
    }

    public Server() {
        System.out.println("服务器启动，端口9999...");
        new Advertise().start();
        try {
            ss = new ServerSocket(9999);
            // 循环监听
            while (true) {
                Socket socket = ss.accept();
                // 拿到user对象
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                User user = (User) ois.readObject();
                Message message = new Message();
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

                // 验证
                if (verify(user.getId(), user.getPwd())) {
                    // 登陆成功
                    message.setMsgType(MessageType.LOGIN_SUCCEED);
                    // 创建线程，保持通讯
                    Connection conn = new Connection(socket, user.getId());
                    conn.start();
                    // 加到集合方便访问
                    ConnectionsManager.add(user.getId(), conn);
                    // 将信息封装然后回复
                    oos.writeObject(message);
                } else {
                    // 失败
                    message.setMsgType(MessageType.LOGIN_FAILED);
                    System.out.println(user.getId() + " " + user.getPwd() + " 验证失败。");
                    // 将信息封装然后回复
                    oos.writeObject(message);
                    socket.close();
                }
                // 发送离线消息
                if (disconnected_msgs.get(user.getId()) != null) {
                    List<Message> messages = disconnected_msgs.get(user.getId());
                    for (Message msg : messages) {
                        // 如果不创建新的流，就无法区分Message对象之间的间隔，从而导致
                        // 一个流里多个Message混在一起，引发流异常。我也尝试使用flush方法刷新流
                        // 但没有用。下面是跟chatGPT要的解释。
                        oos = new ObjectOutputStream(socket.getOutputStream());
                        oos.writeObject(msg);

                        /*
                        所以一个流可以理解为一个管道，当多个不同类型的Message对象混在一起，就好像可乐雪碧王老吉混在一起，无法在管道的另一头将他们分离。从而引发流异常?
ChatGPT
Yes, that's a good analogy! Think of a stream as a pipeline or a channel through which data (in this case, objects) flows. When you mix different types of objects (e.g., different types of Message objects) in the same stream, it becomes difficult to distinguish one type from another when reading them on the receiving end.

Just like mixing different drinks (cola, Sprite, Wong Lo Kat) in a single bottle, it becomes challenging to identify and separate each type of drink when pouring it out. Similarly, when reading from a stream, the receiving end needs to know the exact structure and type of the data being read to interpret it correctly.

When you create a new ObjectOutputStream for each Message object, it ensures that each object is written separately in its own "pipeline," and the receiving end can process them without any confusion or mixing of different types. This way, you avoid the issues with stream headers being corrupted or encountering stream mismatch errors.
                         */
                    }
                    // 将离线消息删除
                    disconnected_msgs.remove(user.getId());
                }
//                这个不能放这，不然在else块里的socket关闭了就没法将MSG对象传输过去了
//                oos.writeObject(message);
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            // 如果退出了while，说明服务器关闭了，释放资源。
            try {
                ss.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
