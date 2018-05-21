package com.group_8.app.reminder.activity;


import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.group_8.app.reminder.R;
import com.group_8.app.reminder.adapter.TaskAdapter;
import com.group_8.app.reminder.model.ConstKey;
import com.group_8.app.reminder.model.Logger;
import com.group_8.app.reminder.model.OnTaskChangedListener;
import com.group_8.app.reminder.model.Task;
import com.group_8.app.reminder.model.TaskManager;

import java.util.List;

public class DayTaskActivity extends AppCompatActivity {
    private boolean isStartOtherActivity = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_of_day2);
        getClickListener();
    }

    private void getClickListener() {
        EditText editDate = findViewById(R.id.edt_Date);
        ListView taskOfDate = findViewById(R.id.listTaskOfDate);
        Intent intent = getIntent();
        int date = intent.getIntExtra("date",0);
        int month = intent.getIntExtra("month",0);

        month = month+1;

        int year = intent.getIntExtra("year",0);

        editDate.setText(date + "/"+month+'/'+year);
        final List<Task> listTaskToday = TaskManager.getInstance().getTasksOfDate(date,month,year);
        final TaskAdapter todayAdapter = new TaskAdapter(
                DayTaskActivity.this,
                listTaskToday
        );
        taskOfDate.setAdapter(todayAdapter);
        taskOfDate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openTask(listTaskToday.get(position));
            }
        });
        TaskManager.getInstance().subscribe(new OnTaskChangedListener(){
            @Override
            public void onTaskChanged(Task task) {
                for(int i = 0; i < listTaskToday.size(); i++){
                    if(listTaskToday.get(i).getId() == task.getId()){
                        listTaskToday.set(i,task);
                        todayAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onTaskDeleted(Task task) {
                super.onTaskDeleted(task);
                for(int i = 0; i < listTaskToday.size(); i++){
                    if(listTaskToday.get(i).getId() == task.getId()){
                        listTaskToday.remove(i);
                        todayAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onTaskAdded(Task task) {
                listTaskToday.add(0,task);
                todayAdapter.notifyDataSetChanged();
            }
        });
    }

    public void openTask(Task task){
        if(!isStartOtherActivity) {
            Logger.log("Home","Start task activity");
            isStartOtherActivity = true;
            Intent intent = new Intent(DayTaskActivity.this, TaskActivity.class);
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
}
