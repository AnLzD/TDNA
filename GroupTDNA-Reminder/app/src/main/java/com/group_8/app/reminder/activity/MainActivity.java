package com.group_8.app.reminder.activity;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.group_8.app.reminder.R;
import com.group_8.app.reminder.adapter.MainPagerAdapter;
import com.group_8.app.reminder.model.Logger;
import com.group_8.app.reminder.model.TaskManager;
import com.group_8.app.reminder.model.TaskPriority;
import com.group_8.app.reminder.model.TaskProgress;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.vp_main)
    ViewPager mViewPager;
    @BindView(R.id.tl_main)
    TabLayout mTlMain;

    private MainPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initializeView();

        List<TaskProgress> progresses = TaskManager.getInstance().getAllTaskProgresses();
        Logger.log("Khanh","Progress length: " + progresses.size());
        for(int i = 0;i< progresses.size();i++){
            Logger.log("Khanh",progresses.get(i).getId() + " - " +
            progresses.get(i).getProgress());
        }

        List<TaskPriority> priorities = TaskManager.getInstance().getAllTaskPriorities();
        Logger.log("Khanh","Priority length: " + priorities.size());
        for(int i = 0;i< priorities.size();i++){
            Logger.log("Khanh",priorities.get(i).getId() + " - " +
            priorities.get(i).getName() + " - " + priorities.get(i).getColor());
        }
    }

    private void initializeView(){
        mPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mTlMain.setupWithViewPager(mViewPager);

        int icon[] = {R.drawable.ic_home,
        R.drawable.ic_calendar,
        R.drawable.ic_finder,
        //R.drawable.ic_settings
        };

        for(int i = 0;i<3;i++) {
            try {
                mTlMain.getTabAt(i).setIcon(icon[i]);
            } catch (NullPointerException ex){
                //todo nothing
            }
        }
    }
}
