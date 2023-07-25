package service;

import common.Message;
import common.MessageType;
import common.User;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;

/**
 * @author Xiang Weng
 */
public class ChatService {
    // 群聊
    public void sendToAll(String sender, String content){
        Message msg = new Message();
        msg.setMsgType(MessageType.ALL_MSG);
        msg.setSender(sender);
        msg.setContent(content);
        msg.setSendTime(new Date().toString());
        System.out.println("我对全体说: " + content);
        Socket socket = ConnectionsManager.getConn(sender).getSocket();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    // 私聊
    public void sendTo(String sender, String receiver, String content){
        Message msg = new Message();
        msg.setMsgType(MessageType.COMMON_MSG);
        msg.setSender(sender);
        msg.setReceiver(receiver);
        msg.setContent(content);
        msg.setSendTime(new Date().toString());
        System.out.println("我对 " + receiver + " 说: " + content);
        Socket socket = ConnectionsManager.getConn(sender).getSocket();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
