package teamzero.javaweb.service;

import teamzero.javaweb.vo.UserInfoVo;

public interface UserInfoService {

    //update user information
    Boolean updateUserInfo(Integer user_id, UserInfoVo userInfo);

    //get user information by id
    String getUserInfo(Integer user_id);

    //search for user by user name
    String searchUser(String user_name);
}
