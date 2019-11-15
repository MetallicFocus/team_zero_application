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

    @Query("DELETE FROM storedchatlist")
    void deleteAll();

    @Insert
    void insert(StoredChatList storedChatList);

    @Delete
    void delete(StoredChatList storedChatList);

    @Update
    void update(StoredChatList storedChatList);

}