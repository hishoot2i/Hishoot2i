package org.illegaller.ratabb.hishoot2i.di.module;

import dagger.Module;
import dagger.Provides;
import org.illegaller.ratabb.hishoot2i.di.scope.ActivityScope;
import org.illegaller.ratabb.hishoot2i.presenter.LauncherActivityPresenter;

@Module public class LauncherActivityModule {
  @Provides @ActivityScope LauncherActivityPresenter providePresenter() {
    return new LauncherActivityPresenter();
  }
}
