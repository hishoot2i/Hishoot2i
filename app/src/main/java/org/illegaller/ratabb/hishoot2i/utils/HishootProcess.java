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
import javax.inject.Named;
import org.illegaller.ratabb.hishoot2i.HishootApplication;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.model.DataImagePath;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.model.tray.BooleanTray;
import org.illegaller.ratabb.hishoot2i.model.tray.IntTray;
import org.illegaller.ratabb.hishoot2i.model.tray.StringTray;

import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.BADGE_COLOR;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.BADGE_ENABLE;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.BADGE_SIZE;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.BADGE_TEXT;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.BG_COLOR_ENABLE;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.BG_COLOR_INT;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.BG_IMAGE_BLUR_ENABLE;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.BG_IMAGE_BLUR_RADIUS;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.FRAME_ENABLE;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.GLARE_ENABLE;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.SHADOW_ENABLE;
import static org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray.SS_DOUBLE_ENABLE;

public class HishootProcess {
  private final Context context;
  private final boolean doubleSSEnable;
  private final boolean bgColorEnable;
  private final boolean bgImageBlurEnable;
  private final boolean badgeEnable;
  private final boolean glareEnable;
  private final boolean shadowEnable;
  private final boolean frameEnable;
  private final int bgImageBlurRadius;
  private final String badgeText;
  private final int badgeSize;
  @ColorInt private final int bgColorInt;
  @ColorInt private final int badgeColor;
  @Inject @Named(SS_DOUBLE_ENABLE) BooleanTray ssDoubleEnableTray;
  @Inject @Named(BG_COLOR_ENABLE) BooleanTray bgColorEnableTray;
  @Inject @Named(BG_IMAGE_BLUR_ENABLE) BooleanTray bgImageBlurEnableTray;
  @Inject @Named(BADGE_ENABLE) BooleanTray badgeEnableTray;
  @Inject @Named(GLARE_ENABLE) BooleanTray glareEnableTray;
  @Inject @Named(SHADOW_ENABLE) BooleanTray shadowEnableTray;
  @Inject @Named(FRAME_ENABLE) BooleanTray frameEnableTray;
  @Inject @Named(BG_COLOR_INT) IntTray bgColorIntTray;
  @Inject @Named(BG_IMAGE_BLUR_RADIUS) IntTray bgImageBlurRadiusTray;
  @Inject @Named(BADGE_COLOR) IntTray badgeColorTray;
  @Inject @Named(BADGE_SIZE) IntTray badgeSizeTray;
  @Inject @Named(BADGE_TEXT) StringTray badgeTextTray;
  private Callback callback;

  public HishootProcess(Context context) {
    this.context = context;
    HishootApplication.get(context).getApplicationComponent().inject(this);
    this.doubleSSEnable = ssDoubleEnableTray.get();
    this.bgColorEnable = bgColorEnableTray.get();
    this.bgImageBlurEnable = bgImageBlurEnableTray.get();
    this.badgeEnable = badgeEnableTray.get();
    this.glareEnable = glareEnableTray.get();
    this.shadowEnable = shadowEnableTray.get();
    this.frameEnable = frameEnableTray.get();
    this.bgColorInt = bgColorIntTray.get();
    this.bgImageBlurRadius = bgImageBlurRadiusTray.get();
    this.badgeColor = badgeColorTray.get();
    this.badgeText = badgeTextTray.get();
    this.badgeSize = badgeSizeTray.get();
  }

  public void process(@NonNull DataImagePath dataImagePath, @NonNull Template template,
      @NonNull Callback callback, boolean isSave) throws Exception {
    if (Utils.isMainThread()) {
      throw new IllegalThreadStateException("avoid HishootProcess#prosess on main thread");
    }

    final String pathImageBg = dataImagePath.pathImageBackground;
    final String pathImageSS1 = dataImagePath.pathImageScreen1;
    final String pathImageSS2 = dataImagePath.pathImageScreen2;
    this.callback = callback;
    callback.startProcess(System.currentTimeMillis());
    int totalW = template.templatePoint.x;
    int totalH = template.templatePoint.y;
    if (doubleSSEnable) totalW += totalW;
    Bitmap result;
    Canvas canvas;
    try { // FIXME ?
      result = Bitmap.createBitmap(totalW, totalH, Bitmap.Config.ARGB_8888);
      canvas = new Canvas(result);
    } catch (Exception e) {
      failed("Oops error on create " + totalW + "x" + totalH, R.string.send_report, e);
      return;
    }
    ///////background///////
    try {
      if (bgColorEnable) /*background color*/ {
        canvas.drawColor(bgColorInt);
      } else { /*background image*/
        Bitmap background =
            pathImageBg != null ? UILHelper.loadImage(pathImageBg, new Point(totalW, totalH))
                : BitmapUtils.alphaPatternBitmap(context, totalW, totalH);
        Bitmap scaleCenterCrop = null;
        if (background != null) {
          scaleCenterCrop = BitmapUtils.scaleCenterCrop(background, totalW, totalH);
        }
        if (scaleCenterCrop != null) {
          if (bgImageBlurEnable)/*background image blur*/ {
            BitmapUtils.drawBlurToCanvas(canvas, scaleCenterCrop, bgImageBlurRadius);
          } else {
            BitmapUtils.drawBitmapToCanvas(canvas, scaleCenterCrop, 0, 0);
          }
        }
      }
    } catch (Exception e) {
      failed("Oops error draw background " + totalW + "x" + totalH, R.string.send_report, e);
      return;
    }
    ///////template+ss///////
    Bitmap mix1, mix2 = null;
    try {
      mix1 = BitmapUtils.mixTemplate(context, template, pathImageSS1, glareEnable, shadowEnable,
          frameEnable);
      if (doubleSSEnable) {
        mix2 = BitmapUtils.mixTemplate(context, template, pathImageSS2, glareEnable, shadowEnable,
            frameEnable);
      }
    } catch (Exception e) {
      String msg = template.name + " (" + totalW + "x" + totalH + ")";
      failed("Template: " + msg + "\n...", R.string.send_report, e);
      return;
    }
    if (mix1 != null) BitmapUtils.drawBitmapToCanvas(canvas, mix1, 0, 0);
    if (mix2 != null) BitmapUtils.drawBitmapToCanvas(canvas, mix2, (totalW / 2), 0);
    ///////badge///////
    if (badgeEnable) {
      final Bitmap badge = BitmapUtils.bitmapBadge(context, badgeText, badgeColor, badgeSize);
      final int topBadge = totalH - (badge.getHeight() * 2);
      final int leftBadge = (totalW / 2) - (badge.getWidth() / 2);
      BitmapUtils.drawBitmapToCanvas(canvas, badge, leftBadge, topBadge);
    }

    try {
      callback.doneProcess(result, isSave ? Uri.fromFile(Utils.saveHishoot(result)) : null);
    } catch (IOException e) { /* FIXME: reported */
      failed(R.string.cant_save, R.string.save_failed, e);
    }
  }

  void failed(int message, int extra, Throwable e) {
    failed(context.getString(message), extra, e);
  }

  void failed(String message, int extra, Throwable e) {
    failed(message, context.getString(extra), e);
  }

  void failed(String message, String extra, Throwable e) {
    if (e != null) CrashLog.logError("HishootProcess: " + message, e);
    callback.failProcess(message, extra);
  }

  public interface Callback {

    void startProcess(long startTime);

    void failProcess(String message, String extra);

    void doneProcess(Bitmap result, @Nullable Uri uri);
  }
}
