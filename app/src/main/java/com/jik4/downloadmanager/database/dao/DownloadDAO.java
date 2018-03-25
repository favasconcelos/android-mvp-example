package com.jik4.downloadmanager.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.Update;

import com.jik4.downloadmanager.database.converter.StatusConverter;
import com.jik4.downloadmanager.database.model.Download;

import java.util.List;

@Dao
@TypeConverters({StatusConverter.class})
public interface DownloadDAO {

    @Query("SELECT * FROM download WHERE status IN (:status)")
    LiveData<List<Download>> listByStatus(Download.Status[] status);

    @Query("SELECT * FROM download WHERE id IN (:ids)")
    LiveData<List<Download>> listByIDs(int[] ids);

    @Query("SELECT * FROM download WHERE id = :id LIMIT 1")
    Download byID(int id);

    @Insert
    long insert(Download download);

    @Insert
    void insertAll(Download... downloads);

    @Delete
    void delete(Download download);

    @Update
    int update(Download download);

}
