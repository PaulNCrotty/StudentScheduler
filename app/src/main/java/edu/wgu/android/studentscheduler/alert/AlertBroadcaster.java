package edu.wgu.android.studentscheduler.alert;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

public class AlertBroadcaster extends BroadcastReceiver {

    public static final String NOTIFICATION_ID = "edu.wgu.android.studentscheduler.alert.AlertRequester";
    public static final String NOTIFICATION_COLOR_KEY = "edu.wgu.android.studentscheduler.alert.lightColor";
    public static final String NOTIFICATION_KEY = "edu.wgu.android.studentscheduler.alert.AlertRequester.Notification";

    private static final String NOTIFICATION_CHANNEL = "NotificationChannel";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        int id = extras.getInt(NOTIFICATION_ID);
        int lightColor = extras.getInt(NOTIFICATION_COLOR_KEY);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = intent.getParcelableExtra(NOTIFICATION_KEY);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_ID, NOTIFICATION_CHANNEL, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setLightColor(lightColor);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(id, notification);
    }
}
