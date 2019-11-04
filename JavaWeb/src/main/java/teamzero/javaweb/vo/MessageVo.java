package teamzero.javaweb.vo;

import java.util.Date;

public class MessageVo {

    private Integer chatid;
    private Integer senderid;
    private Date datetime;
    private String content;

    public MessageVo(Integer chatid, Integer senderid, Date datetime, String content) {
        this.chatid = chatid;
        this.senderid = senderid;
        this.datetime = datetime;
        this.content = content;
    }

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
