package org.illegaller.ratabb.hishoot2i.di.compenent;

import dagger.Subcomponent;
import org.illegaller.ratabb.hishoot2i.di.module.LauncherActivityModule;
import org.illegaller.ratabb.hishoot2i.di.scope.ActivityScope;
import org.illegaller.ratabb.hishoot2i.view.LauncherActivity;

@ActivityScope @Subcomponent(modules = LauncherActivityModule.class)
public interface LauncherActivityComponent {
  LauncherActivity inject(LauncherActivity activity);
}
