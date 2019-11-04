package teamzero.javaweb.vo;

import java.util.List;

public class ChatInfoVo {

    private Integer chatid;
    private List<Integer> membersid;

    public ChatInfoVo(Integer chatid, List<Integer> membersid) {
        this.chatid = chatid;
        this.membersid = membersid;
    }

    public Integer getChatid() {
        return chatid;
    }

    public void setChatid(Integer chatid) {
        this.chatid = chatid;
    }

    public List<Integer> getMembersid() {
        return membersid;
    }

    public void setMembersid(List<Integer> membersid) {
        this.membersid = membersid;
    }
}
