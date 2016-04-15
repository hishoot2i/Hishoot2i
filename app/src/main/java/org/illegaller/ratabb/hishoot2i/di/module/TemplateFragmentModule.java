package org.illegaller.ratabb.hishoot2i.di.module;

import org.illegaller.ratabb.hishoot2i.presenter.TemplateFragmentPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class TemplateFragmentModule {
    @Provides TemplateFragmentPresenter providePresenter() {
        return new TemplateFragmentPresenter();
    }

}
