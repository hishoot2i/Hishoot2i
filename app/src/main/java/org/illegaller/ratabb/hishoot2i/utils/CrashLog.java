package org.illegaller.ratabb.hishoot2i.utils;

import android.support.annotation.Nullable;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import io.fabric.sdk.android.Fabric;
import org.illegaller.ratabb.hishoot2i.BuildConfig;

/**
 * <strong>CrashLog</strong> with:
 * <li>
 * <ol><code>logError</code> {@linkplain #logError(String, Throwable)}</ol>
 * <ol><code>log</code> {@linkplain #log(String)}</ol>
 * </li>
 */
public class CrashLog {

  static String className;
  static String methodName;
  static int lineNumber;

  private CrashLog() {
    throw new UnsupportedOperationException("no instance");
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

  static void doLog(int priority, @Nullable String msg, @Nullable Throwable e) {
    if (BuildConfig.USE_CRASHLYTICS) {
      if (!Fabric.isInitialized()) return;
      CrashlyticsCore core = Crashlytics.getInstance().core;
      if (msg != null) core.log(priority, className, createLog(msg));
      if (e != null) core.logException(e);
    } else if (BuildConfig.DEBUG && msg != null) {
      if (priority == 3) {
        Log.d(className, createLog(msg));
      } else if (priority == 6) {
        if (e != null) {
          Log.e(className, createLog(msg), e);
        } else {
          Log.e(className, createLog(msg));
        }
      }
    }
  }

  static String createLog(String log) {
    return "[" + methodName + ":" + lineNumber + "]" + log;
  }

  static void getMethodNames(StackTraceElement[] sElements) {
    className = sElements[1].getFileName();
    methodName = sElements[1].getMethodName();
    lineNumber = sElements[1].getLineNumber();
  }
}
