package com.jik4.downloadmanager.ui.main.completed;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.jik4.downloadmanager.database.AppDatabase;
import com.jik4.downloadmanager.database.model.Download;

import java.util.List;

public class CompletedListViewModel extends AndroidViewModel {

    private final LiveData<List<Download>> mList;

    public CompletedListViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getDatabase(this.getApplication());
        Download.Status[] status = {Download.Status.COMPLETED};
        mList = database.downloadDAO().listByStatus(status);
    }

    public LiveData<List<Download>> getCompletedList() {
        return mList;
    }
}
