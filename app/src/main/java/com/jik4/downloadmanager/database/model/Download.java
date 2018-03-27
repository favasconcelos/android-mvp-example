package com.jik4.downloadmanager.database.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;

import com.jik4.downloadmanager.database.converter.DateConverter;
import com.jik4.downloadmanager.database.converter.StatusConverter;

import java.util.Date;

@Entity
@TypeConverters({StatusConverter.class, DateConverter.class})
public class Download implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "url")
    private String url;

    @ColumnInfo(name = "status")
    private Status status;

    @ColumnInfo(name = "created_at")
    private Date createdAt;

    @ColumnInfo(name = "started_at")
    private Date startedAt;

    @ColumnInfo(name = "finished_at")
    private Date finishedAt;

    public Download(String url) {
        this.url = url;
        this.status = Status.INACTIVE;
        this.createdAt = new Date();
    }

    public long  getId() {
        return id;
    }

    public void setId(long  id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    public Date getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Date finishedAt) {
        this.finishedAt = finishedAt;
    }

    public enum Status {
        ACTIVE(0),
        INACTIVE(1),
        COMPLETED(2);

        private int code;

        Status(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    @Override
    public String toString() {
        return "Download{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", startedAt=" + startedAt +
                ", finishedAt=" + finishedAt +
                '}';
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(getId());
        out.writeString(getUrl());
        out.writeInt(getStatus().getCode());
        Long createdAt = getCreatedAt() != null ? getCreatedAt().getTime() : -1;
        out.writeLong(createdAt);
        Long startedAt = getStartedAt() != null ? getStartedAt().getTime() : -1;
        out.writeLong(startedAt);
        Long finishedAt = getFinishedAt() != null ? getFinishedAt().getTime() : -1;
        out.writeLong(finishedAt);
    }

    public static final Parcelable.Creator<Download> CREATOR = new Parcelable.Creator<Download>() {
        public Download createFromParcel(Parcel in) {
            return new Download(in);
        }

        public Download[] newArray(int size) {
            return new Download[size];
        }
    };

    private Download(Parcel in) {
        setId(in.readLong());
        setUrl(in.readString());
        setStatus(Download.Status.values()[in.readInt()]);
        Long createdAt = in.readLong();
        setCreatedAt(createdAt != -1 ? new Date(createdAt) : null);
        Long startedAt = in.readLong();
        setFinishedAt(startedAt != -1 ? new Date(startedAt) : null);
        Long finishedAt = in.readLong();
        setFinishedAt(finishedAt != -1 ? new Date(finishedAt) : null);
    }
}
