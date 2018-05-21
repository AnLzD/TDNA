package com.group_8.app.reminder.model;

public class TaskProgress {
    public final static int FINISH = 3;
    public final static int MISSING = 4;

    private int id;
    private String progress;

    public TaskProgress(int id,String progress){
        this.id = id;
        this.progress = progress;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
