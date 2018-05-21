package com.group_8.app.reminder.model;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.group_8.app.reminder.R;
import com.group_8.app.reminder.activity.SplashActivity;
import com.group_8.app.reminder.activity.TaskActivity;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String jsonMyObject = "";
        Bundle extrs = intent.getExtras();
        if (extrs != null) {
            jsonMyObject = extrs.getString(ConstKey.GET_TASK);
        }

        Task intentTask = new Gson().fromJson(jsonMyObject, Task.class);

        showNotify(context,intentTask.getName(),intentTask.getDescription(), intentTask);

    }

    private void showNotify(Context context, String title, String message, Task task) {
        Bitmap bitmap = null;
        if(task.getImageLink() != null){
            Uri imageUri = Uri.parse(task.getImageLink());
            try {
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Uri uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.cogaim52);
        Intent resultIntent = new Intent(context, TaskActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        resultIntent.putExtra(ConstKey.EXTRA_TASK,new Gson().toJson(task));

        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification notification = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            builder.setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(false)
                    .setSmallIcon(getNotificationIcon())
                    .setContentIntent(resultPendingIntent);

            if(bitmap != null){
                NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap);

                builder.setLargeIcon(bitmap)
                        .setStyle(style);
            }
            builder.setSound(uri);

            notification = builder.build();
            notification.flags |= Notification.FLAG_INSISTENT;
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.notify(0, notification);
        wakeLockScreen(context);
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.logo : R.drawable.logo;
    }

    @SuppressLint("WakelockTimeout")
    private void wakeLockScreen(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        assert pm != null;
        final PowerManager.WakeLock mWakelock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "GCM_PUSH");
        mWakelock.acquire();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                mWakelock.release();
            }
        };
        timer.schedule(task, 500);
    }
}