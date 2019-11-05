package teamzero.javaweb.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ChatInfo {

    @Id
    @Column(columnDefinition = "int(11) COMMENT 'chat id'")
    private Integer chatid;
    @Column(columnDefinition = "varchar(255) COMMENT 'list of member ids'")
    private String membersid;

    public Integer getChatid() {
        return chatid;
    }

    public void setChatid(Integer chatid) {
        this.chatid = chatid;
    }

    public String getMembersid() {
        return membersid;
    }

    public void setMembersid(String membersid) {
        this.membersid = membersid;
    }
}
