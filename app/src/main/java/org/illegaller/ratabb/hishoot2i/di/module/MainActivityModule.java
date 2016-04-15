package org.illegaller.ratabb.hishoot2i.di.module;

import org.illegaller.ratabb.hishoot2i.di.scope.ActivityScope;
import org.illegaller.ratabb.hishoot2i.presenter.MainActivityPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class MainActivityModule {
    @Provides @ActivityScope MainActivityPresenter providePresenter() {
        return new MainActivityPresenter();
    }
}
