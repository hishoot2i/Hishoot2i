package org.illegaller.ratabb.hishoot2i.di.compenent;

import org.illegaller.ratabb.hishoot2i.di.module.LauncherActivityModule;
import org.illegaller.ratabb.hishoot2i.di.scope.ActivityScope;
import org.illegaller.ratabb.hishoot2i.view.LauncherActivity;

import dagger.Component;

@ActivityScope @Component(
        modules = LauncherActivityModule.class
)
public interface LauncherActivityComponent {
    void inject(LauncherActivity activity);
}
