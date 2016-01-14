package org.illegaller.ratabb.hishoot2i.utils;

import org.illegaller.ratabb.hishoot2i.BuildConfig;

import android.util.Log;

public class HLog {
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static String LOG_TAG = "UNDEFINE_TAG";

    private HLog() {
        throw new AssertionError("HLog no construction");
    }

    public static void setTAG(Class clazz) {
        HLog.setTAG(clazz.getSimpleName());
    }

    public static void setTAG(Object object) {
        HLog.setTAG(object.getClass());
    }

    public static void setTAG(String tag) {
        HLog.LOG_TAG = tag;
    }

    public static void d(Object o) {
        HLog.d(o, null);
    }

    public static void d(Object o, Throwable e) {
        if (!HLog.DEBUG) return;
        String msg = nullOrString(o);
        if (null == e && msg != null) Log.d(HLog.LOG_TAG, msg);
        else Log.d(HLog.LOG_TAG, msg, e);
    }

    public static void e(Object o) {
        HLog.e(o, null);
    }

    public static void e(Object o, Throwable e) {
        //  if (!DEBUG) return;
        String msg = nullOrString(o);
        if (null == e && msg != null) Log.e(HLog.LOG_TAG, msg);
        else Log.e(HLog.LOG_TAG, msg, e);
    }

    public static void i(Object o) {
        HLog.i(o, null);
    }

    public static void i(Object o, Throwable e) {
//        if (!DEBUG) return;
        String msg = nullOrString(o);
        if (null == e && msg != null) Log.i(HLog.LOG_TAG, msg);
        else Log.i(HLog.LOG_TAG, msg, e);
    }

    public static void w(Object o) {
        HLog.w(o, null);
    }

    public static void w(Throwable e) {
        HLog.w(null, e);
    }

    public static void w(Object o, Throwable e) {
//        if (!DEBUG) return;
        String msg = nullOrString(o);
        if (null != e && null != msg) Log.w(HLog.LOG_TAG, msg, e);
        else if (null == e && msg != null) Log.w(HLog.LOG_TAG, msg);
        else if (null != e) Log.w(LOG_TAG, e);
    }

    public static void wtf(Object o) {
        HLog.wtf(o, null);
    }

    public static void wtf(Throwable e) {
        HLog.wtf(null, e);
    }

    public static void wtf(Object o, Throwable e) {
//        if (!DEBUG) return;
        String msg = nullOrString(o);
        if (null != e && null != msg) Log.wtf(HLog.LOG_TAG, msg, e);
        else if (null == e && msg != null) Log.wtf(HLog.LOG_TAG, msg);
        else if (null != e) Log.wtf(LOG_TAG, e);
    }

    private static String nullOrString(Object o) {
        return o != null ?
                (o instanceof String ? (String) o : String.valueOf(o))
                : null;
    }
}
