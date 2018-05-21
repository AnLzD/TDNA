package com.group_8.app.reminder.adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Locale;

import com.group_8.app.reminder.R;
import com.group_8.app.reminder.fragment.HomeFragment;
import com.group_8.app.reminder.model.Logger;
import com.group_8.app.reminder.model.Task;
import com.group_8.app.reminder.model.TaskPriority;
import com.group_8.app.reminder.model.TaskProgress;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends BaseAdapter implements Filterable{
    Context context;
    List<Task> tasks;
    List<Task> orig;
    public TaskAdapter(Context context, List<Task> arrayTask){
        this.context =context;
        this.tasks =arrayTask;
    }
    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Object getItem(int position) {
        if(position >= 0 && position < tasks.size()){
            return tasks.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.item_task,parent,false);

        //gán giá tri
        TextView txtName = (TextView) convertView.findViewById(R.id.taskName);
        txtName.setText(tasks.get(position).getName());
        TextView description = convertView.findViewById(R.id.taskDecription);
        if(tasks.get(position).getDescription() != null && !tasks.get(position).getDescription().trim().equals("")) {
            description.setVisibility(View.VISIBLE);
            description.setText(tasks.get(position).getDescription());
        } else{
            description.setVisibility(View.GONE);
        }

        ImageView ivDes = convertView.findViewById(R.id.ivDescription);

        ImageView imgPriority = (ImageView) convertView.findViewById(R.id.taskPriority);
        TaskPriority a = tasks.get(position).getPriority();
        imgPriority.setColorFilter(Color.argb(a.getColorAlpha(),a.getColorRed(),a.getColorGreen(),a.getColorBlue()));


        return convertView;
    }

    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<Task> results = new ArrayList<Task>();
                if (orig == null)
                    orig = tasks;
                if (constraint != null) {
                    if (orig != null && orig.size() > 0) {
                        if(constraint.toString().contains("@#$G2018--")){
                            if(constraint.toString().contains("@#$G2018--1")){
                                for (final Task g : orig) {
                                    if (g.getPriority().getId() == 1)
                                        results.add(g);
                                }
                            }
                            if(constraint.toString().contains("@#$G2018--2")){
                                for (final Task g : orig) {
                                    if (g.getPriority().getId() == 2)
                                        results.add(g);
                                }
                            }
                            if(constraint.toString().contains("@#$G2018--3")){
                                for (final Task g : orig) {
                                    if (g.getPriority().getId() == 3)
                                        results.add(g);
                                }
                            }
                            if(constraint.toString().contains("@#$G2018--4")){
                                for (final Task g : orig) {
                                    if (g.getPriority().getId() == 4)
                                        results.add(g);
                                }
                            }
                        }else{
                            for (final Task g : orig) {
                                if (g.getName().toLowerCase()
                                        .contains(constraint.toString()))
                                    results.add(g);
                            }
                        }

                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                tasks = (List<Task>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

}
