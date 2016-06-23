package org.illegaller.ratabb.hishoot2i.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import org.illegaller.ratabb.hishoot2i.BuildConfig;

/**
 * <strong>CrashLog</strong> with:
 * <li>
 * <ol><code>logError</code> {@linkplain #logError(String, Throwable)}</ol>
 * <ol><code>log</code> {@linkplain #log(String)}</ol>
 * </li>
 */
public class CrashLog {

  private static String mClassName;
  private static String mMethodName;
  private static int mLineNumber;

  private CrashLog() {
    throw new AssertionError("no instance");
  }

  /**
   * log with only throwable
   *
   * @param e a {@link Throwable}
   */
  public static void logError(@NonNull Throwable e) {
    logError(null, e);
  }

  /**
   * log with message and throwable
   *
   * @param msg a message to log {@link String}
   * @param e a {@link Throwable}
   */
  public static void logError(@Nullable String msg, @Nullable Throwable e) {
    getMethodNames(new Throwable().getStackTrace());
    doLog(6, msg, e);
  }

  /**
   * log only message
   *
   * @param msg a message to log {@link String}
   */
  public static void log(String msg) {
    getMethodNames(new Throwable().getStackTrace());
    doLog(3, msg, null);
  }

  private static void doLog(int priority, @Nullable String msg, @Nullable Throwable e) {
    //if (BuildConfig.USE_CRASHLYTICS) {
    //  if (!Fabric.isInitialized()) return;
    //  CrashlyticsCore core = Crashlytics.getInstance().core;
    //  if (msg != null) core.log(priority, mClassName, createLog(msg));
    //  if (e != null) core.logException(e);
    //} else if (BuildConfig.DEBUG && msg != null) {
    //  if (priority == 3) {
    //    Log.d(mClassName, createLog(msg));
    //  } else if (priority == 6) {
    //    if (e != null) {
    //      Log.e(mClassName, createLog(msg), e);
    //    } else {
    //      Log.e(mClassName, createLog(msg));
    //    }
    //  }
    //}
    if (BuildConfig.DEBUG && msg != null) {
      if (priority == 3) {
        Log.d(mClassName, createLog(msg));
      } else if (priority == 6) {
        if (e != null) {
          Log.e(mClassName, createLog(msg), e);
        } else {
          Log.e(mClassName, createLog(msg));
        }
      }
    }
  }

  private static String createLog(String log) {
    return "[" + mMethodName + ":" + mLineNumber + "]" + log;
  }

  private static void getMethodNames(StackTraceElement[] sElements) {
    mClassName = sElements[1].getFileName();
    mMethodName = sElements[1].getMethodName();
    mLineNumber = sElements[1].getLineNumber();
  }
}
