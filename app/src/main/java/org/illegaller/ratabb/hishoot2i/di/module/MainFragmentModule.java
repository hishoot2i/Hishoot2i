package org.illegaller.ratabb.hishoot2i.di.module;

import org.illegaller.ratabb.hishoot2i.presenter.MainFragmentPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class MainFragmentModule {
    @Provides MainFragmentPresenter providePresenter() {
        return new MainFragmentPresenter();
    }
}
