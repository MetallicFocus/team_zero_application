package teamzero.javaweb.vo;

import java.util.List;

public class UserInfoVo {

    private Integer uid;
    private String uname;
    private String email;
    private String avatar;
    private List<Integer> friendsid;
    private List<Integer> groupsid;
    private List<Integer> chatsid;

    public UserInfoVo(Integer uid, String uname, String email, String avatar, List<Integer> friendsid, List<Integer> groupsid, List<Integer> chatsid) {
        this.uid = uid;
        this.uname = uname;
        this.email = email;
        this.avatar = avatar;
        this.friendsid = friendsid;
        this.groupsid = groupsid;
        this.chatsid = chatsid;
    }

    public UserInfoVo(Integer uid, String uname, String email) {
        this.uid = uid;
        this.uname = uname;
        this.email = email;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public List<Integer> getFriendsid() {
        return friendsid;
    }

    public void setFriendsid(List<Integer> friendsid) {
        this.friendsid = friendsid;
    }

    public List<Integer> getGroupsid() {
        return groupsid;
    }

    public void setGroupsid(List<Integer> groupsid) {
        this.groupsid = groupsid;
    }

    public List<Integer> getChatsid() {
        return chatsid;
    }

    public void setChatsid(List<Integer> chatsid) {
        this.chatsid = chatsid;
    }
}
