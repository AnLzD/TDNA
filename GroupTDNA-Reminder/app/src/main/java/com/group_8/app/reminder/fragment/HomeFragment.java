package com.group_8.app.reminder.fragment;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.group_8.app.reminder.R;
import com.group_8.app.reminder.activity.TaskActivity;
import com.group_8.app.reminder.adapter.TaskAdapter;
import com.group_8.app.reminder.model.ConstKey;
import com.group_8.app.reminder.model.Logger;
import com.group_8.app.reminder.model.OnTaskChangedListener;
import com.group_8.app.reminder.model.Task;
import com.group_8.app.reminder.model.TaskManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeFragment extends Fragment {
    private final static long DAY_LONG = 86400000;
    @BindView(R.id.listTodayTask)
    ListView lvTodayTask;
    @BindView(R.id.listTomorowTask)
    ListView lvTomorowTask;
    @BindView(R.id.listUpcomingTask)
    ListView lvUpcomingTask;
    @BindView(R.id.tv_today)
    TextView tvToday;
    @BindView(R.id.tv_tomorrow)
    TextView tvTomorrow;
    @BindView(R.id.tv_upcoming)
    TextView tvUpcoming;

    List<Task> listTaskToday;
    List<Task> listTaskTomorrow;
    List<Task> listTaskUpcoming;
    TaskAdapter todayAdapter;
    TaskAdapter tomorrowAdapter;
    private TaskAdapter upcomingAdapter;

    private boolean isStartOtherActivity = false;

    public HomeFragment() {
        listTaskToday = TaskManager.getInstance().getTasksOfDate(System.currentTimeMillis());
        listTaskTomorrow = TaskManager.getInstance().getTasksOfDate(System.currentTimeMillis() + DAY_LONG);
        listTaskUpcoming = new ArrayList<>();
        for(int i=2;i<7;i++){
            listTaskUpcoming.addAll(TaskManager.getInstance().getTasksOfDate(System.currentTimeMillis() + i*DAY_LONG));
        }
        TaskManager.getInstance().subscribe(new OnTaskChangedListener(){
            @Override
            public void onTaskChanged(Task task) {
                long midnightTimeStamp ;
                Calendar date = new GregorianCalendar();
                date.set(Calendar.HOUR_OF_DAY, 0);
                date.set(Calendar.MINUTE, 0);
                date.set(Calendar.SECOND, 0);
                date.set(Calendar.MILLISECOND, 0);
                midnightTimeStamp = date.getTimeInMillis();
                boolean isExist=false;
                if(task.getStartDate() > midnightTimeStamp && task.getStartDate() < midnightTimeStamp + DAY_LONG){
                    for(int i=0;i<listTaskToday.size();i++){
                        if(task.getId()==listTaskToday.get(i).getId()){
                            listTaskToday.remove(i);
                            listTaskToday.add(i,task);
                            isExist=true;
                            break;
                        }
                    }
                    if(isExist==false){
                        removeTaskOnListView(task);
                        listTaskToday.add(0,task);
                    }
                    todayAdapter.notifyDataSetChanged();
                }else if(task.getStartDate() >= midnightTimeStamp + DAY_LONG && task.getStartDate() < midnightTimeStamp + DAY_LONG*2){
                    for(int i=0;i<listTaskTomorrow.size();i++){
                        if(task.getId()==listTaskTomorrow.get(i).getId()){
                            listTaskTomorrow.remove(i);
                            listTaskTomorrow.add(i,task);
                            isExist=true;
                            break;
                        }
                    }
                    if(isExist==false){
                        removeTaskOnListView(task);
                        listTaskTomorrow.add(0,task);
                    }
                    tomorrowAdapter.notifyDataSetChanged();
                } else if(task.getStartDate() >= midnightTimeStamp + DAY_LONG*2 && task.getStartDate() < midnightTimeStamp + DAY_LONG*7){
                    for(int i=0;i<listTaskUpcoming.size();i++){
                        if(task.getId()==listTaskUpcoming.get(i).getId()){
                            listTaskUpcoming.remove(i);
                            listTaskUpcoming.add(i,task);
                            isExist=true;
                            break;
                        }
                    }
                    if(isExist==false){
                        removeTaskOnListView(task);
                        listTaskUpcoming.add(0,task);
                    }
                    upcomingAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTaskDeleted(Task task) {
                long midnightTimeStamp ;
                Calendar date = new GregorianCalendar();
                date.set(Calendar.HOUR_OF_DAY, 0);
                date.set(Calendar.MINUTE, 0);
                date.set(Calendar.SECOND, 0);
                date.set(Calendar.MILLISECOND, 0);
                midnightTimeStamp = date.getTimeInMillis();
                if(task.getStartDate() > midnightTimeStamp && task.getStartDate() < midnightTimeStamp + DAY_LONG){
                    for(int i=0;i<listTaskToday.size();i++){
                        if(task.getId()==listTaskToday.get(i).getId()){
                            listTaskToday.remove(i);
                            break;
                        }
                    }
                    todayAdapter.notifyDataSetChanged();
                } else if(task.getStartDate() >= midnightTimeStamp + DAY_LONG && task.getStartDate() < midnightTimeStamp + DAY_LONG*2){
                    for(int i=0;i<listTaskTomorrow.size();i++){
                        if(task.getId()==listTaskTomorrow.get(i).getId()){
                            listTaskTomorrow.remove(i);
                            break;
                        }
                    }
                    tomorrowAdapter.notifyDataSetChanged();
                } else if(task.getStartDate() >= midnightTimeStamp + DAY_LONG*2 && task.getStartDate() < midnightTimeStamp + DAY_LONG*7){
                    for(int i=0;i<listTaskUpcoming.size();i++){
                        if(task.getId()==listTaskUpcoming.get(i).getId()){
                            listTaskUpcoming.remove(i);
                            break;
                        }
                    }
                    upcomingAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTaskAdded(Task task) {
                if(TaskManager.getInstance().isTaskOfDay(task,System.currentTimeMillis())){
                    listTaskToday.add(0,task);
                    todayAdapter.notifyDataSetChanged();
                }

                if(TaskManager.getInstance().isTaskOfDay(task,System.currentTimeMillis() + DAY_LONG)){
                    listTaskTomorrow.add(0,task);
                    tomorrowAdapter.notifyDataSetChanged();
                }

                for(int i = 2; i<= 7;i++){
                    if(TaskManager.getInstance().isTaskOfDay(task,System.currentTimeMillis() + i*DAY_LONG)){
                        listTaskUpcoming.add(0,task);
                        upcomingAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        });
    }

    public void removeTaskOnListView(Task task){
        for(int i=0;i<listTaskToday.size();i++){
            if(task.getId()==listTaskToday.get(i).getId()){
                listTaskToday.remove(i);
                break;
            }
        }
        for(int i=0;i<listTaskTomorrow.size();i++){
            if(task.getId()==listTaskTomorrow.get(i).getId()){
                listTaskTomorrow.remove(i);
                break;
            }
        }
        for(int i=0;i<listTaskUpcoming.size();i++){
            if(task.getId()==listTaskUpcoming.get(i).getId()){
                listTaskUpcoming.remove(i);
                break;
            }
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

        todayAdapter = new TaskAdapter(
                getContext(),
                listTaskToday
        );
        lvTodayTask.setAdapter(todayAdapter);
        lvTodayTask.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                openTask(listTaskToday.get(position));
            }
        });

        tomorrowAdapter = new TaskAdapter(
                getContext(),
                listTaskTomorrow
        );
        lvTomorowTask.setAdapter(tomorrowAdapter);
        lvTomorowTask.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openTask(listTaskTomorrow.get(position));
            }
        });

        upcomingAdapter = new TaskAdapter(
                getContext(),
                listTaskUpcoming
        );
        lvUpcomingTask.setAdapter(upcomingAdapter);
        lvUpcomingTask.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openTask(listTaskUpcoming.get(position));
            }
        });
        return view;
    }

    @OnClick(R.id.tv_today)
    public void showToday(){
        tvToday.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        tvTomorrow.setBackgroundColor(getResources().getColor(R.color.colorTitleTask));
        tvUpcoming.setBackgroundColor(getResources().getColor(R.color.colorTitleTask));
        tvToday.setTypeface(null,Typeface.BOLD);
        tvTomorrow.setTypeface(null, Typeface.NORMAL);
        tvUpcoming.setTypeface(null, Typeface.NORMAL);
        lvTodayTask.setVisibility(View.VISIBLE);
        lvTomorowTask.setVisibility(View.GONE);
        lvUpcomingTask.setVisibility(View.GONE);
    }

    @OnClick(R.id.tv_tomorrow)
    public void showTomorrow(){
        tvToday.setBackgroundColor(getResources().getColor(R.color.colorTitleTask));
        tvTomorrow.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        tvUpcoming.setBackgroundColor(getResources().getColor(R.color.colorTitleTask));
        tvToday.setTypeface(null, Typeface.NORMAL);
        tvTomorrow.setTypeface(null, Typeface.BOLD);
        tvUpcoming.setTypeface(null, Typeface.NORMAL);
        lvTodayTask.setVisibility(View.GONE);
        lvTomorowTask.setVisibility(View.VISIBLE);
        lvUpcomingTask.setVisibility(View.GONE);
    }

    @OnClick(R.id.tv_upcoming)
    public void showUpcoming(){
        tvToday.setBackgroundColor(getResources().getColor(R.color.colorTitleTask));
        tvTomorrow.setBackgroundColor(getResources().getColor(R.color.colorTitleTask));
        tvUpcoming.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        tvToday.setTypeface(null,Typeface.NORMAL);
        tvTomorrow.setTypeface(null, Typeface.NORMAL);
        tvUpcoming.setTypeface(null, Typeface.BOLD);
        lvTodayTask.setVisibility(View.GONE);
        lvTomorowTask.setVisibility(View.GONE);
        lvUpcomingTask.setVisibility(View.VISIBLE);
    }

    //@OnClick(R.id.listTodayTask)
    public void openTask(Task task){
        if(!isStartOtherActivity) {
            Logger.log("Home","Start task activity");
            isStartOtherActivity = true;
            Intent intent = new Intent(getActivity(), TaskActivity.class);
            intent.putExtra(ConstKey.EXTRA_TASK,new Gson().toJson(task));
            startActivity(intent);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isStartOtherActivity=false;
                }
            },2000);
        }
    }

    @OnClick(R.id.ib_add)
    public void addNewTask() {
        Logger.log("Home","AddNewTask was called");
        if(!isStartOtherActivity) {
            Logger.log("Home","Start task activity");
            isStartOtherActivity = true;
            Intent intent = new Intent(getActivity(), TaskActivity.class);
            startActivity(intent);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isStartOtherActivity=false;
                }
            },2000);
        }
    }
}
