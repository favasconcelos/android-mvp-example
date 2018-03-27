package com.jik4.downloadmanager.service;

import android.os.Process;
import android.util.Log;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadRunnable implements Runnable {

    // Sets the size for each read action (bytes)
    private static final int READ_SIZE = 1024 * 2;

    // Constants for indicating the state of the download
    static final int HTTP_STATE_FAILED = -1;
    static final int HTTP_STATE_STARTED = 0;
    static final int HTTP_STATE_COMPLETED = 1;

    // Defines a field that contains the calling object of type DownloadTask.
    private final TaskRunnableDownloadMethods mDownloadTask;

    /**
     * This constructor creates an instance of DownloadRunnable and stores in it a reference
     * to the DownloadTask instance that instantiated it.
     */
    DownloadRunnable(TaskRunnableDownloadMethods downloadTask) {
        mDownloadTask = downloadTask;
    }

    @Override
    public void run() {
        // Stores the current Thread in the the DownloadTask instance, so that the instance can interrupt the Thread.
        mDownloadTask.setCurrentThread(Thread.currentThread());
        // Moves the current Thread into the background
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        byte[] byteBuffer = null;
        // Try block that downloads from a URL. The URL value is in the field DownloadTask.mURL
        try {
            // Before continuing, checks to see that the Thread hasn't been interrupted
            checkInterrupt();
            // Calls the DownloadTask implementation of {@link #handleDownloadState} to set the state of the download
            mDownloadTask.handleDownloadState(HTTP_STATE_STARTED);
            // Defines a handle for the byte download stream
            InputStream byteStream = null;
            // Downloads the file and catches IO errors
            try {
                URL url = new URL(mDownloadTask.getDownload().getUrl());
                // Opens an HTTP connection to the file's URL
                HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
                // Before continuing, checks to see that the Thread hasn't been interrupted
                checkInterrupt();
                // Gets the input stream containing the file
                byteStream = httpConn.getInputStream();
                checkInterrupt();
                // Gets the size of the file being downloaded. This may or may not be returned.
                int contentSize = httpConn.getContentLength();

                // If the size of the file isn't available
                if (-1 == contentSize) {
                    // Allocates a temporary buffer
                    byte[] tempBuffer = new byte[READ_SIZE];

                    // Records the initial amount of available space
                    int bufferLeft = tempBuffer.length;

                    /*
                     * Defines the initial offset of the next available
                     * byte in the buffer, and the initial result of
                     * reading the binary
                     */
                    int bufferOffset = 0;
                    int readResult = 0;

                    /*
                     * The "outer" loop continues until all the bytes
                     * have been downloaded. The inner loop continues
                     * until the temporary buffer is full, and then
                     * allocates more buffer space.
                     */
                    outer:
                    do {
                        while (bufferLeft > 0) {
                            /*
                             * Reads from the URL location into
                             * the temporary buffer, starting at the
                             * next available free byte and reading as
                             * many bytes as are available in the buffer.
                             */
                            readResult = byteStream.read(tempBuffer, bufferOffset, bufferLeft);
                            // InputStream.read() returns zero when the file has been completely read.
                            if (readResult < 0) {
                                // The read is finished, so this breaks the to "outer" loop
                                break outer;
                            }
                            /*
                             * The read isn't finished. This sets the
                             * next available open position in the
                             * buffer (the buffer index is 0-based).
                             */
                            bufferOffset += readResult;
                            // Subtracts the number of bytes read from
                            // the amount of buffer left
                            bufferLeft -= readResult;

                            // notify the service that the download has been updated
                            // TODO: Control when this is being called (how many times per byte or second)
                            mDownloadTask.handleDownloadState(DownloadService.DOWNLOAD_UPDATED);
                            checkInterrupt();
                        }
                        /*
                         * The temporary buffer is full, so the
                         * following code creates a new buffer that can
                         * contain the existing contents plus the next
                         * read cycle.
                         */
                        // Resets the amount of buffer left to be the max buffer size
                        bufferLeft = READ_SIZE;
                        /*
                         * Sets a new size that can contain the existing
                         * buffer's contents plus space for the next read cycle.
                         */
                        int newSize = tempBuffer.length + READ_SIZE;
                        /*
                         * Creates a new temporary buffer, moves the
                         * contents of the old temporary buffer into it,
                         * and then points the temporary buffer variable
                         * to the new buffer.
                         */
                        byte[] expandedBuffer = new byte[newSize];
                        System.arraycopy(tempBuffer, 0, expandedBuffer, 0, tempBuffer.length);
                        tempBuffer = expandedBuffer;
                    } while (true);

                    /*
                     * When the entire file has been read, this creates
                     * a permanent byte buffer with the same size as
                     * the number of used bytes in the temporary buffer
                     * (equal to the next open byte, because tempBuffer is 0=based).
                     */
                    byteBuffer = new byte[bufferOffset];
                    // Copies the temporary buffer to the file buffer
                    System.arraycopy(tempBuffer, 0, byteBuffer, 0, bufferOffset);
                    // The download size is available, so this creates a permanent buffer of that length.
                } else {
                    byteBuffer = new byte[contentSize];
                    // How much of the buffer still remains empty
                    int remainingLength = contentSize;
                    // The next open space in the buffer
                    int bufferOffset = 0;
                    /*
                     * Reads into the buffer until the number of bytes
                     * equal to the length of the buffer (the size of
                     * the file) have been read.
                     */
                    while (remainingLength > 0) {
                        int readResult = byteStream.read(byteBuffer, bufferOffset, remainingLength);
                        /*
                         * EOF should not occur, because the loop should
                         * read the exact # of bytes in the file
                         */
                        if (readResult < 0) {
                            // Throws an EOF Exception
                            throw new EOFException();
                        }
                        // Moves the buffer offset to the next open byte
                        bufferOffset += readResult;
                        // Subtracts the # of bytes read from the remaining length
                        remainingLength -= readResult;
                        // notify the service that the download has been updated
                        // TODO: Control when this is being called (how many times per byte or second)
                        mDownloadTask.handleDownloadState(DownloadService.DOWNLOAD_UPDATED);
                        checkInterrupt();
                    }
                }
                checkInterrupt();
                // If an IO error occurs, returns immediately
            } catch (IOException e) {
                Log.e("JIKA", e.toString());
                return;
                // If the input stream is still open, close it
            } finally {
                if (null != byteStream) {
                    try {
                        byteStream.close();
                    } catch (Exception e) {
                        // nothing
                    }
                }
            }
            // Sets the status message in the DownloadTask instance.
            mDownloadTask.handleDownloadState(HTTP_STATE_COMPLETED);
            // Catches exceptions thrown in response to a queued interrupt
        } catch (InterruptedException e1) {
            // Does nothing
            // In all cases, handle the results
        } finally {
            // If the byteBuffer is null, reports that the download failed.
            if (null == byteBuffer) {
                mDownloadTask.handleDownloadState(HTTP_STATE_FAILED);
            }
            /*
             * Locks on the static ThreadPool
             * object and returns the current thread. Locking keeps all references to Thread
             * objects the same until the reference to the current Thread is deleted.
             */
            // Sets the reference to the current Thread to null, releasing its storage
            mDownloadTask.setCurrentThread(null);
            // Clears the Thread's interrupt flag
            Thread.interrupted();
        }
    }

    // Helper method to check for interrupt
    private void checkInterrupt() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
    }

}
