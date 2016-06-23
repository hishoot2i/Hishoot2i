package org.illegaller.ratabb.hishoot2i.di.module;

import android.app.Application;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import org.illegaller.ratabb.hishoot2i.BuildConfig;

@Module public class ApplicationModule {
  private final Application mApplication;

  public ApplicationModule(Application app) {
    this.mApplication = app;
  }

  @Provides @Singleton Application provideApplication() {
    return mApplication;
  }

  @Provides @Singleton RefWatcher provideRefWatcher() {
    return (BuildConfig.DEBUG) ? LeakCanary.install(mApplication) : RefWatcher.DISABLED;
  }
}
