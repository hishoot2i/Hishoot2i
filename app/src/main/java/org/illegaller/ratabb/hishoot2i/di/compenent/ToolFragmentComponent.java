package org.illegaller.ratabb.hishoot2i.di.compenent;

import dagger.Subcomponent;
import org.illegaller.ratabb.hishoot2i.view.fragment.tools.BackgroundToolFragment;
import org.illegaller.ratabb.hishoot2i.view.fragment.tools.BadgeToolFragment;
import org.illegaller.ratabb.hishoot2i.view.fragment.tools.ScreenToolFragment;

@Subcomponent public interface ToolFragmentComponent {
  BackgroundToolFragment inject(BackgroundToolFragment fragment);

  BadgeToolFragment inject(BadgeToolFragment fragment);

  ScreenToolFragment inject(ScreenToolFragment fragment);
}
