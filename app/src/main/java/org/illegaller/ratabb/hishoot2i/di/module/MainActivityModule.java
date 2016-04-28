package org.illegaller.ratabb.hishoot2i.di.module;

import dagger.Module;
import dagger.Provides;
import org.illegaller.ratabb.hishoot2i.di.TemplateManager;
import org.illegaller.ratabb.hishoot2i.di.scope.ActivityScope;
import org.illegaller.ratabb.hishoot2i.presenter.MainActivityPresenter;

@Module public class MainActivityModule {
  @Provides @ActivityScope MainActivityPresenter providePresenter(TemplateManager templateManager) {
    return new MainActivityPresenter(templateManager);
  }
}
