package org.illegaller.ratabb.hishoot2i.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.io.IOException;
import javax.inject.Inject;
import org.illegaller.ratabb.hishoot2i.HishootApplication;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.model.DataImagePath;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.model.tray.TrayManager;

public class HishootProcess {
  private final Context mContext;
  private final boolean mDoubleSSEnable;
  private final boolean mBackgroundColorEnable;
  private final boolean mBackgroundImageBlurEnable;
  private final boolean mBadgeEnable;
  private final boolean mGlareEnable;
  private final boolean mShadowEnable;
  private final boolean mFrameEnable;
  private final int mBackgroundImageBlurRadius;
  private final String mBadgeText;
  private final int mBadgeSize;
  @ColorInt private final int mBackgroundColorInt;
  @ColorInt private final int mBadgeColor;

  @Inject TrayManager mTrayManager;

  public HishootProcess(Context context) {
    HishootApplication.get(context).getAppComponent().inject(this);
    this.mContext = context;
    this.mDoubleSSEnable = mTrayManager.getDoubleEnable().isValue();
    this.mBackgroundColorEnable = mTrayManager.getBackgroundColorEnable().isValue();
    this.mBackgroundImageBlurEnable = mTrayManager.getBackgroundImageBlurEnable().isValue();
    this.mBadgeEnable = mTrayManager.getBadgeEnable().isValue();
    this.mGlareEnable = mTrayManager.getGlareEnable().isValue();
    this.mShadowEnable = mTrayManager.getShadowEnable().isValue();
    this.mFrameEnable = mTrayManager.getFrameEnable().isValue();
    this.mBackgroundColorInt = mTrayManager.getBackgroundColorInt().getValue();
    this.mBackgroundImageBlurRadius = mTrayManager.getBackgroundImageBlurRadius().getValue();
    this.mBadgeColor = mTrayManager.getBadgeColor().getValue();
    this.mBadgeText = mTrayManager.getBadgeText().getValue();
    this.mBadgeSize = mTrayManager.getBadgeSize().getValue();
  }

  public void process(@NonNull final DataImagePath dataImagePath, @NonNull final Template template,
      @NonNull final Callback callback, boolean isSave) throws Exception {
    if (Utils.isMainThread()) throw new ProcessException("avoid #process on main thread");

    final String pathImageBg = dataImagePath.pathImageBackground;
    final String pathImageSS1 = dataImagePath.pathImageScreen1;
    final String pathImageSS2 = dataImagePath.pathImageScreen2;

    callback.startProcess(System.currentTimeMillis());
    int totalW = template.templatePoint.x;
    int totalH = template.templatePoint.y;
    if (mDoubleSSEnable) totalW += totalW;
    Bitmap result;
    Canvas canvas;
    try { // FIXME ?
      result = Bitmap.createBitmap(totalW, totalH, Bitmap.Config.ARGB_8888);
      canvas = new Canvas(result);
    } catch (Exception e) {
      failed("Oops error on create " + totalW + "x" + totalH, R.string.send_report, e, callback);
      return;
    }

    /////// BACKGROUND ///////
    try {
      if (mBackgroundColorEnable) /*background color*/ {
        canvas.drawColor(mBackgroundColorInt);
      } else { /*background image*/
        Bitmap background =
            pathImageBg != null ? UILHelper.loadImage(pathImageBg, new Point(totalW, totalH))
                : BitmapUtils.alphaPatternBitmap(mContext, totalW, totalH);
        Bitmap scaleCenterCrop = null;
        if (background != null) {
          scaleCenterCrop = BitmapUtils.scaleCenterCrop(background, totalW, totalH);
        }
        if (scaleCenterCrop != null) {
          if (mBackgroundImageBlurEnable)/*background image blur*/ {
            BitmapUtils.drawBlurToCanvas(canvas, scaleCenterCrop, mBackgroundImageBlurRadius);
          } else {
            BitmapUtils.drawBitmapToCanvas(canvas, scaleCenterCrop, 0, 0);
          }
        }
      }
    } catch (Exception e) {
      failed("Oops error draw background " + totalW + "x" + totalH, R.string.send_report, e,
          callback);
      return;
    }

    /////// TEMPLATE and SCREEN///////
    Bitmap mix1, mix2 = null;
    try {
      mix1 = BitmapUtils.mixTemplate(mContext, template, pathImageSS1, mGlareEnable, mShadowEnable,
          mFrameEnable);
      if (mDoubleSSEnable) {
        mix2 =
            BitmapUtils.mixTemplate(mContext, template, pathImageSS2, mGlareEnable, mShadowEnable,
                mFrameEnable);
      }
    } catch (Exception e) {
      String msg = template.name + " (" + totalW + "x" + totalH + ")";
      failed("Template: " + msg + "\n...", R.string.send_report, e, callback);
      return;
    }
    if (mix1 != null) BitmapUtils.drawBitmapToCanvas(canvas, mix1, 0, 0);
    if (mix2 != null) BitmapUtils.drawBitmapToCanvas(canvas, mix2, (totalW / 2), 0);

    /////// BADGE ///////
    if (mBadgeEnable) {
      final Bitmap badge = BitmapUtils.bitmapBadge(mContext, mBadgeText, mBadgeColor, mBadgeSize);
      final int topBadge = totalH - (badge.getHeight() * 2);
      final int leftBadge = (totalW / 2) - (badge.getWidth() / 2);
      BitmapUtils.drawBitmapToCanvas(canvas, badge, leftBadge, topBadge);
    }

    //////// RESULT ////////////
    try {
      callback.doneProcess(result, isSave ? Uri.fromFile(Utils.saveHishoot(result)) : null);
    } catch (IOException e) {
      failed(R.string.cant_save, R.string.save_failed, e, callback);
    }
  }

  private void failed(int message, int extra, Throwable e, Callback callback) {
    failed(mContext.getString(message), extra, e, callback);
  }

  private void failed(String message, int extra, Throwable e, Callback callback) {
    failed(message, mContext.getString(extra), e, callback);
  }

  private void failed(String message, String extra, Throwable e, Callback callback) {
    if (e != null) {
      CrashLog.logError("HishootProcess: " + message, e);
    } else {
      CrashLog.log(message);
    }
    callback.failProcess(message, extra);
  }

  public interface Callback {

    void startProcess(long startTime);

    void failProcess(String message, String extra);

    void doneProcess(Bitmap result, @Nullable Uri uri);
  }

  private static class ProcessException extends IllegalStateException {
    ProcessException(String cause) {
      super("HishootProcess: " + cause);
    }
  }
}
