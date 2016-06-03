package org.illegaller.ratabb.hishoot2i.view;

import android.support.v4.app.Fragment;
import org.illegaller.ratabb.hishoot2i.view.common.Mvp;

public interface LauncherActivityView extends Mvp.View {
  void setFragment(Fragment fragment);
}
