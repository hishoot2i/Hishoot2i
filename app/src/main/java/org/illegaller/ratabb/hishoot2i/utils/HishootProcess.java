package org.illegaller.ratabb.hishoot2i.utils;

import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.model.DataImagePath;
import org.illegaller.ratabb.hishoot2i.model.template.Template;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

import java.io.IOException;

public class HishootProcess {
    private final Context context;
    private final Callback callback;
    private final Template template;
    private final boolean doubleSSEnable;
    private final boolean bgColorEnable;
    private final boolean bgImageBlurEnable;
    private final boolean badgeEnable;
    private final boolean glareEnable;
    private final boolean shadowEnable;
    private final int bgImageBlurRadius;
    private final String badgeText;
    private final int badgeSize;
    @ColorInt private final int bgColorInt;
    @ColorInt private final int badgeColor;

    public HishootProcess(Context context, Template template,
                          boolean doubleSSEnable, boolean bgColorEnable,
                          boolean bgImageBlurEnable, boolean badgeEnable,
                          boolean glareEnable, boolean shadowEnable,
                          int bgColorInt, int bgImageBlurRadius, int badgeColor,
                          String badgeText, int badgeSize, Callback callback) {
        this.context = context;
        this.callback = callback;
        this.template = template;
        this.doubleSSEnable = doubleSSEnable;
        this.bgColorEnable = bgColorEnable;
        this.bgImageBlurEnable = bgImageBlurEnable;
        this.badgeEnable = badgeEnable;
        this.glareEnable = glareEnable;
        this.shadowEnable = shadowEnable;
        this.bgColorInt = bgColorInt;
        this.bgImageBlurRadius = bgImageBlurRadius;
        this.badgeColor = badgeColor;
        this.badgeText = badgeText;
        this.badgeSize = badgeSize;
    }

    public void process(@NonNull final DataImagePath dataImagePath, boolean isService) {
        final String pathImageBg = dataImagePath.pathImageBackground;
        final String pathImageSS1 = dataImagePath.pathImageScreen1;
        final String pathImageSS2 = dataImagePath.pathImageScreen2;
        if (template == null) {
            failed("Oops template not found", R.string.send_report, null);
            return;
        }
        callback.startingImage(System.currentTimeMillis());
        int totalW = template.templatePoint.x;
        int totalH = template.templatePoint.y;
        if (doubleSSEnable) totalW += totalW;
        Bitmap result;
        Canvas canvas;
        try { // FIXME: handle this exception || define max width, height from device
            result = Bitmap.createBitmap(totalW, totalH, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(result);
        } catch (Exception e) {
            failed("Oops error on create " + totalW + "x" + totalH, R.string.send_report, e);
            return;
        }
        ///////background///////
        try {
            if (bgColorEnable) /*background color*/ canvas.drawColor(bgColorInt);
            else { /*background image*/
                Bitmap background = pathImageBg != null ?
                        UILHelper.loadImage(pathImageBg, new Point(totalW, totalH))
                        : BitmapUtils.alphaPatternBitmap(context, totalW, totalH);
                Bitmap scaleCenterCrop = null;
                if (background != null)
                    scaleCenterCrop = BitmapUtils.scaleCenterCrop(background, totalW, totalH);
                if (scaleCenterCrop != null) {
                    if (bgImageBlurEnable)/*background image blur*/
                        BitmapUtils.drawBlurToCanvas(canvas, scaleCenterCrop, bgImageBlurRadius);
                    else BitmapUtils.drawBitmapToCanvas(canvas, scaleCenterCrop, 0, 0);
                }
            }
        } catch (Exception e) {
            failed("Oops error draw background " + totalW + "x" + totalH, R.string.send_report, e);
            return;
        }
        ///////template+ss///////
        Bitmap mix1, mix2 = null;
        try {
            mix1 = BitmapUtils.mixTemplate(context, template, pathImageSS1, glareEnable, shadowEnable);
            if (doubleSSEnable)
                mix2 = BitmapUtils.mixTemplate(context, template, pathImageSS2, glareEnable, shadowEnable);
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
            if (isService)
                callback.doneService(result, Uri.fromFile(Utils.saveHishoot(result)));
            else callback.doneImage(result);
        } catch (IOException e) {
            failed("Oops can't save Hishoot", R.string.save_failed, e);
        }

    }

    private void failed(final int message, final int extra, final Throwable e) {
        failed(context.getString(message), extra, e);
    }

    private void failed(final String message, final int extra, final Throwable e) {
        failed(message, context.getString(extra), e);
    }

    private void failed(final String message, final String extra, final Throwable e) {
        if (e != null) CrashLog.logError("HishootProcess: " + message, e);
        callback.failedImage(message, extra);
    }

    public interface Callback {

        void startingImage(long startTime);

        void failedImage(String text, String extra);

        void doneImage(Bitmap result);

        void doneService(Bitmap result, Uri uri);
    }
}
