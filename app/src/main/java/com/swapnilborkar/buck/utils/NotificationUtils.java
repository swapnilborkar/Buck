package com.swapnilborkar.buck.utils;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.swapnilborkar.buck.Constants;
import com.swapnilborkar.buck.MainActivity;
import com.swapnilborkar.buck.R;

import java.util.List;

/**
 * Created by SWAPNIL on 10-09-2016.
 */
public class NotificationUtils {

    public static void showNotificationMessage(Context context, String title, String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }

        if (isAppInBackground(context)) {
            int icon = R.mipmap.ic_show_chart_white_48dp;
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

            Notification notification = builder
                    .setSmallIcon(icon)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setWhen(0)
                    .setAutoCancel(true)
                    .setContentIntent(resultPendingIntent)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                    .build();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(Constants.NOTIFICATION_ID, notification);
        } else {
            Toast.makeText(context, title, Toast.LENGTH_LONG).show();
        }

    }

    public static boolean isAppInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        //IF BUILD VERSION > KITKAT
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses =
                    activityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }

        }

        //IF BUILD VERSION <= KITKAT
        else {
            List<ActivityManager.RunningTaskInfo> taskInfo = activityManager.getRunningTasks(1);
            ComponentName componentName = taskInfo.get(0).topActivity;
            if (componentName.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }
        return isInBackground;
    }
}
