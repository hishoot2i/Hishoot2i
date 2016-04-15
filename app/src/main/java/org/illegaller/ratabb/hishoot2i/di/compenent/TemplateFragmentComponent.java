package org.illegaller.ratabb.hishoot2i.di.compenent;

import org.illegaller.ratabb.hishoot2i.di.module.TemplateFragmentModule;
import org.illegaller.ratabb.hishoot2i.view.fragment.TemplateFragment;

import dagger.Component;
import dagger.Subcomponent;

@Component(modules = TemplateFragmentModule.class)
public interface TemplateFragmentComponent {
    void inject(TemplateFragment fragment);
}
