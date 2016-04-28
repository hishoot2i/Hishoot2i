package org.illegaller.ratabb.hishoot2i.di.module;

import android.app.Application;
import dagger.Module;
import dagger.Provides;
import org.illegaller.ratabb.hishoot2i.di.TemplateManager;
import org.illegaller.ratabb.hishoot2i.di.TemplateProvider;
import org.illegaller.ratabb.hishoot2i.utils.SimpleSchedulers;

@Module public class TemplateModule {
  @Provides TemplateProvider provideTemplateProvider(Application app) {
    return new TemplateProvider(app);
  }

  @Provides TemplateManager provideTemplateManager(TemplateProvider provider,
      SimpleSchedulers schedulers) {
    return new TemplateManager(provider, schedulers);
  }
}
