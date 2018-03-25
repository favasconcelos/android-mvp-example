package com.jik4.downloadmanager.ui.main.addurl;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.jik4.downloadmanager.database.AppDatabase;
import com.jik4.downloadmanager.database.model.Download;

public class AddURLViewModel extends AndroidViewModel {

    private AppDatabase mDatabase;

    public AddURLViewModel(@NonNull Application application) {
        super(application);
        mDatabase = AppDatabase.getDatabase(this.getApplication());
    }

    public void insert(final Download download, OnDataInsertCallback callback) {
        new addAsyncTask(mDatabase, callback).execute(download);
    }

    private static class addAsyncTask extends AsyncTask<Download, Download, Download> {

        private AppDatabase mDatabase;
        private OnDataInsertCallback mCallback;

        addAsyncTask(AppDatabase database, OnDataInsertCallback callback) {
            this.mDatabase = database;
            this.mCallback = callback;
        }

        @Override
        protected Download doInBackground(final Download... params) {
            Download download = params[0];
            long id = this.mDatabase.downloadDAO().insert(download);
            download.setId(id);
            return download;
        }

        @Override
        protected void onPostExecute(Download download) {
            if (mCallback != null) {
                mCallback.onDataInserted(download);
            }
        }
    }

    interface OnDataInsertCallback {
        void onDataInserted(Download download);
    }

}
