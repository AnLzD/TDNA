package com.group_8.app.reminder.model.taskStorage;

import android.content.Context;

public class TaskStorage {
    private TaskDatabase database;
    private static TaskStorage instance;

    private TaskStorage(Context context){
        database = new TaskDatabase(context.getApplicationContext());
    }

    public static void initialize(Context context){
        instance = new TaskStorage(context);
    }

    public static TaskStorage getInstance(){
        return instance;
    }

    public TaskDatabase getDatabase() {
        return database;
    }

    public void setDatabase(TaskDatabase database) {
        this.database = database;
    }
}
