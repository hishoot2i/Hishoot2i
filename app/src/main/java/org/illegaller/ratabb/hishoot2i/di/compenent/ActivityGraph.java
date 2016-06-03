package org.illegaller.ratabb.hishoot2i.di.compenent;

import org.illegaller.ratabb.hishoot2i.view.AboutActivity;
import org.illegaller.ratabb.hishoot2i.view.CropActivity;
import org.illegaller.ratabb.hishoot2i.view.LauncherActivity;
import org.illegaller.ratabb.hishoot2i.view.MainActivity;
import org.illegaller.ratabb.hishoot2i.view.fragment.AboutFragment;
import org.illegaller.ratabb.hishoot2i.view.fragment.historyview.HistoryFragment;
import org.illegaller.ratabb.hishoot2i.view.fragment.historyview.HistoryRecyclerView;
import org.illegaller.ratabb.hishoot2i.view.fragment.templateview.TemplateFragment;
import org.illegaller.ratabb.hishoot2i.view.fragment.templateview.TemplateRecyclerView;
import org.illegaller.ratabb.hishoot2i.view.fragment.tools.BackgroundToolFragment;
import org.illegaller.ratabb.hishoot2i.view.fragment.tools.BadgeToolFragment;
import org.illegaller.ratabb.hishoot2i.view.fragment.tools.ScreenToolFragment;

public interface ActivityGraph {

  /* activity graph */
  AboutActivity inject(AboutActivity activity);

  CropActivity inject(CropActivity activity);

  LauncherActivity inject(LauncherActivity activity);

  MainActivity inject(MainActivity activity);

  AboutFragment inject(AboutFragment fragment);

  TemplateFragment inject(TemplateFragment fragment);

  HistoryFragment inject(HistoryFragment fragment);

  BackgroundToolFragment inject(BackgroundToolFragment fragment);

  BadgeToolFragment inject(BadgeToolFragment fragment);

  ScreenToolFragment inject(ScreenToolFragment fragment);

  TemplateRecyclerView inject(TemplateRecyclerView view);

  HistoryRecyclerView inject(HistoryRecyclerView view);
}
