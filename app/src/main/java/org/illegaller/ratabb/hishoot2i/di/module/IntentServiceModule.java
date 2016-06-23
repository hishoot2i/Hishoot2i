package org.illegaller.ratabb.hishoot2i.di.module;

import android.app.IntentService;
import android.content.Context;
import dagger.Module;
import dagger.Provides;
import org.illegaller.ratabb.hishoot2i.di.scope.IntentServiceScope;

@Module public class IntentServiceModule {
  private IntentService mIntentService;

  public IntentServiceModule(IntentService mIntentService) {
    this.mIntentService = mIntentService;
  }

  @Provides IntentService provideIntentService() {
    return mIntentService;
  }

  @IntentServiceScope @Provides Context provideContext() {
    return mIntentService;
  }
}
