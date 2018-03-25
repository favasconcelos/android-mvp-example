package com.jik4.downloadmanager.ui.main.active;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.jik4.downloadmanager.database.AppDatabase;
import com.jik4.downloadmanager.database.model.Download;

import java.util.List;

public class ActiveListViewModel extends AndroidViewModel {

    private final LiveData<List<Download>> mActiveList;

    public ActiveListViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getDatabase(this.getApplication());
        Download.Status[] status = {Download.Status.ACTIVE, Download.Status.INACTIVE};
        mActiveList = database.downloadDAO().listByStatus(status);
    }

    public LiveData<List<Download>> getActiveList() {
        return mActiveList;
    }
}
