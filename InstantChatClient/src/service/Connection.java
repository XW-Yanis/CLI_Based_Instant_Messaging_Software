package service;

import common.Message;
import common.MessageType;
import utils.Utility;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author Xiang Weng
 * 该类是线程类，负责持有socket
 * 并且循环等待服务器的响应
 */
public class Connection extends Thread {
    private Socket socket;


    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    private boolean loop = true;

    public Connection(Socket socket) {
        this.socket = socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {

        while (loop) {
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                // 如果没有消息，线程会堵塞在这里
//                System.out.println("等待服务器响应...");
                Message msg = (Message) ois.readObject();
                // 这里判断服务器返回的msg类型并执行相应的业务流程
                switch (msg.getMsgType()) {
                    // 获取在线用户
                    case MessageType.REPLY_ONLINE_USERS:
                        String[] s = msg.getContent().split(" ");
                        System.out.println("\n=========当前在线=========");
                        for (String s1 : s) {
                            System.out.println("用户: " + s1);
                        }
                        break;
                    case MessageType.COMMON_MSG:
                        System.out.println("\n" + " [" + msg.getSendTime() + "] " +
                                msg.getSender() +
                                " 对我说: " + msg.getContent());
                        break;
                    case MessageType.ALL_MSG:
                        System.out.println("\n" + " [" + msg.getSendTime() + "] " +
                                msg.getSender() +
                                " 对全体说: " + msg.getContent());
                        break;
                    // 接收文件
                    case MessageType.SENDING_FILE:
                        System.out.println("\n收到 " + msg.getSender() +
                                " 给的 " + msg.getSrcPath() + " 文件。");
                        /*
                        更新： 查到了JOptionPane这个类，尝试一下。
                        暂时不知道怎么解决 线程争抢同一个输入流的问题。
                        主线程拿着输入流，Connection也想拿。
                        按下回车后程序卡住。
                         */
//                        String destPath = scanner.nextLine();
//                        scanner.close();
                        JFileChooser jFileChooser = new JFileChooser();
                        jFileChooser.setDialogTitle("选择存放路径");
                        jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                        if (jFileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                            // 输入文件名称
                            String fileName = JOptionPane.showInputDialog
                                    (null, "请输入文件名：", "保存文件",
                                            JOptionPane.PLAIN_MESSAGE);
                            // 判断文件名非空且非空
                            if (fileName != null && !fileName.trim().isEmpty()) {
                                // 拼接文件路径
                                File file = new File(jFileChooser.getSelectedFile(), fileName);
                                FileOutputStream fs = new FileOutputStream(file);
                                fs.write(msg.getFileBytes(), 0, msg.getFileSize());
                                fs.close();
                                System.out.println("保存完毕。");
                                JOptionPane.showMessageDialog(null, "文件保存成功！", "保存成功",
                                        JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(null, "文件名不能为空！", "保存失败",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        }
                        break;
                    // 接收广播
                    case MessageType.ADVERTISEMENT:
                        System.out.println("\n全体广播: " + msg.getContent());
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
