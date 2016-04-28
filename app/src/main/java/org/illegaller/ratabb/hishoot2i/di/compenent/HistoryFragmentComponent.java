package org.illegaller.ratabb.hishoot2i.di.compenent;

import dagger.Subcomponent;
import org.illegaller.ratabb.hishoot2i.di.module.HistoryFragmentModule;
import org.illegaller.ratabb.hishoot2i.view.fragment.HistoryFragment;

@Subcomponent(modules = HistoryFragmentModule.class) public interface HistoryFragmentComponent {
  HistoryFragment inject(HistoryFragment fragment);
}
