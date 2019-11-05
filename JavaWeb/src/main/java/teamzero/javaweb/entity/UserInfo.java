package teamzero.javaweb.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class UserInfo {

    @Id
    @Column(columnDefinition = "int(11) COMMENT 'user id'")
    private Integer uid;
    @Column(columnDefinition = "varchar(255) COMMENT 'user name'")
    private String uname;
    @Column(columnDefinition = "varchar(255) COMMENT 'user email'")
    private String email;
    /* Todo: specify how to store an img */
    @Column(columnDefinition = "varchar(255) COMMENT 'avatar'")
    private String avatar;
    @Column(columnDefinition = "varchar(255) COMMENT 'list of friend ids'")
    private String friendsid;
    @Column(columnDefinition = "varchar(255) COMMENT 'list of group ids'")
    private String groupsid;
    @Column(columnDefinition = "varchar(255) COMMENT 'list of chat ids'")
    private String chatsid;

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

    public String getFriendsid() {
        return friendsid;
    }

    public void setFriendsid(String friendsid) {
        this.friendsid = friendsid;
    }

    public String getGroupsid() {
        return groupsid;
    }

    public void setGroupsid(String groupsid) {
        this.groupsid = groupsid;
    }

    public String getChatsid() {
        return chatsid;
    }

    public void setChatsid(String chatsid) {
        this.chatsid = chatsid;
    }
}
