package org.illegaller.ratabb.hishoot2i.view;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import org.illegaller.ratabb.hishoot2i.view.common.Mvp;

public interface CropActivityView extends Mvp.View {

  void onResult(@Nullable Uri uri);

  void showProgress(boolean isShow);

  void setCropImageView(Bitmap bitmap);
}
