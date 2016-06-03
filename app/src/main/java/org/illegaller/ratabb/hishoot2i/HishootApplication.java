package org.illegaller.ratabb.hishoot2i;

import android.app.Application;
import android.content.Context;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import com.crashlytics.android.Crashlytics;
import com.frogermcs.androiddevmetrics.AndroidDevMetrics;
import io.fabric.sdk.android.Fabric;
import java.util.Locale;
import javax.inject.Inject;
import org.illegaller.ratabb.hishoot2i.di.compenent.AppComponent;
import org.illegaller.ratabb.hishoot2i.model.tray.IntTray;
import org.illegaller.ratabb.hishoot2i.model.tray.TrayManager;
import org.illegaller.ratabb.hishoot2i.utils.CrashLog;
import org.illegaller.ratabb.hishoot2i.utils.UILHelper;
import org.illegaller.ratabb.hishoot2i.view.LauncherActivity;

public class HishootApplication extends Application {
  @Inject TrayManager mTrayManager;
  private AppComponent mAppComponent;

  public static HishootApplication get(Context context) {
    return (HishootApplication) context.getApplicationContext();
  }

  public TrayManager getTrayManager() {
    return mTrayManager;
  }

  public AppComponent getAppComponent() {
    if (mAppComponent == null) mAppComponent = AppComponent.Initializer.init(this);
    return mAppComponent;
  }

  public boolean analyticEnable() {
    return !BuildConfig.DEBUG && mTrayManager.getCrashlyticEnable().isValue();
  }

  @Override public void onCreate() {
    super.onCreate();
    if (BuildConfig.DEBUG) AndroidDevMetrics.initWith(this);
    setupInjection();
    setupCAOC();

    if (analyticEnable()) Fabric.with(this, new Crashlytics());

    UILHelper.init(this, mTrayManager.getDeviceWidth().getValue(),
        mTrayManager.getDeviceHeight().getValue());
    logCount();
  }

  void logCount() {
    final IntTray appRunningCount = mTrayManager.getAppRunningCount();
    final String logFormat = "Device name: %s\n OS: %s\n runningCount: %d";
    appRunningCount.setValue(appRunningCount.getValue() + 1);
    CrashLog.log(String.format(Locale.US, logFormat, mTrayManager.getDeviceName(),
        mTrayManager.getDeviceOS(), appRunningCount.getValue()));
  }

  void setupCAOC() {
    CustomActivityOnCrash.setLaunchErrorActivityWhenInBackground(true);
    CustomActivityOnCrash.setRestartActivityClass(LauncherActivity.class);
    CustomActivityOnCrash.setShowErrorDetails(true);
    CustomActivityOnCrash.install(this);
  }

  void setupInjection() {
    mAppComponent = getAppComponent();
    mAppComponent.inject(this);
  }
}
