package org.illegaller.ratabb.hishoot2i.utils;

import com.enrique.stackblur.StackBlurManager;

import org.illegaller.ratabb.hishoot2i.AppConstants;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.model.template.TemplateType;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.NinePatchDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.DisplayMetrics;

public class BitmapUtils {

    protected BitmapUtils() {
        throw new AssertionError("BitmapUtils no construction");
    }

    ////////////////// HishootProcess //////////////////
    public static Bitmap mixTemplate(@NonNull final Context context, @NonNull final Template template,
                                     @NonNull final String pathSS) {
        final int templateW = template.templateSizes.width;
        final int templateH = template.templateSizes.height;
        final int screenW = template.screenSizes.width;
        final int screenH = template.screenSizes.height;
        final int offsetW = template.offset.width;
        final int offsetH = template.offset.height;

        Matrix matrix = new Matrix();

        Bitmap result = Bitmap.createBitmap(templateW, templateH, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        if (template.type == TemplateType.APK_V1) {
            //ss
            matrix.postTranslate(offsetW, offsetH);
            Bitmap ss = UILHelper.loadImage(pathSS);
            BitmapUtils.drawBitmapToCanvas(canvas, matchSizes(ss, screenW, screenH), matrix);
            //frame
            matrix.postTranslate(0 - offsetW, 0 - offsetH);
            Bitmap frame;
            if (template.id.equals(AppConstants.DEFAULT_TEMPLATE_ID))
                frame = BitmapUtils.getNinePatch(context, R.drawable.frame1, templateW, templateH);
            else
                frame = UILHelper.loadImage(template.frameFile);

            BitmapUtils.drawBitmapToCanvas(canvas, matchSizes(frame, templateW, templateH), matrix);
        } else if (template.type == TemplateType.HTZ) {
            if (template.frameFile != null) {
                Bitmap frame = UILHelper.loadImage(template.frameFile);
                BitmapUtils.drawBitmapToCanvas(canvas, matchSizes(frame, templateW, templateH), matrix);
            }
            //ss
            matrix.postTranslate(offsetW, offsetH);
            Bitmap ss = UILHelper.loadImage(pathSS);
            BitmapUtils.drawBitmapToCanvas(canvas, matchSizes(ss, screenW, screenH), matrix);
            matrix.postTranslate(0 - offsetW, 0 - offsetH);
            if (template.glareFile != null) {
                Bitmap glare = UILHelper.loadImage(template.glareFile);
                if (template.overlayOffset != null)
                    matrix.postTranslate(template.overlayOffset.width, template.overlayOffset.height);
                BitmapUtils.drawBitmapToCanvas(canvas, glare, matrix);
            }
        } else {
            if (template.shadowFile != null) {
                Bitmap shadow = UILHelper.loadImage(template.shadowFile);
                BitmapUtils.drawBitmapToCanvas(canvas, matchSizes(shadow, templateW, templateH), matrix);
            }
            if (template.frameFile != null) {
                Bitmap frame = UILHelper.loadImage(template.frameFile);
                ;

                BitmapUtils.drawBitmapToCanvas(canvas, matchSizes(frame, templateW, templateH), matrix);
            }
            //ss
            matrix.postTranslate(offsetW, offsetH);
            Bitmap ss = UILHelper.loadImage(pathSS);

            BitmapUtils.drawBitmapToCanvas(canvas, matchSizes(ss, screenW, screenH), matrix);

            matrix.postTranslate(0 - offsetW, 0 - offsetH);
            if (template.glareFile != null) {
                Bitmap glare = UILHelper.loadImage(template.glareFile);

                BitmapUtils.drawBitmapToCanvas(canvas, matchSizes(glare, templateW, templateH), matrix);
            }
        }
        return result;
    }


    public static Bitmap matchSizes(final Bitmap bitmap, int targetW, int targetH) {
        if (bitmap.getWidth() == targetW && bitmap.getHeight() == targetH) return bitmap;
        else return Bitmap.createScaledBitmap(bitmap, targetW, targetH, true);
    }

    public static void drawBitmapToCanvas(Canvas canvas, Bitmap bitmap, int left, int top) {
        canvas.drawBitmap(bitmap, left, top, null);
        bitmap.recycle();
    }

    public static void drawBitmapToCanvas(Canvas canvas, Bitmap bitmap, Matrix matrix) {
        canvas.drawBitmap(bitmap, matrix, null);
        bitmap.recycle();
    }

    public static void drawBitmapBlurToCanvas(Canvas canvas, Bitmap bitmap, int radius) {
        int rad = (radius > 100) ? 100 : radius;
        int widthSrc = bitmap.getWidth();
        int heightSrc = bitmap.getHeight();
        float ratio = .5f;
        int scaledW = (int) ((float) widthSrc * ratio);
        int scaledH = (int) ((float) heightSrc * ratio);
        Bitmap scaledDown = Bitmap.createScaledBitmap(bitmap, scaledW, scaledH, true);
        bitmap.recycle();
        StackBlurManager stackBlurManager = new StackBlurManager(scaledDown);
        Bitmap blur = stackBlurManager.process(rad);
        scaledDown.recycle();
        Bitmap scaledUp = Bitmap.createScaledBitmap(blur, widthSrc, heightSrc, true);
        blur.recycle();
        BitmapUtils.drawBitmapToCanvas(canvas, scaledUp, 0, 0);
    }

    /** http://stackoverflow.com/a/8113368 */
    public static Bitmap scaleCenterCrop(Bitmap source, int newWidth, int newHeight) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        // Compute the scaling factors to fit the new height and width, respectively.
        // To cover the final image, the final scaling will be the bigger of these two.
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        // Now getInstance the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        // Let's find out the upper left coordinates if the scaled bitmap
        // should be centered in the new size give by the parameters
        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        // The target rectangle for the new, scaled version of the source bitmap will now be
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        // Finally, we create a new bitmap of the specified size and draw our new,
        // scaled bitmap onto it.
        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);
        source.recycle();
        return dest;
    }

    ////////////////// Badge //////////////////
    // TODO: TypeFace
    public static Bitmap bitmapBadge(final Context context, final String badgeText,
                                     @ColorInt int color) {
        final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        final float density = displayMetrics.density;
        final float scaleDensity = displayMetrics.scaledDensity;
        final TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
        textPaint.setTypeface(FontUtils.getBadgeTypeface());
        textPaint.setTextSize(24 * scaleDensity);
        final float padding = 10 * density;
        final float cornerRadius = 8 * density;
        final Rect textBounds = new Rect();
        final String text = badgeText.toUpperCase();
        textPaint.getTextBounds(text, 0, text.length(), textBounds);
        int width = (int) (padding + textBounds.width() + padding);
        int height = (int) (padding + textBounds.height() + padding);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        if (Utils.isHoneycombMR1()) bitmap.setHasAlpha(true);
        final Canvas canvas = new Canvas(bitmap);
        final Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(Utils.setHalfAlphaColor(color));

        final RectF rectF = new RectF(0, 0, width, height);
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, backgroundPaint);

        textPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawText(text, padding, height - padding, textPaint);
        return bitmap;
    }

    ////////////////// Template Default //////////////////
    public static Bitmap getNinePatch(Context context, @DrawableRes int drawableID, int width, int height) {
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        NinePatchDrawable ninePatchDrawable = (NinePatchDrawable) ContextCompat.getDrawable(context, drawableID);
        if (ninePatchDrawable != null) {
            ninePatchDrawable.setBounds(0, 0, width, height);
            ninePatchDrawable.draw(canvas);
        }
        return result;
    }

    ////////////////// HishootService //////////////////

    /**
     * {@link Bitmap} for {@link android.support.v4.app.NotificationCompat.BigPictureStyle#bigPicture(Bitmap)}
     */
    public static Bitmap previewBigPicture(final Bitmap source) {

        final Bitmap screenshot = BitmapUtils.scaleCenterCrop(source, 256, 256);
        int imageWidth = screenshot.getWidth();
        int imageHeight = screenshot.getHeight();
        final int shortSide = imageWidth < imageHeight ? imageWidth : imageHeight;
        Bitmap preview = Bitmap.createBitmap(shortSide, shortSide, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(preview);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0.25f);
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        Matrix matrix = new Matrix();
        matrix.postTranslate((shortSide - imageWidth) / 2, (shortSide - imageHeight) / 2);
        canvas.drawBitmap(screenshot, matrix, paint);
        canvas.drawColor(0x40FFFFFF);
        screenshot.recycle();
        return preview;
    }

    public static Bitmap roundedLargeIcon(final Context context, final Bitmap bitmap) {
        final int iconSize = Utils.getDimensionPixelSize(context, android.R.dimen.app_icon_size);
        final Bitmap scale = Bitmap.createScaledBitmap(bitmap, iconSize, iconSize, true);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, iconSize, iconSize);
        paint.setAntiAlias(true);
        Bitmap rounded = Bitmap.createBitmap(iconSize, iconSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(rounded);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(iconSize / 2, iconSize / 2, iconSize / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(scale, rect, rect, paint);
        scale.recycle();
        return rounded;
    }
}
