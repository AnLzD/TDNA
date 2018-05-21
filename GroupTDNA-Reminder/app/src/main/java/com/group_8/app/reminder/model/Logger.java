package com.group_8.app.reminder.model;

import android.util.Log;

import com.group_8.app.reminder.BuildConfig;

public class Logger {
    public static void log(String tag,String msg){
        if(BuildConfig.DEBUG){
            Log.i(tag,msg);
        }
    }

    public static void error(String tag,String msg){
        if(BuildConfig.DEBUG){
            Log.e(tag,msg);
        }
    }
}
