package org.illegaller.ratabb.hishoot2i.di.compenent;

import dagger.Component;
import javax.inject.Singleton;
import org.illegaller.ratabb.hishoot2i.HishootApplication;
import org.illegaller.ratabb.hishoot2i.di.module.ApplicationModule;
import org.illegaller.ratabb.hishoot2i.di.module.SystemServiceModule;
import org.illegaller.ratabb.hishoot2i.model.tray.TrayModule;

@Singleton
@Component(modules = { ApplicationModule.class, TrayModule.class, SystemServiceModule.class })
public interface AppComponent extends AppGraph {
  final class Initializer {
    private Initializer() {
      throw new UnsupportedOperationException();
    }

    public static AppComponent init(HishootApplication app) {
      return DaggerAppComponent.builder()
          .applicationModule(new ApplicationModule(app))
          .systemServiceModule(new SystemServiceModule())
          .trayModule(new TrayModule(app))
          .build();
    }
  }
}
