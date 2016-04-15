package org.illegaller.ratabb.hishoot2i.di.module;

import org.illegaller.ratabb.hishoot2i.di.scope.ActivityScope;
import org.illegaller.ratabb.hishoot2i.presenter.LauncherActivityPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class LauncherActivityModule {
    @Provides @ActivityScope LauncherActivityPresenter providePresenter() {
        return new LauncherActivityPresenter();
    }
}
