package com.group_8.app.reminder.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.group_8.app.reminder.R;
import com.group_8.app.reminder.activity.DayTaskActivity;
import com.group_8.app.reminder.model.Logger;
import com.group_8.app.reminder.model.OnTaskChangedListener;
import com.group_8.app.reminder.model.Task;
import com.group_8.app.reminder.model.TaskManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CalendarFragment extends Fragment {

    @BindView(R.id.calendarView)
    CalendarView calendarView;

    private Runnable mRenderItemRunnable = new Runnable() {
        @Override
        public void run() {
            showMonthTask();
        }
    };
    private Handler mHandler = new Handler();
    private OnTaskChangedListener mTaskChangedListener = new OnTaskChangedListener(){
        @Override
        public void onTaskChanged(Task task) {
            showMonthTask();
        }

        @Override
        public void onTaskDeleted(Task task) {
            showMonthTask();
        }

        @Override
        public void onTaskAdded(Task task) {
            showMonthTask();
        }
    };

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        ButterKnife.bind(this, view);
        try {
            Calendar c = Calendar.getInstance();
            Logger.log("Calendar","Today: " + c.get(Calendar.MONTH) + "/" + c.get(Calendar.YEAR));
            calendarView.setDate(c);
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }
        calendarView.setOnPreviousPageChangeListener(new OnCalendarPageChangeListener() {
            @Override
            public void onChange() {
                mHandler.removeCallbacks(mRenderItemRunnable);
                mHandler.postDelayed(mRenderItemRunnable,500);
            }
        });
        calendarView.setOnForwardPageChangeListener(new OnCalendarPageChangeListener() {
            @Override
            public void onChange() {
                mHandler.removeCallbacks(mRenderItemRunnable);
                mHandler.postDelayed(mRenderItemRunnable,500);
            }
        });
        //Bổ sung click vào ngày trên calendar
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar clickedDayCalendar = eventDay.getCalendar();
                Date date = clickedDayCalendar.getTime();
                int day = date.getDate();
                int month = date.getMonth();
                int year = 2018;
                Intent intent = new Intent(getActivity(),DayTaskActivity.class);
                intent.putExtra("date",day);
                intent.putExtra("month",month);
                intent.putExtra("year",year);

                startActivity(intent);
            }
        });
        showMonthTask();
        TaskManager.getInstance().subscribe(mTaskChangedListener);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTaskChangedListener.setActive(false);
    }

    private void showMonthTask() {
        List<EventDay> events = new ArrayList<>();
        int month = calendarView.getCurrentPageDate().get(Calendar.MONTH);
        int year = calendarView.getCurrentPageDate().get(Calendar.YEAR);
        Logger.log("Khanh","Current page: " + month + " - " + year);
        for (int i = 1; i <= 31; i++) {
            if (TaskManager.getInstance().existTaskOnDate(i, month+1, year)) {
                Calendar date = Calendar.getInstance();
                date.set(year,month,i);
                events.add(new EventDay(date, R.drawable.red1));
            }
        }
        calendarView.setEvents(events);
    }
}
