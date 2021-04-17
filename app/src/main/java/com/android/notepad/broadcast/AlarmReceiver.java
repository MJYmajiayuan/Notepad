package com.android.notepad.broadcast;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.android.notepad.R;
import com.android.notepad.ui.edit.EditActivity;

public class AlarmReceiver extends BroadcastReceiver {

    private int intentCode = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null
                && intent.getAction().equals("com.android.notepad.alarm")) {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("alarm", "alarm", NotificationManager.IMPORTANCE_HIGH);
                manager.createNotificationChannel(channel);
            }
            // 取出Intent中的id和content
            int noteId = intent.getIntExtra("noteId", 0);
            Log.d("AlarmReceiver", "noteId" + noteId);
            String noteContent = intent.getStringExtra("noteContent");
            intent = new Intent(context, EditActivity.class);
            // 为新的intent设置note的id
            intent.putExtra("noteId", noteId);
            intent.putExtra("tag", "update");
            PendingIntent pi = PendingIntent.getActivity(context, intentCode++, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            Notification notification = new NotificationCompat.Builder(context, "alarm")
                    .setSmallIcon(R.drawable.ic_baseline_label_24)
                    .setContentTitle("设置的提醒")
                    .setContentText(noteContent)
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .build();
            manager.notify(1, notification);
        }
    }
}