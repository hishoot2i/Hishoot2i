package org.illegaller.ratabb.hishoot2i.di.compenent;

import android.app.Activity;
import dagger.Subcomponent;
import org.illegaller.ratabb.hishoot2i.HishootApplication;
import org.illegaller.ratabb.hishoot2i.di.module.ActivityModule;
import org.illegaller.ratabb.hishoot2i.di.scope.ActivityScope;

@ActivityScope @Subcomponent(modules = ActivityModule.class) public interface ActivityComponent
    extends ActivityGraph {
  final class Initializer {
    private Initializer() {
      throw new UnsupportedOperationException("no instance");
    }

    public static ActivityComponent init(Activity activity) {
      return HishootApplication.get(activity)
          .getAppComponent()
          .plus(new ActivityModule(activity));
    }
  }
}

