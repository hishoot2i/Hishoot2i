package org.illegaller.ratabb.hishoot2i.di.compenent;

import dagger.Component;
import javax.inject.Singleton;
import org.illegaller.ratabb.hishoot2i.HishootApplication;
import org.illegaller.ratabb.hishoot2i.HishootService;
import org.illegaller.ratabb.hishoot2i.di.module.AboutActivityModule;
import org.illegaller.ratabb.hishoot2i.di.module.ApplicationModule;
import org.illegaller.ratabb.hishoot2i.di.module.CropActivityModule;
import org.illegaller.ratabb.hishoot2i.di.module.HistoryFragmentModule;
import org.illegaller.ratabb.hishoot2i.di.module.LauncherActivityModule;
import org.illegaller.ratabb.hishoot2i.di.module.SystemServiceModule;
import org.illegaller.ratabb.hishoot2i.di.module.TemplateModule;
import org.illegaller.ratabb.hishoot2i.di.module.TrayModule;
import org.illegaller.ratabb.hishoot2i.model.template.builder.DefaultBuilder;
import org.illegaller.ratabb.hishoot2i.utils.HishootProcess;
import org.illegaller.ratabb.hishoot2i.view.fragment.AboutFragment;

@Singleton
@Component(modules = { ApplicationModule.class, SystemServiceModule.class, TrayModule.class })
public interface ApplicationComponent {
  HishootApplication inject(HishootApplication application);

  HishootService inject(HishootService service);

  DefaultBuilder inject(DefaultBuilder builder);

  HishootProcess inject(HishootProcess process);

  AboutFragment inject(AboutFragment fragment);

  TemplateComponent plus(TemplateModule module);

  AboutActivityComponent plus(AboutActivityModule module);

  CropActivityComponent plus(CropActivityModule module);

  LauncherActivityComponent plus(LauncherActivityModule module);

  HistoryFragmentComponent plus(HistoryFragmentModule module);

  ToolFragmentComponent plus();
}
