package org.illegaller.ratabb.hishoot2i.view;

import android.support.annotation.Nullable;
import org.illegaller.ratabb.hishoot2i.model.ImageReceive;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.view.common.IVew;

public interface MainActivityView extends IVew {
  void showFab(boolean isShow);

  void showProgress(boolean isShow);

  void setImageReceiveTemplate(@Nullable ImageReceive imageReceive, Template template);

  void perform();

  void closePipette();

  void pipetteResult(int color);

  void showActionBar(boolean isShow);
}
