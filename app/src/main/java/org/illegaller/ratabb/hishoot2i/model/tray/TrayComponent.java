package org.illegaller.ratabb.hishoot2i.model.tray;

import javax.inject.Singleton;

import dagger.Component;

@Singleton @Component(modules = TrayModule.class)
public interface TrayComponent {
    void inject(TrayManager manager);
}
