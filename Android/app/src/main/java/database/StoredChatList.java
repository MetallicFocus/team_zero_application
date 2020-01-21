package database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class StoredChatList implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "username")
    private String username;

    @ColumnInfo(name = "last_message_content")
    private String lastMessageContent;

    @ColumnInfo(name = "public_key")
    private String publicKey;

    @ColumnInfo(name = "shared_secret_key")
    private String sharedSecretKey;

    @ColumnInfo(name = "chat_belongs_to")
    private String chatBelongsTo;

    /* *
     *
     * Getters and Setters
     *
     * */

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastMessageContent() {
        return lastMessageContent;
    }

    public void setLastMessageContent(String lastMessageContent) {
        this.lastMessageContent = lastMessageContent;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getSharedSecretKey() { return sharedSecretKey; }

    public void setSharedSecretKey(String sharedSecretKey) {
        this.sharedSecretKey = sharedSecretKey;
    }

    public String getChatBelongsTo() { return chatBelongsTo; }

    public void setChatBelongsTo(String chatBelongsTo) {
        this.chatBelongsTo = chatBelongsTo;
    }
}
