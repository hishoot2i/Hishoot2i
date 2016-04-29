package org.illegaller.ratabb.hishoot2i.di.compenent;

import dagger.Component;
import javax.inject.Singleton;
import org.illegaller.ratabb.hishoot2i.HishootApplication;
import org.illegaller.ratabb.hishoot2i.di.module.ApplicationModule;
import org.illegaller.ratabb.hishoot2i.di.module.SystemServiceModule;
import org.illegaller.ratabb.hishoot2i.di.module.TrayModule;

@Singleton
@Component(modules = { ApplicationModule.class, SystemServiceModule.class, TrayModule.class })
public interface ApplicationComponent extends ApplicationGraph {

  final class Initializer {
    private Initializer() {
      throw new UnsupportedOperationException();
    }

    public static ApplicationComponent init(HishootApplication app) {
      return DaggerApplicationComponent.builder()
          .applicationModule(new ApplicationModule(app))
          .systemServiceModule(new SystemServiceModule())
          .trayModule(new TrayModule())
          .build();
    }
  }
}
