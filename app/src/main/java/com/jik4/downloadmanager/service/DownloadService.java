package com.jik4.downloadmanager.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.jik4.downloadmanager.R;
import com.jik4.downloadmanager.database.model.Download;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DownloadService extends Service {

    private static final int ONGOING_NOTIFICATION_ID = 1;

    // Status indicators
    static final int DOWNLOAD_FAILED = -1;
    static final int DOWNLOAD_STARTED = 1;
    static final int DOWNLOAD_UPDATED = 2;
    static final int TASK_COMPLETE = 3;
    // Sets the amount of time an idle thread will wait for a task before terminating
    private static final int KEEP_ALIVE_TIME = 1;
    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    // Sets the initial thread pool size to 8
    private static final int CORE_POOL_SIZE = 8;
    // Sets the maximum thread pool size to 8
    private static final int MAXIMUM_POOL_SIZE = 8;

    // A queue of Runnable for the download pool
    private LinkedBlockingQueue<Runnable> mDownloadWorkQueue;
    // A queue of DownloadTasks. Tasks are handed to a ThreadPool.
    private Queue<DownloadTask> mDownloadTaskWorkQueue;
    // A managed pool of background download threads
    private ThreadPoolExecutor mDownloadThreadPool;
    // An object that manages Messages in a Thread
    private Handler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        // Creates a work queue for the pool of Thread objects used for decoding, using a linked list queue that blocks when the queue is empty.
        mDownloadWorkQueue = new LinkedBlockingQueue<>();
        // Creates a work queue for the set of of task objects that control downloading and decoding, using a linked list queue that blocks when the queue is empty.
        mDownloadTaskWorkQueue = new LinkedBlockingQueue<>();
        // Creates a new pool of Thread objects for the download work queue
        mDownloadThreadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mDownloadWorkQueue);
        // Instantiates a new anonymous Handler object and defines its handleMessage() method.
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                // Gets the task from the incoming Message object.
                DownloadTask downloadTask = (DownloadTask) inputMessage.obj;
                // Chooses the action to take, based on the incoming message
                final int action = inputMessage.what;
                switch (action) {
                    // If the download has started, sets background color to dark green
                    case DOWNLOAD_STARTED:
                        // TODO: Update Download in database
                        Log.d("JIKA", "DownloadManager - DOWNLOAD_STARTED");
                        createOrUpdateNotification(downloadTask.getDownload(), action);
                        break;

                    case DOWNLOAD_UPDATED:
                        // TODO: Update Download in database
                        Log.d("JIKA", "DownloadManager - DOWNLOAD_UPDATED");
                        createOrUpdateNotification(downloadTask.getDownload(), action);
                        break;

                    // The download failed, sets the background color to dark red
                    case DOWNLOAD_FAILED:
                        // TODO: Update Download in database
                        Log.d("JIKA", "DownloadManager - DOWNLOAD_FAILED");
                        createOrUpdateNotification(downloadTask.getDownload(), action);
                        // Attempts to re-use the Task object
                        recycleTask(downloadTask);
                        break;

                    // The TASK is completed
                    case TASK_COMPLETE:
                        // TODO: Update Download in database
                        Log.d("JIKA", "DownloadManager - TASK_COMPLETE");
                        createOrUpdateNotification(downloadTask.getDownload(), action);
                        recycleTask(downloadTask);
                        break;

                    default:
                        // Otherwise, calls the super method
                        super.handleMessage(inputMessage);
                }
            }
        };
        createServiceNotification();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelAllTasks();
        cancelAllNotifications();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Download download = intent.getParcelableExtra(BRConstants.EXTRA_DATA_DOWNLOAD);
        startDownload(download);
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    private void initNotificationChannel() {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        NotificationChannel channel = new NotificationChannel("DOWNLOAD_MANAGER", "Download Manager", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
    }

    private void createServiceNotification() {
        initNotificationChannel();
        Notification notification = new NotificationCompat.Builder(this, "DOWNLOAD_MANAGER")
                .setContentTitle("Download Manager")
                .setContentText("Download manager service is running!")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();
        startForeground(ONGOING_NOTIFICATION_ID, notification);
    }

    private void cancelAllNotifications() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancelAll();
    }

    /**
     * Creates or updates a notification related to the download.
     */
    private void createOrUpdateNotification(Download download, int action) {
        initNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "DOWNLOAD_MANAGER")
                .setContentTitle("Download [" + download.getUrl() + "]")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        if (action == DownloadService.DOWNLOAD_FAILED) {
            builder.setContentText("Download FAILED!!");
            builder.setSmallIcon(android.R.drawable.presence_offline);
        } else if (action == DownloadService.TASK_COMPLETE) {
            builder.setContentText("Download finished...");
            builder.setSmallIcon(android.R.drawable.presence_invisible);
        } else {
            builder.setContentText("Downloading...");
            builder.setSmallIcon(android.R.drawable.presence_online);
        }
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // TODO: Check if the download ID is good enough to be used as the notification ID.
        // The +2 is because *1* is the service notification.
        manager.notify((int) download.getId() + 2, builder.build());
    }


    /**
     * Cancels all Threads in the ThreadPool
     */
    public void cancelAllTasks() {
        // Creates an array of tasks that's the same size as the task work queue
        DownloadTask[] taskArray = new DownloadTask[mDownloadWorkQueue.size()];
        // Populates the array with the task objects in the queue
        mDownloadWorkQueue.toArray(taskArray);
        // Stores the array length in order to iterate over the array
        int arrayLength = taskArray.length;
        /*
         * Locks on the singleton to ensure that other processes aren't mutating Threads, then
         * iterates over the array of tasks and interrupts the task's current Thread.
         */
        synchronized (this) {
            // Iterates over the array of tasks
            for (int taskArrayIndex = 0; taskArrayIndex < arrayLength; taskArrayIndex++) {
                // Gets the task's current thread
                Thread thread = taskArray[taskArrayIndex].getCurrentThread();
                // if the Thread exists, post an interrupt to it
                if (null != thread) {
                    thread.interrupt();
                }
            }
        }
        mDownloadWorkQueue.clear();
        mDownloadTaskWorkQueue.clear();
        mDownloadThreadPool.shutdownNow();
    }

    /**
     * Stops a download Thread and removes it from the thread pool
     *
     * @param downloadTask The download task associated with the Thread.
     * @param download     The URL being .
     */
    public void removeDownload(DownloadTask downloadTask, Download download) {
        if (downloadTask != null && downloadTask.getDownload().equals(download)) {
            // Locks on singleton to ensure that other processes aren't mutating Threads.
            synchronized (this) {
                // Gets the Thread that the downloader task is running on
                Thread thread = downloadTask.getCurrentThread();
                // If the Thread exists, posts an interrupt to it
                if (null != thread) {
                    thread.interrupt();
                }
            }
            /*
             * Removes the download Runnable from the ThreadPool. This opens a Thread in the
             * ThreadPool's work queue, allowing a task in the queue to start.
             */
            mDownloadThreadPool.remove(downloadTask.getDownloadRunnable());
        }
    }

    /**
     * Starts the download of the file.
     *
     * @param download The download object that contains all the needed information.
     * @return The task instance that will handle the work
     */
    public DownloadTask startDownload(Download download) {
        DownloadTask task = mDownloadTaskWorkQueue.poll();
        if (task == null) {
            task = new DownloadTask(this);
        }
        task.setDownload(download);
        mDownloadThreadPool.execute(task.getDownloadRunnable());
        return task;
    }

    /**
     * Handles state messages for a particular task object
     *
     * @param downloadTask A task object
     * @param state        The state of the task
     */
    public void handleState(DownloadTask downloadTask, int state) {
        switch (state) {
            // The task finished downloading
            case TASK_COMPLETE:
                // Gets a Message object, stores the state in it, and sends it to the Handler
                Message completeMessage = mHandler.obtainMessage(state, downloadTask);
                completeMessage.sendToTarget();
                break;
            // In all other cases, pass along the message without any other action.
            default:
                mHandler.obtainMessage(state, downloadTask).sendToTarget();
                break;
        }
    }

    /**
     * Recycles tasks by putting them back into the task queue.
     */
    private void recycleTask(DownloadTask downloadTask) {
        // Puts the task object back into the queue for re-use.
        mDownloadTaskWorkQueue.offer(downloadTask);
    }
}
