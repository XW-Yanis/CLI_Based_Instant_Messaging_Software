package service;

import common.Message;
import common.MessageType;

import java.io.*;
import java.util.Date;

/**
 * @author Xiang Weng
 * 用来传输文件的类
 */
public class FileService {


    // 可以指定接收方在哪里保存文件...不太合理
    public void sendFileTo(String sender, String receiver, String srcPath, String destPath) {
        Message msg = new Message();
        msg.setSender(sender);
        msg.setReceiver(receiver);
        msg.setMsgType(MessageType.SENDING_FILE);
        msg.setSendTime(new Date().toString());
        msg.setSrcPath(srcPath);
        msg.setDestPath(destPath);
        try {
            // 获取文件数据，设置msg内容
            File file = new File(srcPath);
            if (!file.exists()){
                System.out.println("读取文件失败，请检查文件是否存在。");
                return;
            }

            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            byte[] bytes = bis.readAllBytes();
            msg.setFileBytes(bytes);
            msg.setFileSize(bytes.length);
            System.out.println("我 发送 " +
                    msg.getSrcPath() + " 给 " + msg.getReceiver() +
                    " ,到 " + msg.getDestPath() + " 位置。");
            // 获取socket，并获取对应对象输出流
            ObjectOutputStream oos = new ObjectOutputStream
                    (ConnectionsManager.getConn(sender).getSocket().getOutputStream());
            oos.writeObject(msg);
            bis.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 只发送文件，让接收方自行决定放在哪
    public void sendFileToVersion2(String sender, String receiver, String srcPath) {
        Message msg = new Message();
        msg.setSender(sender);
        msg.setReceiver(receiver);
        msg.setMsgType(MessageType.SENDING_FILE);
        msg.setSendTime(new Date().toString());
        msg.setSrcPath(srcPath);
        try {
            // 获取文件数据，设置msg内容
            File file = new File(srcPath);
            if (!file.exists()){
                System.out.println("读取文件失败，请检查文件是否存在。");
                return;
            }

            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            byte[] bytes = bis.readAllBytes();
            msg.setFileBytes(bytes);
            msg.setFileSize(bytes.length);
            System.out.println("我 发送 " +
                    msg.getSrcPath() + " 给 " + msg.getReceiver());
            // 获取socket，并获取对应对象输出流
            ObjectOutputStream oos = new ObjectOutputStream
                    (ConnectionsManager.getConn(sender).getSocket().getOutputStream());
            oos.writeObject(msg);
            bis.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
