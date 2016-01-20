package org.illegaller.ratabb.hishoot2i.utils;

import com.enrique.stackblur.StackBlurManager;

import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.model.template.Template;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.NinePatchDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.DisplayMetrics;

import java.util.Locale;

import static org.illegaller.ratabb.hishoot2i.AppConstants.DEFAULT_TEMPLATE_ID;

public class BitmapUtils {

    private BitmapUtils() {
        throw new AssertionError("BitmapUtils no construction");
    }

    @Nullable public static Bitmap mixTemplate(@NonNull final Context context, @NonNull final Template template,
                                               @NonNull final String pathSS) throws OutOfMemoryError {
        final int templateW = template.templateSizes.width;
        final int templateH = template.templateSizes.height;

        Matrix matrix = new Matrix();
        HLog.d("Template w:" + templateW + " h:" + templateH);

        Bitmap result = Bitmap.createBitmap(templateW, templateH, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(result);
        Bitmap ss = UILHelper.loadImage(pathSS);
        ;
        Bitmap frame;
        Bitmap shadow;
        Bitmap glare;
        switch (template.type) {
            case APK_V1:
                if (ss != null) BitmapUtils.drawPerspective(canvas, ss, template);
                if (template.id.equals(DEFAULT_TEMPLATE_ID))
                    frame = BitmapUtils.getNinePatch(context, R.drawable.frame1, templateW, templateH);
                else frame = UILHelper.loadImage(template.frameFile, template.templateSizes);

                if (frame != null) BitmapUtils.drawBitmapToCanvas(canvas,
                        BitmapUtils.matchSizes(frame, templateW, templateH), matrix);
                break;
            case APK_V2:
                if (template.shadowFile != null) {
                    shadow = UILHelper.loadImage(template.shadowFile, template.templateSizes);
                    if (shadow != null) BitmapUtils.drawBitmapToCanvas(canvas,
                            BitmapUtils.matchSizes(shadow, templateW, templateH), matrix);
                }
                if (template.frameFile != null) {
                    frame = UILHelper.loadImage(template.frameFile, template.templateSizes);
                    if (frame != null) BitmapUtils.drawBitmapToCanvas(canvas,
                            BitmapUtils.matchSizes(frame, templateW, templateH), matrix);
                }
                if (ss != null) BitmapUtils.drawPerspective(canvas, ss, template);
                if (template.glareFile != null) {
                    glare = UILHelper.loadImage(template.glareFile, template.templateSizes);
                    if (glare != null) BitmapUtils.drawBitmapToCanvas(canvas,
                            BitmapUtils.matchSizes(glare, templateW, templateH), matrix);
                }
                break;
            case HTZ:
                if (template.frameFile != null) {
                    frame = UILHelper.loadImage(template.frameFile, template.templateSizes);
                    if (frame != null) BitmapUtils.drawBitmapToCanvas(canvas,
                            BitmapUtils.matchSizes(frame, templateW, templateH), matrix);
                }
                if (ss != null) BitmapUtils.drawPerspective(canvas, ss, template);
                if (template.glareFile != null) {
                    glare = UILHelper.loadImage(template.glareFile);
                    if (template.overlayOffset != null)
                        matrix.postTranslate(template.overlayOffset.width, template.overlayOffset.height);
                    if (glare != null) BitmapUtils.drawBitmapToCanvas(canvas, glare, matrix);
                }
                break;
            default:
                throw new RuntimeException("no default switch; unknown template type ");
        }
        return result;
    }

    /*http://github.com/StudentNSK/Image-Perspective-Transformation-Example/*/
    private static void drawPerspective(@NonNull final Canvas canvas, @NonNull final Bitmap bitmap,
                                        @NonNull final Template template) {
        Matrix matrix = new Matrix();
        Paint paint = new Paint();
        final float tWidth = bitmap.getWidth();
        final float tHeight = bitmap.getHeight();
        final float[] src = new float[]{
                0, 0,
                tWidth, 0,
                0, tHeight,
                tWidth, tHeight};

        final float leftTopX = template.leftTop.width;
        final float leftTopY = template.leftTop.height;
        final float rightTopX = template.rightTop.width;
        final float rightTopY = template.rightTop.height;
        final float leftBottomX = template.leftBottom.width;
        final float leftBottomY = template.leftBottom.height;
        final float rightBottomX = template.rightBottom.width;
        final float rightBottomY = template.rightBottom.height;

        final float[] dst = new float[]{
                leftTopX, leftTopY,
                rightTopX, rightTopY,
                leftBottomX, leftBottomY,
                rightBottomX, rightBottomY};

        matrix.setPolyToPoly(src, 0, dst, 0, src.length / 2);
        canvas.save();
        canvas.concat(matrix);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        canvas.restore();
        bitmap.recycle();
    }

    private static Bitmap matchSizes(@NonNull final Bitmap bitmap, int targetW, int targetH) {
        if (bitmap.getWidth() == targetW && bitmap.getHeight() == targetH) return bitmap;
        else return Bitmap.createScaledBitmap(bitmap, targetW, targetH, true);
    }

    public static void drawBitmapToCanvas(@NonNull final Canvas canvas, @NonNull final Bitmap bitmap,
                                          int left, int top) throws OutOfMemoryError {
        canvas.drawBitmap(bitmap, left, top, null);
        bitmap.recycle();
    }

    public static void drawBitmapToCanvas(@NonNull final Canvas canvas, @NonNull final Bitmap bitmap,
                                          Matrix matrix) throws OutOfMemoryError {
        canvas.drawBitmap(bitmap, matrix, null);
        bitmap.recycle();
    }

    public static void drawBlurToCanvas(@NonNull final Canvas canvas, @NonNull final Bitmap bitmap,
                                        int radius) throws OutOfMemoryError {
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
    public static Bitmap scaleCenterCrop(@NonNull final Bitmap source, int newWidth, int newHeight) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);
        source.recycle();
        return dest;
    }

