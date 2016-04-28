package org.illegaller.ratabb.hishoot2i.di.compenent;

import dagger.Subcomponent;
import org.illegaller.ratabb.hishoot2i.di.module.AboutActivityModule;
import org.illegaller.ratabb.hishoot2i.di.scope.ActivityScope;
import org.illegaller.ratabb.hishoot2i.view.AboutActivity;

@ActivityScope @Subcomponent(modules = AboutActivityModule.class)
public interface AboutActivityComponent {
  AboutActivity inject(AboutActivity activity);
}
