package org.illegaller.ratabb.hishoot2i.di.compenent;

import org.illegaller.ratabb.hishoot2i.di.module.MainFragmentModule;
import org.illegaller.ratabb.hishoot2i.presenter.MainFragmentPresenter;
import org.illegaller.ratabb.hishoot2i.view.fragment.MainFragment;

import dagger.Component;
import dagger.Subcomponent;

@Component(modules = MainFragmentModule.class)
public interface MainFragmentComponent {
    void inject(MainFragment fragment);
}
