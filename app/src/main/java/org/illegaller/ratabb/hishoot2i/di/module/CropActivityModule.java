package org.illegaller.ratabb.hishoot2i.di.module;

import dagger.Module;
import dagger.Provides;
import org.illegaller.ratabb.hishoot2i.di.scope.ActivityScope;
import org.illegaller.ratabb.hishoot2i.presenter.CropActivityPresenter;
import org.illegaller.ratabb.hishoot2i.utils.SimpleSchedulers;

@Module public class CropActivityModule {
  @Provides @ActivityScope CropActivityPresenter providePresenter(SimpleSchedulers schedulers) {
    return new CropActivityPresenter(schedulers);
  }
}
