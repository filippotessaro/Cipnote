package com.cipnote.ui;

public class RowItem {

    boolean done;
    String task;

    public RowItem(boolean done, String task) {
        this.done = done;
        this.task = task;
    }

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
}
