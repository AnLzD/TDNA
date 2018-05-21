package com.group_8.app.reminder.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.group_8.app.reminder.R;
import com.group_8.app.reminder.model.TaskManager;

public class SplashActivity extends AppCompatActivity {


    private TaskManager taskManager = TaskManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        loadDataFromLocal();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        },2000);
    }

    private void loadDataFromLocal(){
        taskManager.initialize(this);
    }

}
