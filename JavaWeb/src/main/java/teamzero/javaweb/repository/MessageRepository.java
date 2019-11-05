package teamzero.javaweb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamzero.javaweb.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Integer> {
}
