package org.illegaller.ratabb.hishoot2i;

import android.app.Application;
import android.content.Context;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import com.frogermcs.androiddevmetrics.AndroidDevMetrics;
import java.util.Locale;
import javax.inject.Inject;
import org.illegaller.ratabb.hishoot2i.di.compenent.AppComponent;
import org.illegaller.ratabb.hishoot2i.model.tray.IntTray;
import org.illegaller.ratabb.hishoot2i.model.tray.TrayManager;
import org.illegaller.ratabb.hishoot2i.utils.CrashLog;
import org.illegaller.ratabb.hishoot2i.utils.SimpleSchedule;
import org.illegaller.ratabb.hishoot2i.utils.UILHelper;
import org.illegaller.ratabb.hishoot2i.view.LauncherActivity;
import rx.Observable;

import static org.illegaller.ratabb.hishoot2i.utils.Utils.avoidUiThread;
import static org.illegaller.ratabb.hishoot2i.utils.Utils.enableStrictMode;

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

  @Override public void onCreate() {
    super.onCreate();
    enableStrictMode();  // Debugging with StrictMode
    if (BuildConfig.DEBUG) AndroidDevMetrics.initWith(this);

    setupCAOC();
    injectOnBackground().compose(SimpleSchedule.schedule()).subscribe(o -> { /*empty*/
    }, throwable -> CrashLog.logError("onInjection", throwable), this::afterInjection);
  }

  private void setupCAOC() {
    CustomActivityOnCrash.setLaunchErrorActivityWhenInBackground(true);
    CustomActivityOnCrash.setRestartActivityClass(LauncherActivity.class);
    CustomActivityOnCrash.setShowErrorDetails(true);
    CustomActivityOnCrash.install(this);
  }

  private void performInjection() throws IllegalStateException {
    avoidUiThread("performInjection on main thread");
    getAppComponent().inject(this);
  }

  private void afterInjection() {
    final TrayManager trayManager = getTrayManager();
    //if (analyticEnable(trayManager)) Fabric.with(this, new Crashlytics());
    UILHelper.init(this, trayManager.getDeviceWidth().getValue(),
        trayManager.getDeviceHeight().getValue());
    logCount(trayManager);
  }

  private boolean analyticEnable(TrayManager trayManager) {
    return !BuildConfig.DEBUG && trayManager.getAnalyticsEnable().isValue();
  }

  private void logCount(TrayManager trayManager) {
    final IntTray appRunningCount = trayManager.getAppRunningCount();
    final String logFormat = "Device name: %s\nOS: %s\nrunningCount: %d";
    appRunningCount.setValue(appRunningCount.getValue() + 1);
    CrashLog.log(
        String.format(Locale.US, logFormat, trayManager.getDeviceName(), trayManager.getDeviceOS(),
            appRunningCount.getValue()));
  }

  private Observable<Void> injectOnBackground() {
    return Observable.create(subscriber -> {
      try {
        performInjection();
        subscriber.onCompleted();
      } catch (IllegalStateException e) {
        subscriber.onError(e);
      }
    });
  }
}
