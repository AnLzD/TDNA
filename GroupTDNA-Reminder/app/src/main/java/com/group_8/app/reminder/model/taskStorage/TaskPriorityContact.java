package com.group_8.app.reminder.model.taskStorage;

import android.content.ContentValues;
import android.database.Cursor;

import com.group_8.app.reminder.model.TaskPriority;

public class TaskPriorityContact {
    public static String TABLE_NAME = "priority_table";
    public static String COL_ID = "id";
    public static String COL_NAME = "name";
    public static String COL_COLOR = "color";

    public static String getCreateTableQuery(){
        return String.format("CREATE TABLE IF NOT EXISTS %s (" +
                " %s INTEGER PRIMARY KEY," +
                " %s TEXT," +
                " %s TEXT)",
                TABLE_NAME,
                COL_ID,
                COL_NAME,
                COL_COLOR);
    }

    public static String getDropTableQuery(){
        return "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static String getLoadAllPriorityQuery(){
        return "SELECT * FROM " + TABLE_NAME + " WHERE 1";
    }

    public static TaskPriority getPriority(Cursor cursor){
        int id = cursor.getInt(cursor.getColumnIndex(COL_ID));
        String name = cursor.getString(cursor.getColumnIndex(COL_NAME));
        String color = cursor.getString(cursor.getColumnIndex(COL_COLOR));
        return new TaskPriority(id,name,color);
    }

    public static ContentValues getContentValues(TaskPriority priority){
        ContentValues values = new ContentValues();
        if(priority.getId() > 0) {
            values.put(COL_ID, priority.getId());
        }
        values.put(COL_NAME,priority.getName());
        values.put(COL_COLOR,priority.getColor());
        return values;
    }
}
