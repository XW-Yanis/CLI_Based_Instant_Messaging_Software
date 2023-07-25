package ui;

import service.ChatService;
import service.ClientService;
import service.FileService;
import utils.Utility;

/**
 * @author Xiang Weng
 */
public class View {
    private boolean loop = true; //控制显示菜单
    private String input = "";
    private ClientService cs = new ClientService();
    private ChatService chatS = new ChatService();
    private FileService fs = new FileService();

    public static void main(String[] args) {
        new View().show();
    }

    private void show() {
        while (loop) {
            System.out.println("=========欢迎使用=========");
            System.out.println("\t\t 1 登陆系统");
            System.out.println("\t\t 9 退出系统");
            System.out.print("输入选择: ");
            input = Utility.readString(1);

            switch (input) {
                case "1":
//                    System.out.println("登录。。。");
                    System.out.print("用户名: ");
                    String id = Utility.readString(50);
                    System.out.print("密 码: ");
                    String pwd = Utility.readString(50);
                    String content;
                    // 开始登陆验证
                    if (cs.checkUser(id, pwd)) {
                        while (loop) {
                            System.out.println("=========当前用户(" + id + ")=========");
                            System.out.println("\t\t 1 显示在线用户");
                            System.out.println("\t\t 2 群发消息");
                            System.out.println("\t\t 3 私聊消息");
                            System.out.println("\t\t 4 发送文件");
                            System.out.println("\t\t 9 退出系统");
                            System.out.print("输入选择: ");
                            input = Utility.readString(1);
                            switch (input) {
                                case "1":
//                                    System.out.println("显示在线用户");
                                    cs.getOnlineUsers();
                                    break;
                                case "2":
//                                    System.out.println("群发");
                                    System.out.print("内容: ");
                                    content = Utility.readString(100);
                                    chatS.sendToAll(id, content);
                                    break;
                                case "3":
                                    // 私聊
                                    System.out.print("收信人: ");
                                    String receiver = Utility.readString(50);
                                    System.out.print("内容: ");
                                    content = Utility.readString(100);
                                    chatS.sendTo(id, receiver, content);
                                    break;
                                case "4":
//                                    System.out.println("发文件");
                                    System.out.print("收件人(需在线): ");
                                    String fileReceiver = Utility.readString(50);
                                    System.out.print("文件绝对路径(d:\\xx.mp4): ");
                                    String srcPath = Utility.readString(100);
//                                    System.out.print("接收方的绝对路径(e:\\xx.mp4): ");
//                                    String destPath = Utility.readString(100);
                                    fs.sendFileToVersion2(id, fileReceiver, srcPath);

                                    break;
                                case "9":
                                    loop = false;
                                    cs.logout();
                                    break;
                            }
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    } else {
                        System.out.println("用户名或密码错误。");
                    }
                    break;
                case "9":
//                    System.out.println("退出。。。");
                    loop = false;
                    break;
            }
        }
    }

}
