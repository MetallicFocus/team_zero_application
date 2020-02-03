package database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

// Interface for the CRUD operations on UsersOnDevice table

@Dao
public interface UsersOnDeviceDao {

    @Query("SELECT * FROM usersondevice")
    List<UsersOnDevice> getAllUsersOnDevice();

    @Query("DELETE FROM usersondevice WHERE username LIKE :username")
    void deleteUserFromDevice(String username);

    @Query("SELECT private_key FROM usersondevice WHERE username LIKE :username")
    String getUserPrivateKey(String username);

    @Insert
    void insert(UsersOnDevice usersOnDevice);

    @Delete
    void delete(UsersOnDevice usersOnDevice);

    @Update
    void update(UsersOnDevice usersOnDevice);

}
