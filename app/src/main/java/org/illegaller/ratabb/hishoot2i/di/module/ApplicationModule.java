package org.illegaller.ratabb.hishoot2i.di.module;

import android.app.Application;
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

  @Provides SimpleSchedulers provideSimpleSchedulers() {
    return new SimpleSchedulers();
  }
}
