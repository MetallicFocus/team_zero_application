package teamzero.javaweb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamzero.javaweb.entity.UserInfo;

public interface UserInfoRepository extends JpaRepository<UserInfo,Integer> {
}
