package org.illegaller.ratabb.hishoot2i.di.compenent;

import org.illegaller.ratabb.hishoot2i.HishootApplication;
import org.illegaller.ratabb.hishoot2i.HishootService;
import org.illegaller.ratabb.hishoot2i.di.module.AppModule;
import org.illegaller.ratabb.hishoot2i.di.module.SystemServiceModule;
import org.illegaller.ratabb.hishoot2i.model.template.builder.DefaultBuilder;
import org.illegaller.ratabb.hishoot2i.model.tray.TrayManager;

import android.app.Application;
import android.view.WindowManager;

import javax.inject.Singleton;

import dagger.Component;

@Singleton @Component(
        modules = {
                AppModule.class, SystemServiceModule.class
        }
)
public interface AppComponent {
    void inject(HishootApplication app);

    void inject(DefaultBuilder defaultBuilder);

    void inject(HishootService service);

    Application application();

    WindowManager windowManager();

    TrayManager trayManager();
}
