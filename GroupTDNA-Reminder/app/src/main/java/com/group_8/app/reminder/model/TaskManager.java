package com.group_8.app.reminder.model;

import android.content.Context;

import com.group_8.app.reminder.R;
import com.group_8.app.reminder.model.taskStorage.TaskStorage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class TaskManager {
    private final static int TYPE_ADDED = 0;
    private final static int TYPE_DELETED = 1;
    private final static int TYPE_MODIFIED = 2;

    private static TaskManager instance;
    private List<Task> tasks;
    private List<OnTaskChangedListener> subscribers;
    private TaskStorage mDatabase;
    private List<TaskPriority> mTaskPriorities;
    private List<TaskProgress> mTaskProgresses;

    private TaskManager() {
        tasks = new ArrayList<>();
        subscribers = new ArrayList<>();
        mTaskPriorities = new ArrayList<>();
        mTaskProgresses = new ArrayList<>();
    }

    private TaskManager(Context context) {
        this();
        TaskStorage.initialize(context);
        mDatabase = TaskStorage.getInstance();
    }

    private void initializeTaskPriority(Context context) {
        List<TaskPriority> priorities = new ArrayList<>();
        String[] names = context.getResources().getStringArray(R.array.priority_array);
        String[] color = context.getResources().getStringArray(R.array.priority_color);
        for (int i = 0; i < names.length; i++) {
            priorities.add(new TaskPriority(i + 1, names[i], color[i]));
        }
        mDatabase.getDatabase().savePriorities(priorities);
        mTaskPriorities = priorities;
    }

    private void initializeTaskProgress(Context context) {
        List<TaskProgress> progresses = new ArrayList<>();
        String[] names = context.getResources().getStringArray(R.array.progress_array);
        for (int i = 0; i < names.length; i++) {
            progresses.add(new TaskProgress(i + 1, names[i]));
        }
        mDatabase.getDatabase().saveProgresses(progresses);
        mTaskProgresses = progresses;
    }

    public static void initialize(Context context) {
        instance = new TaskManager(context);
        instance.mTaskProgresses = instance.mDatabase.getDatabase().loadAllProgresses();
        instance.mTaskPriorities = instance.mDatabase.getDatabase().loadAllPriorities();
        if (instance.mTaskPriorities.size() == 0) {
            instance.initializeTaskPriority(context);
        }
        if (instance.mTaskProgresses.size() == 0) {
            instance.initializeTaskProgress(context);
        }
        instance.tasks = instance.mDatabase.getDatabase().getAllTasks();
        Logger.log("Khanh", "Task count: " + instance.tasks.size());
    }

    public static TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }

    public List<Task> getAllTasks() {
        return tasks;
    }

    public void setAllTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public int addTask(Task task) {
        tasks.add(task);
        long id = mDatabase.getDatabase().saveTask(task);
        Logger.log("Khanh", "Add new task that has id: " + id);
        notifyToSubscriber(task, TYPE_ADDED);
        return (int) id;
    }

    public void updateTask(Task task) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == task.getId()) {
                tasks.set(i, task);
                notifyToSubscriber(task, TYPE_MODIFIED);
                break;
            }
        }
        mDatabase.getDatabase().updateTask(task);
    }

    public void deleteTask(Task task) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == task.getId()) {
                tasks.remove(i);
                notifyToSubscriber(task, TYPE_DELETED);
                break;
            }
        }
        mDatabase.getDatabase().deleteTask(task);
    }

    public Task getTask(int id) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == id) {
                return tasks.get(i);
            }
        }
        return null;
    }

    public List<Task> getTasksOfDate(long timestamp) {
        Calendar c = Calendar.getInstance(Locale.getDefault());
        c.setTimeInMillis(timestamp);
        return getTasksOfDate(c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR));
    }

    public List<Task> getTasksOfDate(int dd, int mm, int yyyy) {
        Logger.log("Khanh", "Get task of day: " + dd + "/" + mm+ "/" + yyyy);
        Calendar date = new GregorianCalendar(yyyy, mm - 1, dd);
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        long start = date.getTimeInMillis();
        date.add(Calendar.DAY_OF_YEAR, 1);
        long end = date.getTimeInMillis();
        List<Task> tasks = new ArrayList<>();
        Logger.log("Khanh", "Time: " + start + " - " + end);
        for (int i = 0; i < this.tasks.size(); i++) {
            long taskStart = this.tasks.get(i).getStartDate();
            long taskEnd = this.tasks.get(i).getEndDate();
            if ((taskStart >= start && taskStart <= end) ||
                    (taskStart < start && taskEnd >= start)) {
                Logger.log("Khanh","Added " + this.tasks.get(i).getId());
                tasks.add(this.tasks.get(i));
            }
        }
        return tasks;
    }

    public boolean existTaskOnDate(long timestamp) {
        Calendar c = Calendar.getInstance(Locale.getDefault());
        c.setTimeInMillis(timestamp);
        return existTaskOnDate(c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR));
    }

    public boolean existTaskOnDate(int dd, int mm, int yyyy) {
        Calendar date = new GregorianCalendar(yyyy, mm - 1, dd);
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        long start = date.getTimeInMillis();
        date.add(Calendar.DAY_OF_YEAR, 1);
        long end = date.getTimeInMillis();
        for (int i = 0; i < this.tasks.size(); i++) {
            long taskStart = this.tasks.get(i).getStartDate();
            long taskEnd = this.tasks.get(i).getEndDate();
            if ((taskStart >= start && taskStart <= end) ||
                    (taskStart < start && taskEnd >= start)) {
                return true;
            }
        }
        return false;
    }

    public List<Task> search(String name) {
        return null;
    }

    public List<Task> search(TaskPriority priority) {
        return null;
    }

    public List<TaskPriority> getAllTaskPriorities() {
        return mTaskPriorities;
    }

    public List<TaskProgress> getAllTaskProgresses() {
        return mTaskProgresses;
    }

    public int subscribe(OnTaskChangedListener listener) {
        subscribers.add(listener);
        listener.setActive(true);
        return subscribers.size() - 1;
    }

    public void unSubscribe(int id) {
        subscribers.get(id).setActive(false);
    }

    private void notifyToSubscriber(Task task, int type) {
        for (int i = 0; i < subscribers.size(); i++) {
            if (subscribers.get(i) != null && subscribers.get(i).isActive()) {
                switch (type) {
                    case TYPE_ADDED:
                        subscribers.get(i).onTaskAdded(task);
                        break;
                    case TYPE_MODIFIED:
                        subscribers.get(i).onTaskChanged(task);
                        break;
                    default:
                        subscribers.get(i).onTaskDeleted(task);
                        break;
                }
            }
        }
    }

    public boolean isTaskOfDay(Task task, long timestamp){
        long taskStart = task.getStartDate();
        long taskEnd = task.getEndDate();

        Calendar c = Calendar.getInstance(Locale.getDefault());
        c.setTimeInMillis(timestamp);

        Calendar date = new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        long start = date.getTimeInMillis();
        date.add(Calendar.DAY_OF_YEAR, 1);
        long end = date.getTimeInMillis();

        if ((taskStart >= start && taskStart <= end) ||
                (taskStart < start && taskEnd >= start)) {
            return true;
        }
        return false;
    }
}
