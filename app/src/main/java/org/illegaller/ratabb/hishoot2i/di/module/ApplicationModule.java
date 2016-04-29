package org.illegaller.ratabb.hishoot2i.di.module;

import android.app.Application;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import org.illegaller.ratabb.hishoot2i.utils.SimpleSchedulers;

@Module public class ApplicationModule {
  private final Application app;

  public ApplicationModule(Application app) {
    this.app = app;
  }

  @Provides @Singleton Application provideApplication() {
    return app;
  }

  @Provides @Singleton SimpleSchedulers provideSimpleSchedulers() {
    return new SimpleSchedulers();
  }

  @Provides @Singleton RefWatcher provideRefWatcher() {
    return LeakCanary.install(app);
  }
}
