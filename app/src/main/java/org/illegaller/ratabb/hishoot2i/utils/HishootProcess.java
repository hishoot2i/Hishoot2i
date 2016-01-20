package org.illegaller.ratabb.hishoot2i.utils;

import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.model.DataImagePath;
import org.illegaller.ratabb.hishoot2i.model.Sizes;
import org.illegaller.ratabb.hishoot2i.model.template.Template;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;

public class HishootProcess {
    private final Context context;
    private final Callback callback;
    private final Template template;
    private final boolean doubleSSEnable;
    private final boolean backgroundColorEnable;
    private final boolean backgroundImageBlurEnable;
    private final boolean badgeEnable;
    private final int backgroundImageBlurRadius;
    @ColorInt private final int backgroundColorInt;
    @ColorInt private final int badgeColor;
    private final String badgeText;
    private final int badgeSize;

    public HishootProcess(Context context, Callback callback, Template template, boolean doubleSSEnable,
                          boolean backgroundColorEnable, boolean backgroundImageBlurEnable,
                          boolean badgeEnable, int backgroundColorInt, int backgroundImageBlurRadius,
                          int badgeColor, String badgeText, int badgeSize) {
        this.context = context;
        this.callback = callback;
        this.template = template;
        this.doubleSSEnable = doubleSSEnable;
        this.backgroundColorEnable = backgroundColorEnable;
        this.backgroundImageBlurEnable = backgroundImageBlurEnable;
        this.badgeEnable = badgeEnable;
        this.backgroundColorInt = backgroundColorInt;
        this.backgroundImageBlurRadius = backgroundImageBlurRadius;
        this.badgeColor = badgeColor;
        this.badgeText = badgeText;
        this.badgeSize = badgeSize;
        HLog.setTAG(this);
    }

    public void process(@NonNull final DataImagePath dataImagePath) {
        final String pathImageBg = dataImagePath.pathImageBackground;
        final String pathImageSS1 = dataImagePath.pathImageScreen1;
        final String pathImageSS2 = dataImagePath.pathImageScreen2;

        boolean isValid = pathImageSS1 != null;
        if (doubleSSEnable) isValid &= pathImageSS2 != null;
        if (!backgroundColorEnable) isValid &= pathImageBg != null;

        if (!isValid) {
            failed(null, R.string.config_not_valid, R.string.send_report);
            return;
        }

        long startMs = System.currentTimeMillis();
        callback.startingImage(startMs);
        UILHelper.tryClearMemoryCache();
        int totalW = template.templateSizes.width;
        int totalH = template.templateSizes.height;

        if (doubleSSEnable) totalW += totalW;

        Bitmap result;
        Canvas canvas;
        try {
            result = Bitmap.createBitmap(totalW, totalH, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(result);
        } catch (Exception e) {
            failed(e, "Oops error on create " + totalW + "x" + totalH, R.string.send_report);
            return;
        }

        //background//
        try {
            if (backgroundColorEnable) {// background color
                canvas.drawColor(backgroundColorInt);
            } else { // background image
                Bitmap background = UILHelper.loadImage(pathImageBg, Sizes.create(totalW, totalH));
                Bitmap scaleCenterCrop = null;
                if (background != null)
                    scaleCenterCrop = BitmapUtils.scaleCenterCrop(background, totalW, totalH);
                if (scaleCenterCrop != null) {
                    if (backgroundImageBlurEnable)// background blur
                        BitmapUtils.drawBlurToCanvas(canvas, scaleCenterCrop, backgroundImageBlurRadius);
                    else BitmapUtils.drawBitmapToCanvas(canvas, scaleCenterCrop, 0, 0);
                }
            }
        } catch (Exception e) {
            failed(e, "Oops error on load background " + totalW + "x" + totalH, R.string.send_report);
            return;
        }

        //template+ss//
        try {
            Bitmap mix1 = BitmapUtils.mixTemplate(context, template, pathImageSS1);
            if (mix1 != null) BitmapUtils.drawBitmapToCanvas(canvas, mix1, 0, 0);
            if (doubleSSEnable) {
                Bitmap mix2 = BitmapUtils.mixTemplate(context, template, pathImageSS2);
                if (mix2 != null) BitmapUtils.drawBitmapToCanvas(canvas, mix2, (totalW / 2), 0);
            }
        } catch (OutOfMemoryError e) {
            String msg = template.name + " (" + totalW + "x" + totalH + ")";
            failed(e, "Template: " + msg + "\n...", R.string.send_report);
            return;
        }

        //badge//
        if (badgeEnable) {
            final Bitmap badge = BitmapUtils.bitmapBadge(context, badgeText, badgeColor, badgeSize);
            final int topBadge = totalH - (badge.getHeight() * 2);
            final int leftBadge = (totalW / 2) - (badge.getWidth() / 2);
            BitmapUtils.drawBitmapToCanvas(canvas, badge, leftBadge, topBadge);
        }
        try {
            final File file = Utils.saveHishoot(result);
            callback.doneImage(Uri.fromFile(file), result);
        } catch (IOException e) {
            failed(e, "Oops can't save Hishoot", R.string.save_failed);
        }
        UILHelper.tryClearMemoryCache();
    }

    private void failed(final Throwable e, final int message, final int extra) {
        failed(e, context.getString(message), extra);
    }

    private void failed(final Throwable e, final String message, final int extra) {
        failed(e, message, context.getString(extra));
    }

    private void failed(final Throwable e, final String message, final String extra) {
        if (e != null) HLog.e(message, e);
        callback.failedImage(message, extra);
    }

    public interface Callback {

        void startingImage(long startTime);

        void failedImage(String text, String extra);

        void doneImage(Uri imageUri, Bitmap result);
    }
}
