package teamzero.javaweb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamzero.javaweb.entity.ChatInfo;

public interface ChatInfoRepository  extends JpaRepository<ChatInfo, Integer> {

}
