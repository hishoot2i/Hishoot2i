package org.illegaller.ratabb.hishoot2i.view;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import org.illegaller.ratabb.hishoot2i.view.common.IVew;

public interface CropActivityView extends IVew {

  void onResult(@Nullable Uri uri);

  void showProgress(boolean isShow);

  void setCropImageView(Bitmap bitmap);
}
