package org.illegaller.ratabb.hishoot2i.di.compenent;

import org.illegaller.ratabb.hishoot2i.HishootApplication;
import org.illegaller.ratabb.hishoot2i.HishootService;
import org.illegaller.ratabb.hishoot2i.di.module.AboutActivityModule;
import org.illegaller.ratabb.hishoot2i.di.module.CropActivityModule;
import org.illegaller.ratabb.hishoot2i.di.module.HistoryFragmentModule;
import org.illegaller.ratabb.hishoot2i.di.module.LauncherActivityModule;
import org.illegaller.ratabb.hishoot2i.di.module.TemplateModule;
import org.illegaller.ratabb.hishoot2i.model.template.builder.DefaultBuilder;
import org.illegaller.ratabb.hishoot2i.utils.HishootProcess;
import org.illegaller.ratabb.hishoot2i.view.common.BaseFragment;
import org.illegaller.ratabb.hishoot2i.view.fragment.AboutFragment;

public interface ApplicationGraph {
  HishootApplication inject(HishootApplication application);

  HishootService inject(HishootService service);

  DefaultBuilder inject(DefaultBuilder builder);

  HishootProcess inject(HishootProcess process);

  AboutFragment inject(AboutFragment fragment);

  BaseFragment inject(BaseFragment fragment);

  TemplateComponent plus(TemplateModule module);

  AboutActivityComponent plus(AboutActivityModule module);

  CropActivityComponent plus(CropActivityModule module);

  LauncherActivityComponent plus(LauncherActivityModule module);

  HistoryFragmentComponent plus(HistoryFragmentModule module);

  ToolFragmentComponent plus();
}
