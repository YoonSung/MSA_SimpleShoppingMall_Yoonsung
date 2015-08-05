package repository;

import domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by yoon on 15. 8. 5..
 */
public interface UserRepository extends JpaRepository<User, Long> {
}
