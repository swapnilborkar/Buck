package com.swapnilborkar.buck.utils;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;

/**
 * Created by SWAPNIL on 10-09-2016.
 */
public class AlarmUtils {

    public static final String LOG_TAG = AlarmUtils.class.getName();
    private static final int[] REPEAT_TIME = new int[]{60, 120, 300, 1200, 3600, 86400};
    private static AlarmManager sAlarmManager;
    private static PendingIntent sPendingIntent;

    @TargetApi(Build.VERSION_CODES.N)
    public static void startService(Context context, Intent intent, REPEAT repeat) {
        stopService();
        sPendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        sAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        sAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(),
                REPEAT_TIME[repeat.ordinal()] * 1000, sPendingIntent);
        LogUtils.log(LOG_TAG, "Alarm has been started! " + repeat);
    }

    public static void stopService() {
        if (sPendingIntent != null && sAlarmManager != null) {
            sAlarmManager.cancel(sPendingIntent);
            LogUtils.log(LOG_TAG, "Alarm has been stopped!");
        }
    }

    public enum REPEAT {
        REPEAT_EVERY_MINUTE, REPEAT_EVERY_2_MINUTES, REPEAT_EVERY_5_MINUTES,
        REPEAT_EVERY_20_MINUTES, REPEAT_EVERY_HOUR, REPEAT_EVERYDAY
    }

}
