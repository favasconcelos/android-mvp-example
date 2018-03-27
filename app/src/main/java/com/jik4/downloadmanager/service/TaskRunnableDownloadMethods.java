package com.jik4.downloadmanager.service;

import com.jik4.downloadmanager.database.model.Download;

/**
 * An interface that defines methods that DownloadTask implements. An instance of
 * DownloadTask passes itself to an PhotoDownloadRunnable instance through the
 * PhotoDownloadRunnable constructor, after which the two instances can access each other's
 * variables.
 */
interface TaskRunnableDownloadMethods {
    /**
     * Sets the Thread that this instance is running on
     *
     * @param currentThread the current Thread
     */
    void setCurrentThread(Thread currentThread);

    /**
     * Defines the actions for each state of the DownloadTask instance.
     *
     * @param state The current state of the task
     */
    void handleDownloadState(int state);

    /**
     * Gets the URL for the file being downloaded
     *
     * @return The image URL
     */
    Download getDownload();
}
