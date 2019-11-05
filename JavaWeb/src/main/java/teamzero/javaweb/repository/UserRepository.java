package teamzero.javaweb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamzero.javaweb.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
}
