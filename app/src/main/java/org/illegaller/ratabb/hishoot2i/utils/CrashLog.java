package org.illegaller.ratabb.hishoot2i.utils;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;

import org.illegaller.ratabb.hishoot2i.BuildConfig;

import android.support.annotation.Nullable;
import android.util.Log;

public class CrashLog {

    static String className;
    static String methodName;
    static int lineNumber;

    private CrashLog() {        //no instance
    }

    public static void logError(@Nullable String msg, @Nullable Throwable e) {
        getMethodNames(new Throwable().getStackTrace());
        doLog(6, msg, e);
    }

    public static void log(@Nullable String msg) {
        getMethodNames(new Throwable().getStackTrace());
        doLog(3, msg, null);
    }

    static void doLog(int priority, @Nullable String msg, @Nullable Throwable e) {
        if (BuildConfig.USE_CRASHLYTICS) {
            CrashlyticsCore core = Crashlytics.getInstance().core;
            if (msg != null) core.log(priority, className, createLog(msg));
            if (e != null) core.logException(e);
        } else if (BuildConfig.DEBUG && msg != null) {
            if (priority == 3) Log.d(className, createLog(msg));
            else if (priority == 6) {
                if (e != null) Log.e(className, createLog(msg), e);
                else Log.e(className, createLog(msg));
            }
        }
    }

    static String createLog(String log) {
        return "[" + methodName + ":" + lineNumber + "]" + log;
    }
    static void getMethodNames(StackTraceElement[] sElements){
        className = sElements[1].getFileName();
        methodName = sElements[1].getMethodName();
        lineNumber = sElements[1].getLineNumber();
    }
}
