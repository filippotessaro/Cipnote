package com.cipnote.data;

public class CalendarEntity {

    private int allDay;
    private long startTime, endTime;        // endTime only for not all day event
    private String titleCalendar, description, endTimeAllday;

    // only for add, not update
    private int calendarID;
    private String timeZone;

    private float x=0, y=0;

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
}
