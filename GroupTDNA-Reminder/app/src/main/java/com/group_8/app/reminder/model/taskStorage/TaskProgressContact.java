package com.group_8.app.reminder.model.taskStorage;

import android.content.ContentValues;
import android.database.Cursor;

import com.group_8.app.reminder.model.TaskProgress;

public class TaskProgressContact {
    public static String TABLE_NAME = "task_progress";
    public static String COL_ID = "p_id";
    public static String COL_NAME = "p_name";

    public static String getCreateTableQuery(){
        return String.format("CREATE TABLE IF NOT EXISTS %s (" +
                        " %s INTEGER PRIMARY KEY," +
                        " %s TEXT)",
                TABLE_NAME,
                COL_ID,
                COL_NAME);
    }

    public static String getDropTableQuery(){
        return "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static String getLoadAllProgressQuery(){
        return "SELECT * FROM " + TABLE_NAME + " WHERE 1";
    }

    public static TaskProgress getProgress(Cursor cursor){
        int id = cursor.getInt(cursor.getColumnIndex(COL_ID));
        String name = cursor.getString(cursor.getColumnIndex(COL_NAME));
        return new TaskProgress(id,name);
    }

    public static ContentValues getContentValues(TaskProgress progress){
        ContentValues values = new ContentValues();
        if(progress.getId() > 0) {
            values.put(COL_ID, progress.getId());
        }
        values.put(COL_NAME,progress.getProgress());
        return values;
    }
}
