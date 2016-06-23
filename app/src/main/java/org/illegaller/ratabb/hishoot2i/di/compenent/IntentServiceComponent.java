package org.illegaller.ratabb.hishoot2i.di.compenent;

import android.app.IntentService;
import dagger.Subcomponent;
import org.illegaller.ratabb.hishoot2i.HishootApplication;
import org.illegaller.ratabb.hishoot2i.HishootService;
import org.illegaller.ratabb.hishoot2i.di.module.IntentServiceModule;
import org.illegaller.ratabb.hishoot2i.di.scope.IntentServiceScope;

@IntentServiceScope @Subcomponent(modules = IntentServiceModule.class)
public interface IntentServiceComponent {
  HishootService inject(HishootService hishootService);

  final class Initializer {
    private Initializer() {
      throw new AssertionError("no instance");
    }

    public static IntentServiceComponent init(IntentService service) {
      return HishootApplication.get(service)
          .getAppComponent()
          .plus(new IntentServiceModule(service));
    }
  }
}
