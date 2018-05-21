package com.group_8.app.reminder.model;

import java.util.Calendar;

public class Task {
    private int id;
    private String name;
    private String description;
    private String imageLink;
    private TaskPriority priority;
    private TaskProgress progress;
    private long startDate;
    private long endDate;
    private long notifyDate;
    private boolean notify;

    public long getNotifyDate() {
        return notifyDate;
    }

    public void setNotifyDate(long notifyDate) {
        this.notifyDate = notifyDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public TaskProgress getProgress() {
        return progress;
    }

    public void setProgress(TaskProgress progress) {
        this.progress = progress;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNotify(boolean notify){this.notify = notify;}

    public boolean getNotify(){return notify;}

    public Task(){

    }

    public Task(Task input){
        setName(input.getName());
        setDescription(input.getDescription());
        setImageLink(input.getImageLink());
        setId(input.getId());
        setNotify(input.getNotify());
        setNotifyDate(input.getNotifyDate());
        setStartDate(input.getStartDate());
        setEndDate(input.getEndDate());
        setPriority(new TaskPriority(input.getPriority().getId(), input.getPriority().getName(), input.getPriority().getColor()));
        setProgress(new TaskProgress(input.getProgress().getId(), input.getProgress().getProgress()));
    }
}
