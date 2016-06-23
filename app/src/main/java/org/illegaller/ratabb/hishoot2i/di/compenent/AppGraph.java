package org.illegaller.ratabb.hishoot2i.di.compenent;

import org.illegaller.ratabb.hishoot2i.HishootApplication;
import org.illegaller.ratabb.hishoot2i.di.module.ActivityModule;
import org.illegaller.ratabb.hishoot2i.di.module.IntentServiceModule;

interface AppGraph {
  ActivityComponent plus(ActivityModule activityModule);

  IntentServiceComponent plus(IntentServiceModule intentServiceModule);

  HishootApplication inject(HishootApplication application);
}