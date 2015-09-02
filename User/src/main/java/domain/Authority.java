package domain;

import javax.persistence.*;

/**
 * Created by yoon on 15. 9. 1..
 */
@Entity
@Table
public class Authority {

    enum Type {
        Anonymous,
        User,
        Admin;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;
}