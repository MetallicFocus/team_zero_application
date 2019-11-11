package teamzero.javaweb.vo;

import java.sql.Timestamp;

public class MessageVo {

    private Integer message_id;
    private Integer chat_id;
    private Integer sender_id;
    private Integer recipient_id;
    private Timestamp time_sent;
    private String content;
    private boolean group_flag;

    public MessageVo(Integer message_id, Integer chat_id, Integer sender_id, Integer recipient_id, Timestamp time_sent, String content, boolean group_flag) {
        this.message_id = message_id;
        this.chat_id = chat_id;
        this.sender_id = sender_id;
        this.recipient_id = recipient_id;
        this.time_sent = time_sent;
        this.content = content;
        this.group_flag = group_flag;
    }

    public Integer getMessage_id() {
        return message_id;
    }

    public void setMessage_id(Integer message_id) {
        this.message_id = message_id;
    }

    public Integer getChat_id() {
        return chat_id;
    }

    public void setChat_id(Integer chat_id) {
        this.chat_id = chat_id;
    }

    public Integer getSender_id() {
        return sender_id;
    }

    public void setSender_id(Integer sender_id) {
        this.sender_id = sender_id;
    }

    public Integer getRecipient_id() {
        return recipient_id;
    }

    public void setRecipient_id(Integer recipient_id) {
        this.recipient_id = recipient_id;
    }

    public Timestamp getTimesent() {
        return time_sent;
    }

    public void setTimesent(Timestamp time_sent) {
        this.time_sent = time_sent;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isGroup_flag() {
        return group_flag;
    }

    public void setGroup_flag(boolean group_flag) {
        this.group_flag = group_flag;
    }
}