    /** http://github.com/yesidlazaro/BadgedImageView */
    @TargetApi(12) public static Bitmap bitmapBadge(@NonNull final Context context,
                                                    @NonNull final String badgeText,
                                                    @ColorInt int badgeColor, int badgeSize) {
        final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        final float density = displayMetrics.density;
        final float scaleDensity = displayMetrics.scaledDensity;
        final float padding = 8 * density;
        final float cornerRadius = 8 * density;
        final String text = badgeText.toUpperCase(Locale.US);
        final Rect textBounds = new Rect();


        final TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
        textPaint.setTypeface(FontUtils.getBadgeTypeface());
        textPaint.setTextSize(badgeSize * scaleDensity);
        textPaint.getTextBounds(text, 0, text.length(), textBounds);
        final int width = (int) (padding + textBounds.width() + padding);
        final int height = (int) (padding + textBounds.height() + padding);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        if (Utils.isHoneycombMR1()) bitmap.setHasAlpha(true);
        final Canvas canvas = new Canvas(bitmap);

        final Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(BitmapUtils.setHalfAlphaColor(badgeColor));

        final RectF rectF = new RectF(0, 0, width, height);
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, backgroundPaint);

        textPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawText(text, padding, height - padding, textPaint);
        return bitmap;
    }

    @ColorInt public static int setHalfAlphaColor(@ColorInt int source) {
        return Color.argb(128, Color.red(source), Color.green(source), Color.blue(source));
    }

    ////////////////// Template Default //////////////////
    public static Bitmap getNinePatch(@NonNull final Context context, @DrawableRes int drawableID,
                                      int width, int height) {
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        NinePatchDrawable npd = (NinePatchDrawable) ContextCompat.getDrawable(context, drawableID);
        if (npd != null) {
            npd.setBounds(0, 0, width, height);
            npd.draw(canvas);
        }
        return result;
    }

    /**
     * {@link Bitmap} for
     * {@link android.support.v4.app.NotificationCompat.BigPictureStyle#bigPicture(Bitmap)}
     */
    public static Bitmap previewBigPicture(@NonNull final Bitmap source) {
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
        matrix.postTranslate((shortSide - imageWidth) / 2f, (shortSide - imageHeight) / 2f);
        canvas.drawBitmap(screenshot, matrix, paint);
        canvas.drawColor(BitmapUtils.setHalfAlphaColor(Color.DKGRAY));
        screenshot.recycle();
        return preview;
    }

    ////////////////// HishootService //////////////////
    public static Bitmap roundedLargeIcon(@NonNull final Context context, @NonNull final Bitmap bitmap) {
        final int iconSize = Utils.getDimensionPixelSize(context, android.R.dimen.app_icon_size);
        final float halfIconSize = (float) (iconSize / (double) 2);
        final Bitmap scale = Bitmap.createScaledBitmap(bitmap, iconSize, iconSize, true);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, iconSize, iconSize);
        paint.setAntiAlias(true);
        Bitmap rounded = Bitmap.createBitmap(iconSize, iconSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(rounded);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(halfIconSize, halfIconSize, halfIconSize, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(scale, rect, rect, paint);
        scale.recycle();
        return rounded;
    }
}
