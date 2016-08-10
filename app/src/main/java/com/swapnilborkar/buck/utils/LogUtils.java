package com.swapnilborkar.buck.utils;

import android.annotation.TargetApi;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.util.Log;

/**
 * Created by SWAPNIL on 04-08-2016.
 */
public class LogUtils {

    private static StringBuffer sStringBuffer = new StringBuffer();
    private static LogListener sLogListener;

    @TargetApi(Build.VERSION_CODES.N)
    public static void log(String tag, String message) {
        Log.d(tag, message);
        StringBuilder stringBuilder = new StringBuilder();
        String date = formatDate(Calendar.getInstance());
        stringBuilder.append(date);
        stringBuilder.append(" ");
        stringBuilder.append(tag);
        stringBuilder.append(" ");
        stringBuilder.append(message);
        stringBuilder.append("\n\n");
        sStringBuffer.insert(0, stringBuilder.toString());
        printLogs();

    }

    private static void printLogs() {
        if (sLogListener != null) {
            sLogListener.onLogged(sStringBuffer);
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static String formatDate(Calendar calender) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd hh:mm:ss");
        return simpleDateFormat.format(calender.getTime());
    }

    public static void clearLogs() {
        sStringBuffer = new StringBuffer();
        printLogs();
    }

    public static void setLogListener(LogListener logListener) {
        sLogListener = logListener;
    }

    public interface LogListener{
        void onLogged(StringBuffer log);
    }
}
