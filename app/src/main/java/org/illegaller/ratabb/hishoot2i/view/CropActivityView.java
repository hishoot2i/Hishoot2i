package org.illegaller.ratabb.hishoot2i.view;

import android.content.Intent;
import android.graphics.Point;
import android.view.View;
import org.illegaller.ratabb.hishoot2i.view.common.Mvp;
import org.illegaller.ratabb.hishoot2i.view.widget.CropImageView;

public interface CropActivityView extends Mvp.View {
  void setResult(int resultCode, Intent data);

  void setResult(int resultCode);

  void finish();

  void showProgress(boolean isShow);

  String getPathImage();

  Point getPointRatio();

  CropImageView getCropImageView();

  View getViewBtnOk();

  View getViewBtnCancel();
}
