package org.illegaller.ratabb.hishoot2i.di.module;

import org.illegaller.ratabb.hishoot2i.model.tray.TrayManager;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private final Application app;

    public AppModule(Application app) {
        this.app = app;
    }

    @Provides @Singleton Application provideApplication() {
        return app;
    }

    @Provides @Singleton TrayManager provideTrayManager() {
        return new TrayManager(app);
    }
}
