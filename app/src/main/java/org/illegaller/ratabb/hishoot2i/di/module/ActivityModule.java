package org.illegaller.ratabb.hishoot2i.di.module;

import android.app.Activity;
import android.content.Context;
import dagger.Module;
import dagger.Provides;
import org.illegaller.ratabb.hishoot2i.di.scope.ActivityScope;

@Module public class ActivityModule {

  private Activity mActivity;

  public ActivityModule(Activity activity) {
    mActivity = activity;
  }

  @Provides Activity provideActivity() {
    return mActivity;
  }

  @Provides @ActivityScope Context providesContext() {
    return mActivity;
  }
}