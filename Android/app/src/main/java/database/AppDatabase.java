package database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {StoredChatList.class, UsersOnDevice.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract StoredChatListDao storedChatListDao();
    public abstract UsersOnDeviceDao usersOnDeviceDao();
}