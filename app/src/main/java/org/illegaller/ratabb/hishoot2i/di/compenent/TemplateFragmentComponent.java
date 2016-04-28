package org.illegaller.ratabb.hishoot2i.di.compenent;

import dagger.Subcomponent;
import org.illegaller.ratabb.hishoot2i.di.module.TemplateFragmentModule;
import org.illegaller.ratabb.hishoot2i.view.fragment.TemplateFragment;

@Subcomponent(modules = TemplateFragmentModule.class) public interface TemplateFragmentComponent {
  TemplateFragment inject(TemplateFragment fragment);
}
