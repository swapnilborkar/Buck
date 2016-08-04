package com.swapnilborkar.buck.utils;

/**
 * Created by SWAPNIL on 04-08-2016.
 */
public class LogUtils {

    private static StringBuffer sStringBuffer = new StringBuffer();

    public interface LogListener{
        void onLogged(StringBuffer log);
    }

    private static LogListener sLogListener;
}
