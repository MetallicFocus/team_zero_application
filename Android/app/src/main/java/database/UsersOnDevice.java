package database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

@Entity
public class UsersOnDevice implements Serializable {

    @PrimaryKey
    @NonNull private String username;

    @ColumnInfo(name = "private_key")
    private String privateKey;

    /* *
     *
     * Getters and Setters
     *
     * */

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

}
