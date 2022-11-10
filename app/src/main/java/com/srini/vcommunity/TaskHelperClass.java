package com.srini.vcommunity;

public class TaskHelperClass {
    public TaskHelperClass() {
    }
    String title, description;
    boolean isDone;

    public TaskHelperClass(String title, String description, boolean isDone) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
