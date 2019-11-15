package teamzero.javaweb.vo;

import java.util.List;

public class ChatInfoVo {

    private Integer chat_id;
    private List<UserInfoVo> members;
    private boolean group_flag;

    public ChatInfoVo(Integer chat_id, List<UserInfoVo> members, boolean group_flag) {
        this.chat_id = chat_id;
        this.members = members;
        this.group_flag = group_flag;
    }

    public boolean isGroup_flag() {
        return group_flag;
    }

    public void setGroup_flag(boolean group_flag) {
        this.group_flag = group_flag;
    }

    public Integer getChat_id() {
        return chat_id;
    }

    public void setChat_id(Integer chat_id) {
        this.chat_id = chat_id;
    }

    public List<UserInfoVo> getMembers() {
        return members;
    }

    public void setMembers(List<UserInfoVo> members) {
        this.members = members;
    }
}
