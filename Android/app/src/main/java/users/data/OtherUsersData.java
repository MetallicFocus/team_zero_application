package users.data;

public class OtherUsersData {

    private String username = "";
    private String status = "";
    private String lastMessageContent = "";
    private String lastMessageTime = "";
    private String avatar = "";
    private String publicKey = "";

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastMessageContent() {
        return lastMessageContent;
    }

    public void setLastMessageContent(String lastMessageContent) {
        this.lastMessageContent = lastMessageContent;
    }

    public String getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(String lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPublicKey() { return publicKey; }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

}
