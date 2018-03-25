package com.jik4.downloadmanager.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.jik4.downloadmanager.database.AppDatabase;
import com.jik4.downloadmanager.database.dao.DownloadDAO;
import com.jik4.downloadmanager.database.model.Download;

import java.util.Date;

public class DownloadIntentService extends IntentService {

    private DownloadDAO mDao;

    public DownloadIntentService() {
        super("DownloadIntentService");
        mDao = AppDatabase.getDatabase(this.getApplication()).downloadDAO();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Download download = intent.getParcelableExtra(BRConstants.EXTRA_DATA_DOWNLOAD);
        download.setStatus(Download.Status.ACTIVE);
        download.setStartedAt(new Date());
        sendIntent(BRConstants.BR_DOWNLOAD_START, download);
        for (int i = 0; i < 100; i++) {
            if (i % 10 == 0) {
                sendIntent(BRConstants.BR_DOWNLOAD_PROGRESS, download, i);
            }
            try {
                // TODO: Remove this, only to "fake" download time
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
        download.setStatus(Download.Status.COMPLETED);
        download.setFinishedAt(new Date());
        sendIntent(BRConstants.BR_DOWNLOAD_END, download);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDao = null;
    }

    private void sendIntent(String action, Download download, int progress) {
        Intent intent = new Intent(action);
        intent.putExtra(BRConstants.EXTRA_DATA_DOWNLOAD, download);
        intent.putExtra(BRConstants.EXTRA_DATA_PROGRESS, progress);
        sendIntent(intent);
    }

    private void sendIntent(String action, Download download) {
        Intent intent = new Intent(action);
        intent.putExtra(BRConstants.EXTRA_DATA_DOWNLOAD, download);
        sendIntent(intent);
    }

    private void sendIntent(Intent intent) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
