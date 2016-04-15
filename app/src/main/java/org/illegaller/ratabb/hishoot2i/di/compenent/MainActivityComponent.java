package org.illegaller.ratabb.hishoot2i.di.compenent;

import org.illegaller.ratabb.hishoot2i.di.module.MainActivityModule;
import org.illegaller.ratabb.hishoot2i.di.scope.ActivityScope;
import org.illegaller.ratabb.hishoot2i.view.MainActivity;

import dagger.Component;

@ActivityScope @Component(modules = MainActivityModule.class)
public interface MainActivityComponent {
    void inject(MainActivity activity);
}
