package teamzero.javaweb.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Message {

    @Id
    @Column(columnDefinition = "int(11) COMMENT 'chat id'")
    private Integer chatid;
    @Column(columnDefinition = "int(11) COMMENT 'sender id'")
    private Integer senderid;
    @Column(columnDefinition = "date COMMENT 'sent time of message'")
    private Date datetime;
    @Column(columnDefinition = "varchar(255) COMMENT 'content'")
    private String content;

    public Integer getChatid() {
        return chatid;
    }

    public void setChatid(Integer chatid) {
        this.chatid = chatid;
    }

    public Integer getSenderid() {
        return senderid;
    }

    public void setSenderid(Integer senderid) {
        this.senderid = senderid;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
