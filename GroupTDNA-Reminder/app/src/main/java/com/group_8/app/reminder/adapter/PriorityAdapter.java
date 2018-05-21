package com.group_8.app.reminder.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.group_8.app.reminder.R;
import com.group_8.app.reminder.model.Logger;
import com.group_8.app.reminder.model.TaskPriority;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by JUNED on 6/10/2016.
 */
public class PriorityAdapter extends RecyclerView.Adapter<PriorityAdapter.PriorityHolder> {
    public interface OnClickListener {
        void onClick(int position);
    }

    public class PriorityHolder extends RecyclerView.ViewHolder     {
        @BindView(R.id.ib_priority)
        ImageButton priority;
        View container;

        public PriorityHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            container = view;
            priority.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Logger.log("Task","Click item");
                    TaskPriority p = priorities.get(getAdapterPosition());
                    if(mOnClickListener != null){
                        activePosition = getAdapterPosition();
                        notifyDataSetChanged();
                        mOnClickListener.onClick(activePosition);
                    }
                }
            });
        }
    }

    private List<TaskPriority> priorities;
    private Context context;
    private OnClickListener mOnClickListener;
    private int activePosition = 1;

    public PriorityAdapter(Context context, List<TaskPriority> priorities, OnClickListener listener) {
        this.context = context;
        this.priorities = priorities;
        this.mOnClickListener = listener;
    }

    public void setActive(int position){
        activePosition = position;
    }

    @Override
    public PriorityAdapter.PriorityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_priority, parent, false);
        return new PriorityHolder(view);
    }

    @Override
    public void onBindViewHolder(PriorityHolder holder, int position) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        long scale = (long) displayMetrics.density;
        int height = (int) (50 * scale);
        int width = displayMetrics.widthPixels - 50;

        if(position == activePosition){
            holder.priority.setImageResource(R.drawable.ic_circle_white);
        } else{
            holder.priority.setImageResource(R.drawable.ic_circle_white_outline);
        }

        TaskPriority p = priorities.get(position);
        holder.container.setLayoutParams(new LinearLayout.LayoutParams(width / priorities.size(),  height));
        holder.priority.
                setColorFilter(
                        Color.argb(p.getColorAlpha(), p.getColorRed(), p.getColorGreen(), p.getColorBlue()));
    }

    @Override
    public int getItemCount() {
        return priorities != null?priorities.size():0;
    }
}
