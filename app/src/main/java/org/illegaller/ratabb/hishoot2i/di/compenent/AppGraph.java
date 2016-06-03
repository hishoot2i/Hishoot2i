package org.illegaller.ratabb.hishoot2i.di.compenent;

import com.squareup.leakcanary.RefWatcher;
import org.illegaller.ratabb.hishoot2i.HishootApplication;
import org.illegaller.ratabb.hishoot2i.HishootService;
import org.illegaller.ratabb.hishoot2i.di.module.ActivityModule;
import org.illegaller.ratabb.hishoot2i.model.template.builder.DefaultBuilder;
import org.illegaller.ratabb.hishoot2i.utils.HishootProcess;

public interface AppGraph {
  /* add component */
  ActivityComponent plus(ActivityModule activityModule);

  /* expose */
  RefWatcher refWatcher();

  /* START app graph*/
  HishootApplication inject(HishootApplication application);

  HishootService inject(HishootService service);

  HishootProcess inject(HishootProcess process);

  DefaultBuilder inject(DefaultBuilder defaultBuilder);
  /* END app graph*/
}
