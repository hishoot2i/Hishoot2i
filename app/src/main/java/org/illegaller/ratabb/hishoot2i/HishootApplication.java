package org.illegaller.ratabb.hishoot2i;

import android.app.Application;
import android.content.Context;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import com.crashlytics.android.Crashlytics;
import com.frogermcs.androiddevmetrics.AndroidDevMetrics;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import io.fabric.sdk.android.Fabric;
import javax.inject.Inject;
import javax.inject.Named;
import org.illegaller.ratabb.hishoot2i.di.compenent.ApplicationComponent;
import org.illegaller.ratabb.hishoot2i.di.compenent.DaggerApplicationComponent;
import org.illegaller.ratabb.hishoot2i.di.module.ApplicationModule;
import org.illegaller.ratabb.hishoot2i.model.tray.BooleanTray;
import org.illegaller.ratabb.hishoot2i.model.tray.IntTray;
import org.illegaller.ratabb.hishoot2i.model.tray.StringTray;
import org.illegaller.ratabb.hishoot2i.utils.CrashLog;
import org.illegaller.ratabb.hishoot2i.utils.UILHelper;
import org.illegaller.ratabb.hishoot2i.view.LauncherActivity;

import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.APP_RUNNING_COUNT;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.CRASHLYTIC_ENABLE;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.DEVICE_HEIGHT;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.DEVICE_NAME;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.DEVICE_OS;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.DEVICE_WIDTH;

public class HishootApplication extends Application {
  @Inject @Named(CRASHLYTIC_ENABLE) BooleanTray crashlyticEnableTray;
  @Inject @Named(APP_RUNNING_COUNT) IntTray appRunningCountTray;
  @Inject @Named(DEVICE_HEIGHT) IntTray deviceHeightTray;
  @Inject @Named(DEVICE_WIDTH) IntTray deviceWidthTray;
  @Inject @Named(DEVICE_NAME) StringTray deviceNameTray;
  @Inject @Named(DEVICE_OS) StringTray deviceOSTray;
  private ApplicationComponent applicationComponent;
  private RefWatcher mWatcher;

  public static HishootApplication get(Context context) {
    return (HishootApplication) context.getApplicationContext();
  }

  public RefWatcher getWatcher() {
    return mWatcher;
  }

  public synchronized ApplicationComponent getApplicationComponent() {
    if (applicationComponent == null) {
      applicationComponent = DaggerApplicationComponent.builder()
          .applicationModule(new ApplicationModule(this))
          .build();
    }
    return applicationComponent;
  }

  @Override public void onCreate() {
    super.onCreate();
    if (BuildConfig.DEBUG) AndroidDevMetrics.initWith(this);
    setupInjection();
    setupCAOC();
    CrashLog.setCrashlyticsEnable(crashlyticEnableTray.get());
    if (BuildConfig.USE_CRASHLYTICS && crashlyticEnableTray.get()) {
      CrashLog.setCrashlyticsEnable(true);
      Fabric.with(this, new Crashlytics());
    }
    UILHelper.init(this, deviceWidthTray.get(), deviceHeightTray.get());
    mWatcher = LeakCanary.install(this);
    logCount();
  }

  void logCount() {
    appRunningCountTray.set(appRunningCountTray.get() + 1);
    CrashLog.log("Device name: "
        + deviceNameTray.get()
        + " OS: "
        + deviceOSTray.get()
        + " runningCount:"
        + appRunningCountTray.get());
  }

  void setupCAOC() {
    CustomActivityOnCrash.setLaunchErrorActivityWhenInBackground(true);
    CustomActivityOnCrash.setRestartActivityClass(LauncherActivity.class);
    CustomActivityOnCrash.setShowErrorDetails(true);
    CustomActivityOnCrash.install(this);
  }

  void setupInjection() {
    applicationComponent = getApplicationComponent();
    applicationComponent.inject(this);
  }
}
