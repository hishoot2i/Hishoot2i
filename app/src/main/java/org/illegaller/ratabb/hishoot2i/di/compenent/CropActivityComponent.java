package org.illegaller.ratabb.hishoot2i.di.compenent;

import dagger.Subcomponent;
import org.illegaller.ratabb.hishoot2i.di.module.CropActivityModule;
import org.illegaller.ratabb.hishoot2i.di.scope.ActivityScope;
import org.illegaller.ratabb.hishoot2i.view.CropActivity;

@ActivityScope @Subcomponent(modules = CropActivityModule.class)
public interface CropActivityComponent {
  CropActivity inject(CropActivity activity);
}
