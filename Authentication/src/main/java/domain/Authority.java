package domain;

import javax.persistence.*;

/**
 * Created by yoon on 15. 8. 5..
 */
public class Authority {

    enum Type {
        Anonymous,
        User,
        Admin;
    }

    private Long id;
    private Type type;
}