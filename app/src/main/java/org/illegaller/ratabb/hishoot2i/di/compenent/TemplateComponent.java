package org.illegaller.ratabb.hishoot2i.di.compenent;

import dagger.Subcomponent;
import org.illegaller.ratabb.hishoot2i.di.module.MainActivityModule;
import org.illegaller.ratabb.hishoot2i.di.module.TemplateFragmentModule;
import org.illegaller.ratabb.hishoot2i.di.module.TemplateModule;

@Subcomponent(modules = TemplateModule.class) public interface TemplateComponent {
  MainActivityComponent plus(MainActivityModule module);

  TemplateFragmentComponent plus(TemplateFragmentModule module);
}
