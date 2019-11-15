package teamzero.javaweb.vo;

import java.sql.Blob;
import java.util.List;

public class UserInfoVo {

    private Integer user_id;
    private String user_name;
    private String email;
    private Blob avatar;
    private List<UserInfoVo> friends;
    private List<GroupInfoVo> groups;
    private List<ChatInfoVo> chats;

    public UserInfoVo(Integer user_id, String user_name, String email, Blob avatar, List<UserInfoVo> friends, List<GroupInfoVo> groups, List<ChatInfoVo> chats) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.email = email;
        this.avatar = avatar;
        this.friends = friends;
        this.groups = groups;
        this.chats = chats;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Blob getAvatar() {
        return avatar;
    }

    public void setAvatar(Blob avatar) {
        this.avatar = avatar;
    }

    public List<UserInfoVo> getFriends() {
        return friends;
    }

    public void setFriends(List<UserInfoVo> friends) {
        this.friends = friends;
    }

    public List<GroupInfoVo> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupInfoVo> groups) {
        this.groups = groups;
    }

    public List<ChatInfoVo> getChats() {
        return chats;
    }

    public void setChats(List<ChatInfoVo> chats) {
        this.chats = chats;
    }
}
