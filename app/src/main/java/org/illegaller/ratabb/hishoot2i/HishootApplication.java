package org.illegaller.ratabb.hishoot2i;

import com.crashlytics.android.Crashlytics;
import com.frogermcs.androiddevmetrics.AndroidDevMetrics;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import org.illegaller.ratabb.hishoot2i.di.compenent.AppComponent;
import org.illegaller.ratabb.hishoot2i.di.compenent.DaggerAppComponent;
import org.illegaller.ratabb.hishoot2i.di.module.AppModule;
import org.illegaller.ratabb.hishoot2i.model.tray.TrayManager;
import org.illegaller.ratabb.hishoot2i.utils.CrashLog;
import org.illegaller.ratabb.hishoot2i.utils.UILHelper;
import org.illegaller.ratabb.hishoot2i.view.LauncherActivity;

import android.app.Application;
import android.content.Context;

import javax.inject.Inject;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import io.fabric.sdk.android.Fabric;

public class HishootApplication extends Application {
    @Inject TrayManager mTrayManager;
    private AppComponent component;
    private RefWatcher mWatcher;

    public static HishootApplication get(Context context) {
        return (HishootApplication) context.getApplicationContext();
    }

    public RefWatcher getWatcher() {
        return mWatcher;
    }

    public AppComponent getComponent() {
        return component;
    }

    @Override public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) AndroidDevMetrics.initWith(this);
        setupInjection();
        setupCAOC();
        if (BuildConfig.USE_CRASHLYTICS) Fabric.with(this, new Crashlytics());
        UILHelper.init(
                this,
                mTrayManager.getDeviceWidthTray().get(),
                mTrayManager.getDeviceHeightTray().get()
        );
        mWatcher = LeakCanary.install(this);
        logCount();
    }

    private void logCount() {
        mTrayManager.getAppRunningCountTray().set(mTrayManager.getAppRunningCountTray().get() + 1);
        CrashLog.log("Device: " + mTrayManager.getDeviceNameTray().get()
                + " " + mTrayManager.getDeviceOSTray().get()
                + " runningCount:" + mTrayManager.getAppRunningCountTray().get());
    }

    private void setupCAOC() {
        CustomActivityOnCrash.setLaunchErrorActivityWhenInBackground(true);
        CustomActivityOnCrash.setRestartActivityClass(LauncherActivity.class);
        CustomActivityOnCrash.setShowErrorDetails(true);
        CustomActivityOnCrash.install(this);
    }

    private void setupInjection() {
        component = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
        component.inject(this);
    }
}
