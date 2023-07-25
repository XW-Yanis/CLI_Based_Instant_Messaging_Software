package common;

/**
 * @author Xiang Weng
 */
public interface MessageType {
    //登陆成功
    String LOGIN_SUCCEED = "1";
    //登录失败
    String LOGIN_FAILED = "2";
    // 普通消息
    String COMMON_MSG = "3";
    String ALL_MSG = "4";
    // 请求返回在线用户列表
    String REQUEST_ONLINE_USERS = "5";
    // 返回的在线用户列表
    String REPLY_ONLINE_USERS = "6";
    // 发送文件
    String SENDING_FILE = "7";
    // 接收文件
    // 登出系统
    String LOGOUT = "8";

    // 广播消息
    String ADVERTISEMENT = "9";



}
