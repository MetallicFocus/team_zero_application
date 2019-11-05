package teamzero.javaweb.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import teamzero.javaweb.Utils.Encryption;
import teamzero.javaweb.entity.User;
import teamzero.javaweb.entity.UserInfo;
import teamzero.javaweb.repository.UserInfoRepository;
import teamzero.javaweb.repository.UserRepository;
import teamzero.javaweb.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    /* Todo: Check injection or illegal characters before update database. */
    public String signUp(UserInfo userInfo, User user) {
        if (userRepository.existsById(user.getUid()))
            return "User already existed.";

        userInfoRepository.saveAndFlush(userInfo);
        user.setPwd(Encryption.md5encrypt(user.getPwd()));
        userRepository.saveAndFlush(user);
        return "success";
    }

    public String signIn(String identifier, String pwd) {
        if (identifier.contains("@")) {
            /* Todo: Get connection with server by user email. */
        } else {
            /* Todo: Get connection with server by user name. */
        }
        return null;
    }
}
