package edu.wgu.android.studentscheduler.alert;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import edu.wgu.android.studentscheduler.R;

public class AlertRequester extends BroadcastReceiver {

    public static final String NOTIFICATION_ID = "edu.wgu.android.studentscheduler.alert.AlertRequester";
    public static final String NOTIFICATION_KEY = "edu.wgu.android.studentscheduler.alert.AlertRequester.Notification";

    private static final String NOTIFICATION_CHANNEL = "NotificationChannel";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = intent.getParcelableExtra(NOTIFICATION_KEY);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_ID, NOTIFICATION_CHANNEL, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setLightColor(R.color.orange_dream);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(channel);
        }

        Bundle extras = intent.getExtras();
        int id = extras.getInt(NOTIFICATION_ID);
        notificationManager.notify(id, notification);
    }
}
