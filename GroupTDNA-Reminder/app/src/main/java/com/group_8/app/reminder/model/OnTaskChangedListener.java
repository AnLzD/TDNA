package com.group_8.app.reminder.model;

public class OnTaskChangedListener {
    private boolean active = false;

    public void onTaskChanged(Task task){

    }

    public void onTaskDeleted(Task task){

    }

    public void onTaskAdded(Task task){

    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }
}