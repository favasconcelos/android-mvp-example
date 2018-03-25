package com.jik4.downloadmanager.database.converter;


import android.arch.persistence.room.TypeConverter;

import com.jik4.downloadmanager.database.model.Download;

import static com.jik4.downloadmanager.database.model.Download.Status.ACTIVE;
import static com.jik4.downloadmanager.database.model.Download.Status.COMPLETED;
import static com.jik4.downloadmanager.database.model.Download.Status.INACTIVE;

public class StatusConverter {

    @TypeConverter
    public static Download.Status intToStatus(int status) {
        if (status == ACTIVE.getCode()) {
            return ACTIVE;
        } else if (status == INACTIVE.getCode()) {
            return INACTIVE;
        } else if (status == COMPLETED.getCode()) {
            return COMPLETED;
        } else {
            throw new IllegalArgumentException("Could not recognize status");
        }
    }

    @TypeConverter
    public static int statusToInt(Download.Status status) {
        return status.getCode();
    }
}
