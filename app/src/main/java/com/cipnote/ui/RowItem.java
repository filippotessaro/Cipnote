package com.cipnote.ui;

import android.os.Parcel;
import android.os.Parcelable;

public class RowItem implements Parcelable {

    private boolean done;
    private String task;

    public RowItem(boolean done, String task) {
        this.done = done;
        this.task = task;
    }

    public RowItem() {
    }

    protected RowItem(Parcel in) {
        done = in.readByte() != 0;
        task = in.readString();
    }

    public static final Creator<RowItem> CREATOR = new Creator<RowItem>() {
        @Override
        public RowItem createFromParcel(Parcel in) {
            return new RowItem(in);
        }

        @Override
        public RowItem[] newArray(int size) {
            return new RowItem[size];
        }
    };

    public boolean isDone() {
        return done;
    }

    public String getTask() {
        return task;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public void setTask(String task) {
        this.task = task;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (done ? 1 : 0));
        dest.writeString(task);
    }
}
