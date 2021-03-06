package com.cipnote.data;

import android.os.Parcel;
import android.os.Parcelable;

public class CalendarEntity implements Parcelable {

    private int allDay;
    private long startTime;
    private long endTime;        // endTime only for not all day event
    private String titleCalendar;
    private String description;
    private String endTimeAllday;

    // only for add, not update
    private int calendarID;
    private String timeZone;

    private float x=0;
    private float y=0;

    // constructor not all day event
    public CalendarEntity(long startTime, long endTime, String titleCalendar, String description,
                          int calendarID, String timeZone) {
        this.allDay = 0;
        this.startTime = startTime;
        this.endTime = endTime;
        this.titleCalendar = titleCalendar;
        this.description = description;
        this.endTimeAllday = "";
        this.calendarID = calendarID;
        this.timeZone = timeZone;
    }

    public CalendarEntity() {
    }

    //constructor all day event
    public CalendarEntity(int allDay, long startTime, String titleCalendar, String description,
                          String endTimeAllday, int calendarID, String timeZone) {
        this.allDay = allDay;
        this.startTime = startTime;
        this.endTime = 0;
        this.titleCalendar = titleCalendar;
        this.description = description;
        this.endTimeAllday = endTimeAllday;
        this.calendarID = calendarID;
        this.timeZone = timeZone;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getAllDay() {
        return allDay;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public String getTitleCalendar() {
        return titleCalendar;
    }

    public String getDescription() {
        return description;
    }

    public String getEndTimeAllday() {
        return endTimeAllday;
    }

    public int getCalendarID() {
        return calendarID;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setAllDay(int allDay) {
        this.allDay = allDay;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setTitleCalendar(String titleCalendar) {
        this.titleCalendar = titleCalendar;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEndTimeAllday(String endTimeAllday) {
        this.endTimeAllday = endTimeAllday;
    }

    public void setCalendarID(int calendarID) {
        this.calendarID = calendarID;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    protected CalendarEntity(Parcel in) {
        allDay = in.readInt();
        calendarID = in.readInt();
        timeZone = in.readString();
        x = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(allDay);
        dest.writeInt(calendarID);
        dest.writeString(timeZone);
        dest.writeFloat(x);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CalendarEntity> CREATOR = new Parcelable.Creator<CalendarEntity>() {
        @Override
        public CalendarEntity createFromParcel(Parcel in) {
            return new CalendarEntity(in);
        }

        @Override
        public CalendarEntity[] newArray(int size) {
            return new CalendarEntity[size];
        }
    };

    protected CalendarEntity(Parcel in) {
        allDay = in.readInt();
        startTime = in.readLong();
        endTime = in.readLong();
        titleCalendar = in.readString();
        description = in.readString();
        endTimeAllday = in.readString();
        calendarID = in.readInt();
        timeZone = in.readString();
        x = in.readFloat();
        y = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(allDay);
        dest.writeLong(startTime);
        dest.writeLong(endTime);
        dest.writeString(titleCalendar);
        dest.writeString(description);
        dest.writeString(endTimeAllday);
        dest.writeInt(calendarID);
        dest.writeString(timeZone);
        dest.writeFloat(x);
        dest.writeFloat(y);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CalendarEntity> CREATOR = new Parcelable.Creator<CalendarEntity>() {
        @Override
        public CalendarEntity createFromParcel(Parcel in) {
            return new CalendarEntity(in);
        }

        @Override
        public CalendarEntity[] newArray(int size) {
            return new CalendarEntity[size];
        }
    };
}