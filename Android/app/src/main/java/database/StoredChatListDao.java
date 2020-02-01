package database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

// Interface for the CRUD operations on StoredChatList table

@Dao
public interface StoredChatListDao {

    @Query("SELECT * FROM storedchatlist")
    List<StoredChatList> getAll();

    @Query("SELECT * FROM storedchatlist WHERE chat_belongs_to LIKE :username")
    List<StoredChatList> getChatsForClient(String username);

    @Query("SELECT * FROM storedchatlist WHERE username LIKE :userToSearchFor AND chat_belongs_to LIKE :myself")
    List<StoredChatList> getUserFromChatList(String userToSearchFor, String myself);

    @Query("DELETE FROM storedchatlist")
    void deleteAll();

    @Query("DELETE FROM storedchatlist WHERE chat_belongs_to LIKE :username")
    void deleteAllChatsForClient(String username);

    @Insert
    void insert(StoredChatList storedChatList);

    @Delete
    void delete(StoredChatList storedChatList);

    @Update
    void update(StoredChatList storedChatList);

}