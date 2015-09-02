package repository;

import domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by yoon on 15. 9. 1..
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
