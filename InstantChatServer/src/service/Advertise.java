package service;

import common.Message;
import common.MessageType;
import utils.Utility;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.Scanner;
import java.util.Set;

/**
 * @author Xiang Weng
 */
public class Advertise extends Thread {

    @Override
    public void run() {
        while (true) {
            System.out.println("广播信息(q 退出): ");
            String s = Utility.readString(50);
            if (s.equalsIgnoreCase("q")) break;
            Message msg = new Message();
            msg.setSender("Server");
            msg.setMsgType(MessageType.ADVERTISEMENT);
            msg.setContent(s);
            msg.setSendTime(new Date().toString());
            System.out.println("Server 对 全体 广播: " + s);
            Set<String> users = ConnectionsManager.getCons().keySet();
            if (users.size() != 0) {
                for (String user : users) {
                    try {
                        ObjectOutputStream oos = new ObjectOutputStream
                                (ConnectionsManager.getConn(user).getSocket().getOutputStream());
                        oos.writeObject(msg);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

            } else {
                System.out.println("好像没人在线，过会儿再试试吧...");
            }
        }
        System.out.println("广播已退出。");
    }
}