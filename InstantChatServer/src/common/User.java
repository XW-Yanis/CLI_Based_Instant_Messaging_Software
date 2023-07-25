package common;

import java.io.Serializable;

/**
 * @author Xiang Weng
 */
public class User implements Serializable {
    private String id,pwd;
    private static final long serialVersionUID = 1L;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public User(String id, String pwd) {
        this.id = id;
        this.pwd = pwd;
    }
}
