package org.illegaller.ratabb.hishoot2i.view;

import android.support.v4.app.Fragment;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.roughike.bottombar.BottomBar;
import org.illegaller.ratabb.hishoot2i.view.common.Mvp;

public interface LauncherActivityView extends Mvp.View {
  void setFragment(Fragment fragment);

  void setBottomBar(BottomBar bottomBar);

  MaterialSearchView getSearchView();
}
