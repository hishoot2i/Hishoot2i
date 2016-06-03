package org.illegaller.ratabb.hishoot2i.di.module;

import android.app.Application;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import net.grandcentrix.tray.AppPreferences;

@Module public class ApplicationModule {
  private final Application mApplication;

  public ApplicationModule(Application app) {
    this.mApplication = app;
  }

  @Provides @Singleton Application provideApplication() {
    return mApplication;
  }

  @Provides @Singleton RefWatcher provideRefWatcher() {
    return LeakCanary.install(mApplication);
  }

  @Provides @Singleton AppPreferences provideAppPreferences() {
    return new AppPreferences(mApplication);
  }
}
