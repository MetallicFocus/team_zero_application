package database;

import android.arch.persistence.room.Room;
import android.content.Context;

public class AppDatabaseClient {

    private Context mCtx;
    private static AppDatabaseClient mInstance;

    private AppDatabase appDatabase;

    private AppDatabaseClient(Context mCtx) {

        this.mCtx = mCtx;

        appDatabase = Room.databaseBuilder(mCtx, AppDatabase.class, "MyToDos").build();
    }

    public static synchronized AppDatabaseClient getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new AppDatabaseClient(mCtx);
        }
        return mInstance;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}