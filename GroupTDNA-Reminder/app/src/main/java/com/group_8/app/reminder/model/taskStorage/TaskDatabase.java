package com.group_8.app.reminder.model.taskStorage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.group_8.app.reminder.model.Task;
import com.group_8.app.reminder.model.TaskPriority;
import com.group_8.app.reminder.model.TaskProgress;

import java.util.ArrayList;
import java.util.List;

public class TaskDatabase extends SQLiteOpenHelper {
    private static String DATABASE_NAME = "task_database";
    private static int VERSION_CODE = 2;

    public TaskDatabase(Context context) {
        super(context, DATABASE_NAME, null, VERSION_CODE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TaskProgressContact.getCreateTableQuery());
        db.execSQL(TaskPriorityContact.getCreateTableQuery());
        db.execSQL(TaskContact.getCreateTableQuery());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(TaskContact.getDropTableQuery());
        db.execSQL(TaskProgressContact.getDropTableQuery());
        db.execSQL(TaskPriorityContact.getDropTableQuery());

        onCreate(db);
    }

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(TaskContact.getLoadAllTasksQuery(), null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Task task = TaskContact.getTask(cursor);
                tasks.add(task);
            } while (cursor.moveToNext());
            cursor.close();
        }
        if(cursor != null){
            cursor.close();
        }
        db.close();
        return tasks;
    }

    public long saveTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = TaskContact.getContentValues(task);
        long id = db.insert(TaskContact.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public void updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = TaskContact.getContentValues(task);
        db.update(TaskContact.TABLE_NAME, values,
                TaskContact.COL_ID + " = ?", new String[]{String.valueOf(task.getId())});
        db.close();
    }

    public void deleteTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TaskContact.TABLE_NAME,
                TaskContact.COL_ID + " = ?", new String[]{String.valueOf(task.getId())});
        db.close();
    }

    public void deleteTasks(List<Task> tasks) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        for (int i = 0; i < tasks.size(); i++) {
            try {
                db.delete(TaskContact.TABLE_NAME,
                        TaskContact.COL_ID + " = ?",
                        new String[]{String.valueOf(tasks.get(i).getId())});
            } catch (Exception ex) {
                //todo nothing
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public List<TaskPriority> loadAllPriorities(){
        List<TaskPriority> priorities = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(TaskPriorityContact.getLoadAllPriorityQuery(),null);
        if(cursor != null && cursor.moveToFirst()){
            do{
                priorities.add(TaskPriorityContact.getPriority(cursor));
            } while (cursor.moveToNext());
        }
        if(cursor != null){
            cursor.close();
        }
        db.close();
        return priorities;
    }

    public void savePriority(TaskPriority priority){
        ContentValues values = TaskPriorityContact.getContentValues(priority);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TaskPriorityContact.TABLE_NAME,null,values);
        db.close();
    }

    public void savePriorities(List<TaskPriority> priorities){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        for(int i = 0;i< priorities.size();i++){
            try{
                ContentValues values = TaskPriorityContact.getContentValues(priorities.get(i));
                db.insert(TaskPriorityContact.TABLE_NAME,null,values);
            } catch (Exception ex){
                //todo nothing
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public List<TaskProgress> loadAllProgresses(){
        List<TaskProgress> progresses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(TaskProgressContact.getLoadAllProgressQuery(),null);
        if(cursor != null && cursor.moveToFirst()){

            do{
                progresses.add(TaskProgressContact.getProgress(cursor));
            } while (cursor.moveToNext());
        }
        if(cursor != null){
            cursor.close();
        }
        db.close();
        return progresses;
    }

    public void saveProgress(TaskProgress progress){
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TaskProgressContact.TABLE_NAME,null,
                TaskProgressContact.getContentValues(progress));
        db.close();
    }

    public void saveProgresses(List<TaskProgress> progresses){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        for(int i = 0;i< progresses.size();i++){
            try {
                db.insert(TaskProgressContact.TABLE_NAME, null,
                        TaskProgressContact.getContentValues(progresses.get(i)));
            } catch (Exception ex){
                //todo log
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }
}
