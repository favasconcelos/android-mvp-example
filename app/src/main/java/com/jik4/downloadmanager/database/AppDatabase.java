package com.jik4.downloadmanager.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.jik4.downloadmanager.database.dao.DownloadDAO;
import com.jik4.downloadmanager.database.model.Download;

@Database(entities = {Download.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase sInstance;

    public static AppDatabase getDatabase(Context context) {
        if (sInstance == null) {
            sInstance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "download_manager_db").build();
        }
        return sInstance;
    }

    public abstract DownloadDAO downloadDAO();

}
