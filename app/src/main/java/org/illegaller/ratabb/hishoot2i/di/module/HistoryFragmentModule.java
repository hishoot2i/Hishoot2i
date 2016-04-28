package org.illegaller.ratabb.hishoot2i.di.module;

import dagger.Module;
import dagger.Provides;
import org.illegaller.ratabb.hishoot2i.presenter.HistoryFragmentPresenter;
import org.illegaller.ratabb.hishoot2i.utils.SimpleSchedulers;

@Module public class HistoryFragmentModule {
  @Provides HistoryFragmentPresenter provideHistoryFragmentPresenter(SimpleSchedulers schedulers) {
    return new HistoryFragmentPresenter(schedulers);
  }
}
