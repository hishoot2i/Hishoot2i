package org.illegaller.ratabb.hishoot2i.di.compenent;

import dagger.Subcomponent;
import org.illegaller.ratabb.hishoot2i.di.module.MainActivityModule;
import org.illegaller.ratabb.hishoot2i.di.scope.ActivityScope;
import org.illegaller.ratabb.hishoot2i.view.MainActivity;

@ActivityScope @Subcomponent(modules = MainActivityModule.class)
public interface MainActivityComponent {
  MainActivity inject(MainActivity activity);
}
