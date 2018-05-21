package com.group_8.app.reminder.model.taskStorage;

import android.content.ContentValues;
import android.database.Cursor;

import com.group_8.app.reminder.model.Task;
import com.group_8.app.reminder.model.TaskPriority;
import com.group_8.app.reminder.model.TaskProgress;

public class TaskContact {
    public static String TABLE_NAME = "task";
    public static String COL_ID = "id";
    public static String COL_NAME = "name";
    public static String COL_DESCRIPTION = "description";
    public static String COL_IMAGE = "image";
    public static String COL_PRIORITY = "priority";
    public static String COL_PROGRESS = "progress";
    public static String COL_START_DATE = "start_date";
    public static String COL_END_DATE = "end_date";
    public static String COL_NOTIFY_DATE = "notify_date";
    public static String COL_PRIORITY_NAME = "priority_name";
    public static String COL_PRIORITY_COLOR = "priority_color";
    public static String COL_PROGRESS_NAME = "progress_name";
    public static String COL_ENABLE_NOTIFY = "enable_notification";


    public static String getCreateTableQuery() {
        return String.format(
                "CREATE TABLE IF NOT EXISTS %s (" +
                        "%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "%s TEXT," +
                        "%s TEXT," +
                        "%s TEXT," +
                        "%s INTEGER," +
                        "%s INTEGER," +
                        "%s INTEGER," +
                        "%s INTEGER," +
                        "%s INTEGER," +
                        "%s INTEGER)",
                TABLE_NAME,
                COL_ID,
                COL_NAME,
                COL_DESCRIPTION,
                COL_IMAGE,
                COL_PRIORITY,
                COL_PROGRESS,
                COL_START_DATE,
                COL_END_DATE,
                COL_NOTIFY_DATE,
                COL_ENABLE_NOTIFY
        );
    }

    public static String getDropTableQuery() {
        return "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static String getLoadAllTasksQuery() {
        return String.format("SELECT %s.*," +
                        " %s.%s AS %s," +
                        " %s.%s AS %s, " +
                        " %s.%s AS %s " +
                        " FROM %s LEFT JOIN %s ON " +
                        " %s.%s = %s.%s " +
                        " LEFT JOIN %s ON " +
                        " %s.%s = %s.%s WHERE 1",
                TABLE_NAME,
                TaskPriorityContact.TABLE_NAME, TaskPriorityContact.COL_NAME, COL_PRIORITY_NAME,
                TaskPriorityContact.TABLE_NAME, TaskPriorityContact.COL_COLOR, COL_PRIORITY_COLOR,
                TaskProgressContact.TABLE_NAME, TaskProgressContact.COL_NAME, COL_PROGRESS_NAME,
                TABLE_NAME, TaskPriorityContact.TABLE_NAME,
                TABLE_NAME, COL_PRIORITY, TaskPriorityContact.TABLE_NAME, TaskPriorityContact.COL_ID,
                TaskProgressContact.TABLE_NAME,
                TABLE_NAME, COL_PROGRESS, TaskProgressContact.TABLE_NAME, TaskProgressContact.COL_ID
        );
    }

    public static ContentValues getContentValues(Task task) {
        ContentValues values = new ContentValues();
        if (task.getId() > 0) {
            values.put(COL_ID, task.getId());
        }
        values.put(COL_NAME, task.getName());
        values.put(COL_IMAGE, task.getImageLink());
        values.put(COL_DESCRIPTION, task.getDescription());
        values.put(COL_PRIORITY, task.getPriority().getId());
        values.put(COL_PROGRESS, task.getProgress().getId());
        values.put(COL_START_DATE, task.getStartDate());
        values.put(COL_END_DATE, task.getEndDate());
        values.put(COL_NOTIFY_DATE, task.getNotifyDate());
        values.put(COL_ENABLE_NOTIFY,task.getNotify()?1:0);
        return values;
    }

    public static Task getTask(Cursor cursor) {
        Task task = new Task();
        int id = cursor.getInt(cursor.getColumnIndex(COL_ID));
        int priority = cursor.getInt(cursor.getColumnIndex(COL_PRIORITY));
        String priorityName = cursor.getString(cursor.getColumnIndex(COL_PRIORITY_NAME));
        String priorityColor = cursor.getString(cursor.getColumnIndex(COL_PRIORITY_COLOR));
        int progress = cursor.getInt(cursor.getColumnIndex(COL_PROGRESS));
        String progressName = cursor.getString(cursor.getColumnIndex(COL_PROGRESS_NAME));
        long startDate = cursor.getLong(cursor.getColumnIndex(COL_START_DATE));
        long endDate = cursor.getLong(cursor.getColumnIndex(COL_END_DATE));
        long notifyDate = cursor.getLong(cursor.getColumnIndex(COL_NOTIFY_DATE));
        String name = cursor.getString(cursor.getColumnIndex(COL_NAME));
        String description = cursor.getString(cursor.getColumnIndex(COL_DESCRIPTION));
        String image = cursor.getString(cursor.getColumnIndex(COL_IMAGE));
        int notification = cursor.getInt(cursor.getColumnIndex(COL_ENABLE_NOTIFY));

        task.setId(id);
        task.setName(name);
        task.setDescription(description);
        task.setImageLink(image);
        task.setStartDate(startDate);
        task.setEndDate(endDate);
        task.setNotifyDate(notifyDate);
        task.setProgress(new TaskProgress(progress,progressName));
        task.setPriority(new TaskPriority(priority,priorityName,priorityColor));
        task.setNotify(notification==1);
        return task;
    }
}
