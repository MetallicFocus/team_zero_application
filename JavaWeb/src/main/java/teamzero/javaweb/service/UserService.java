package teamzero.javaweb.service;

import teamzero.javaweb.entity.User;
import teamzero.javaweb.entity.UserInfo;

public interface UserService {

    String signUp (UserInfo userInfo, User user);

    String signIn (String identifier, String pwd);
}
