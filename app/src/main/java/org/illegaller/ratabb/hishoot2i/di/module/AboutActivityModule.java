package org.illegaller.ratabb.hishoot2i.di.module;

import dagger.Module;
import dagger.Provides;
import org.illegaller.ratabb.hishoot2i.di.scope.ActivityScope;
import org.illegaller.ratabb.hishoot2i.presenter.AboutActivityPresenter;

@Module public class AboutActivityModule {
  @Provides @ActivityScope AboutActivityPresenter providePresenter() {
    return new AboutActivityPresenter();
  }
}
