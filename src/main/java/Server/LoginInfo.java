package Server;

import java.io.Serializable;

public class LoginInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;

    public LoginInfo(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}