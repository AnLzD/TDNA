package com.group_8.app.reminder.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class FinderFragment extends Fragment {
    @BindView(R.id.listOfResults)
    ListView lvResults;

    List<Task> listAllTask;
    List<Task> listResult;

    TaskAdapter resultAdapter;

    SearchView searchView;
    CheckBox    cb1, cb2, cb3, cb4;
    LinearLayout linearLayout1;
    private boolean isStartOtherActivity = false;


    public FinderFragment() {

        listAllTask = TaskManager.getInstance().getAllTasks();
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

                    for(int i=0;i<listAllTask.size();i++){
                        if(task.getId()==listAllTask.get(i).getId()){
                            listAllTask.remove(i);
                            break;
                        }
                    }
                    resultAdapter.notifyDataSetChanged();

            }

        });
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_finder, container, false);
        searchView = (SearchView) view.findViewById(R.id.search_view);
        cb1 = (CheckBox) view.findViewById(R.id.checkBox1);
        cb2 = (CheckBox) view.findViewById(R.id.checkBox2);
        cb3 = (CheckBox) view.findViewById(R.id.checkBox3);
        cb4 = (CheckBox) view.findViewById(R.id.checkBox4);
        linearLayout1 = (LinearLayout)  view.findViewById(R.id.LinearLayout1);

        ButterKnife.bind(this, view);

        resultAdapter = new TaskAdapter(
                getContext(),
                listAllTask
        );
        lvResults.setAdapter(resultAdapter);
        lvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                openTask(listAllTask.get(position));
                //searchView.setQueryHint("Search");
            }
        });
        lvResults.setTextFilterEnabled(true);
        setupSearchView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                    //linearLayout1.setVisibility(View.GONE);
                    cb1.setChecked(false);
                cb2.setChecked(false);
                cb3.setChecked(false);
                cb4.setChecked(false);
                    resultAdapter.getFilter().filter(newText);
                    return false;

            }
        });
        //
        cb1.setOnClickListener(new View.OnClickListener(){
            @Override
            //set a click event for CheckBox Pink
            public void onClick(View v){
                String str = "";
                if(cb1.isChecked())
                {
                    str = "@#$G2018--1";
                    searchView.setQuery("",false);
                    searchView.clearFocus();
		            cb1.setChecked(true);
                }
                if(cb2.isChecked())
                {
                    str += "@#$G2018--2";
                }
                if(cb3.isChecked())
                {
                    str += "@#$G2018--3";
                }
                if(cb4.isChecked())
                {
                    str += "@#$G2018--4";
                }
                resultAdapter.getFilter().filter(str);
            }
        });
        cb4.setOnClickListener(new View.OnClickListener(){
            @Override
            //set a click event for CheckBox Pink
            public void onClick(View v){
                String str = "";
                if(cb1.isChecked())
                {
                    str = "@#$G2018--1";
                }
                if(cb2.isChecked())
                {
                    str += "@#$G2018--2";
                }
                if(cb3.isChecked())
                {
                    str += "@#$G2018--3";
                }
                if(cb4.isChecked())
                {
                    str += "@#$G2018--4";
                    searchView.setQuery("",false);
                    searchView.clearFocus();
                    cb4.setChecked(true);
                }
                resultAdapter.getFilter().filter(str);
            }
        });
        cb2.setOnClickListener(new View.OnClickListener(){
            @Override
            //set a click event for CheckBox Pink
            public void onClick(View v){
                String str = "";
                if(cb1.isChecked())
                {
                    str = "@#$G2018--1";
                }
                if(cb2.isChecked())
                {
                    str += "@#$G2018--2";
                    searchView.setQuery("",false);
                    searchView.clearFocus();
                    cb2.setChecked(true);
                }
                if(cb3.isChecked())
                {
                    str += "@#$G2018--3";
                }
                if(cb4.isChecked())
                {
                    str += "@#$G2018--4";
                }
                resultAdapter.getFilter().filter(str);
            }
        });
        cb3.setOnClickListener(new View.OnClickListener(){
            @Override
            //set a click event for CheckBox Pink
            public void onClick(View v){
                String str = "";
                if(cb1.isChecked())
                {
                    str = "@#$G2018--1";
                }
                if(cb2.isChecked())
                {
                    str += "@#$G2018--2";
                }
                if(cb3.isChecked())
                {
                    str += "@#$G2018--3";
                    searchView.setQuery("",false);
                    searchView.clearFocus();
                    cb3.setChecked(true);
                }
                if(cb4.isChecked())
                {
                    str += "@#$G2018--4";
                }
                resultAdapter.getFilter().filter(str);
            }
        });


        return view;
    }

    private void setupSearchView() {
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("Search Here");
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
}
