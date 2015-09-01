package domain;

/**
 * Created by yoon on 15. 9. 1..
 */
public class Authentication {

    private final String password;
    private final String id;

    public Authentication(String id, String password) {
        this.id = id;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getId() {
        return id;
    }
}
