package org.illegaller.ratabb.hishoot2i.di.module;

import dagger.Module;
import dagger.Provides;
import org.illegaller.ratabb.hishoot2i.di.TemplateManager;
import org.illegaller.ratabb.hishoot2i.presenter.TemplateFragmentPresenter;

@Module public class TemplateFragmentModule {

  @Provides TemplateFragmentPresenter providePresenter(TemplateManager templateManager) {
    return new TemplateFragmentPresenter(templateManager);
  }
}
