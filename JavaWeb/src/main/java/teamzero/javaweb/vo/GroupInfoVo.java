package teamzero.javaweb.vo;

import java.sql.Blob;
import java.util.List;

public class GroupInfoVo {

    private Integer group_id;
    private String group_name;
    private Integer number_of_members;
    private Blob group_avatar;
    private List<UserInfoVo> members;

    public GroupInfoVo(Integer group_id, String group_name, Integer number_of_members, Blob group_avatar, List<UserInfoVo> members) {
        this.group_id = group_id;
        this.group_name = group_name;
        this.number_of_members = number_of_members;
        this.group_avatar = group_avatar;
        this.members = members;
    }

    public Integer getGroup_id() {
        return group_id;
    }

    public void setGroup_id(Integer group_id) {
        this.group_id = group_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public Integer getNumber_of_members() {
        return number_of_members;
    }

    public void setNumber_of_members(Integer number_of_members) {
        this.number_of_members = number_of_members;
    }

    public Blob getGroup_avatar() {
        return group_avatar;
    }

    public void setGroup_avatar(Blob group_avatar) {
        this.group_avatar = group_avatar;
    }

    public List<UserInfoVo> getMembers() {
        return members;
    }

    public void setMembers(List<UserInfoVo> members) {
        this.members = members;
    }
}
