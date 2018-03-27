package com.jik4.downloadmanager.service;

import com.jik4.downloadmanager.database.model.Download;

public class DownloadTask implements TaskRunnableDownloadMethods {

    // The download object to be downloaded
    private Download mDownload;
    // The Thread on which this task is currently running.
    private Thread mCurrentThread;
    // Fields containing references to the two runnable objects that handle downloading and decoding of the file.
    private Runnable mDownloadRunnable;
    // An object that contains the ThreadPool singleton.
    private DownloadService mDownloadService;

    DownloadTask(DownloadService downloadService) {
        mDownloadService = downloadService;
        // Create the runnable
        mDownloadRunnable = new DownloadRunnable(this);
    }

    public void setDownload(Download download) {
        mDownload = download;
    }

    @Override
    public Download getDownload() {
        return mDownload;
    }

    /**
     * Returns the instance that downloaded the file.
     */
    Runnable getDownloadRunnable() {
        return mDownloadRunnable;
    }

    // Passes the download state to the ThreadPool object.
    @Override
    public void handleDownloadState(int state) {
        int outState;
        // Converts the download state to the overall state
        switch (state) {
            case DownloadRunnable.HTTP_STATE_STARTED:
                outState = DownloadService.DOWNLOAD_STARTED;
                break;
            case DownloadRunnable.HTTP_STATE_FAILED:
                outState = DownloadService.DOWNLOAD_FAILED;
                break;
            case DownloadRunnable.HTTP_STATE_COMPLETED:
                outState = DownloadService.TASK_COMPLETE;
                break;
            default:
                outState = state;
                break;
        }
        // Passes the state to the ThreadPool object.
        mDownloadService.handleState(this, outState);
    }

    /*
     * Returns the Thread that this Task is running on. The method must first get a lock, in this case the ThreadPool singleton. The lock is needed because the
     * Thread object reference is stored in the Thread object itself, and that object can be
     * changed by processes outside of this app.
     */
    public Thread getCurrentThread() {
        synchronized (mDownloadService) {
            return mCurrentThread;
        }
    }

    /*
     * Sets the identifier for the current Thread. This must be a synchronized operation; see the
     * notes for getCurrentThread()
     */
    @Override
    public void setCurrentThread(Thread thread) {
        synchronized (mDownloadService) {
            mCurrentThread = thread;
        }
    }
}
