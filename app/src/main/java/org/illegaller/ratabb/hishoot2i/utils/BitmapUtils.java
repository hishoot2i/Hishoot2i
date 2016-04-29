package org.illegaller.ratabb.hishoot2i.utils;

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
import com.enrique.stackblur.StackBlurManager;
import java.util.Locale;
import net.grandcentrix.tray.AppPreferences;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.model.tray.IKeyNameTray;

import static android.os.Build.VERSION_CODES.HONEYCOMB_MR1;
import static org.illegaller.ratabb.hishoot2i.AppConstants.DEFAULT_TEMPLATE_ID;

public class BitmapUtils {
  private static final int MAX_BLUR_RADIUS = 100;

  private BitmapUtils() { /*no instance*/ }

  @Nullable public static Bitmap alphaPatternBitmap(Context context) {
    final AppPreferences appPreferences = new AppPreferences(context);
    final int width = appPreferences.getInt(IKeyNameTray.DEVICE_WIDTH, 320);
    final int height = appPreferences.getInt(IKeyNameTray.DEVICE_HEIGHT, 480);
    return BitmapUtils.alphaPatternBitmap(context, width, height);
  }

  @Nullable public static Bitmap alphaPatternBitmap(Context context, int width, int height) {
    int density = (int) context.getResources().getDisplayMetrics().density;
    return BitmapUtils.alphaPatternBitmap(5 * density, width, height);
  }

  @Nullable public static Bitmap alphaPatternBitmap(int rectangleSize, int width, int height) {
    Paint mPaintWhite = new Paint();
    Paint mPaintGray = new Paint();
    mPaintWhite.setColor(0xffffffff);
    mPaintGray.setColor(0xffcbcbcb);
    int numRectanglesHorizontal = (int) Math.ceil((width / rectangleSize));
    int numRectanglesVertical = (int) Math.ceil(height / rectangleSize);
    Bitmap result;
    try {
      result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    } catch (Exception e) {
      CrashLog.logError(" w:" + width + " h:" + height, e);
      return null;
    }
    assert result != null;
    Canvas canvas = new Canvas(result);
    Rect rect = new Rect();
    boolean verticalStartWhite = true;
    for (int i = 0; i <= numRectanglesVertical; i++) {
      boolean isWhite = verticalStartWhite;
      for (int j = 0; j <= numRectanglesHorizontal; j++) {
        rect.top = i * rectangleSize;
        rect.left = j * rectangleSize;
        rect.bottom = rect.top + rectangleSize;
        rect.right = rect.left + rectangleSize;
        canvas.drawRect(rect, isWhite ? mPaintWhite : mPaintGray);
        isWhite = !isWhite;
      }
      verticalStartWhite = !verticalStartWhite;
    }
    return result;
  }

  @Nullable public static Bitmap mixTemplate(final Context context, final Template template,
      final String pathSS, boolean glareEnable, boolean shadowEnable, boolean frameEnable)
      throws OutOfMemoryError {
    final int templateW = template.templatePoint.x;
    final int templateH = template.templatePoint.y;
    Matrix matrix = new Matrix();
    Bitmap result;
    try {
      result = Bitmap.createBitmap(templateW, templateH, Bitmap.Config.ARGB_8888);
    } catch (OutOfMemoryError e) {
      CrashLog.logError("Template:" + template.id + " w:" + templateW + " h:" + templateH, e);
      return null;
    }
    assert result != null;
    Canvas canvas = new Canvas(result);
    Bitmap ss =
        pathSS != null ? UILHelper.loadImage(pathSS) : BitmapUtils.alphaPatternBitmap(context);
    Bitmap frame, shadow, glare;
    switch (template.type) {
      case APK_V1:
        if (ss != null) BitmapUtils.drawPerspective(canvas, ss, template);
        if (template.id.equals(DEFAULT_TEMPLATE_ID)) {
          frame = BitmapUtils.getNinePatch(context, R.drawable.frame1, templateW, templateH);
        } else {
          frame = UILHelper.loadImage(template.frameFile, template.templatePoint);
        }
        if (frame != null) {
          Bitmap bitmap = BitmapUtils.matchSizes(frame, templateW, templateH);
          if (bitmap != null) BitmapUtils.drawBitmapToCanvas(canvas, bitmap, matrix);
        }
        break;
      case APK_V2:
        if (template.shadowFile != null) {
          shadow = UILHelper.loadImage(template.shadowFile, template.templatePoint);
          if (shadow != null && shadowEnable) {
            Bitmap bitmap = BitmapUtils.matchSizes(shadow, templateW, templateH);
            if (bitmap != null) BitmapUtils.drawBitmapToCanvas(canvas, bitmap, matrix);
          }
        }
        if (template.frameFile != null) {
          frame = UILHelper.loadImage(template.frameFile, template.templatePoint);
          if (frame != null && frameEnable) {
            Bitmap bitmap = BitmapUtils.matchSizes(frame, templateW, templateH);
            if (bitmap != null) BitmapUtils.drawBitmapToCanvas(canvas, bitmap, matrix);
          }
        }
        if (ss != null) BitmapUtils.drawPerspective(canvas, ss, template);
        if (template.glareFile != null) {
          glare = UILHelper.loadImage(template.glareFile, template.templatePoint);
          if (glare != null && glareEnable) {
            Bitmap bitmap = BitmapUtils.matchSizes(glare, templateW, templateH);
            if (bitmap != null) BitmapUtils.drawBitmapToCanvas(canvas, bitmap, matrix);
          }
        }
        break;
      case HTZ:
        if (template.frameFile != null) {
          frame = UILHelper.loadImage(template.frameFile, template.templatePoint);
          if (frame != null) {
            Bitmap bitmap = BitmapUtils.matchSizes(frame, templateW, templateH);
            if (bitmap != null) BitmapUtils.drawBitmapToCanvas(canvas, bitmap, matrix);
          }
        }
        if (ss != null) BitmapUtils.drawPerspective(canvas, ss, template);
        if (template.glareFile != null) {
          glare = UILHelper.loadImage(template.glareFile);
          if (template.overlayOffset != null) {
            matrix.postTranslate(template.overlayOffset.x, template.overlayOffset.y);
          }
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
    //coordinate source
    final float tWidth = bitmap.getWidth();
    final float tHeight = bitmap.getHeight();
    final float[] src = new float[] {
        0, 0, tWidth, 0, 0, tHeight, tWidth, tHeight
    };
    //coordinate perspective
    final float leftTopX = template.leftTop.x;
    final float leftTopY = template.leftTop.y;
    final float rightTopX = template.rightTop.x;
    final float rightTopY = template.rightTop.y;
    final float leftBottomX = template.leftBottom.x;
    final float leftBottomY = template.leftBottom.y;
    final float rightBottomX = template.rightBottom.x;
    final float rightBottomY = template.rightBottom.y;
    final float[] dst = new float[] {
        leftTopX, leftTopY, rightTopX, rightTopY, leftBottomX, leftBottomY, rightBottomX,
        rightBottomY
    };
    //drawing
    Matrix matrix = new Matrix();
    Paint paint = new Paint();
    matrix.setPolyToPoly(src, 0, dst, 0, src.length / 2);
    canvas.save();
    canvas.concat(matrix);
    canvas.drawBitmap(bitmap, 0, 0, paint);
    canvas.restore();
    bitmap.recycle();
  }

  @Nullable private static Bitmap matchSizes(@NonNull final Bitmap bitmap, int targetW, int targetH)
      throws OutOfMemoryError {
    if (bitmap.getWidth() == targetW && bitmap.getHeight() == targetH) {
      return bitmap;
    } else {
      try {
        return Bitmap.createScaledBitmap(bitmap, targetW, targetH, true);
      } catch (OutOfMemoryError e) {
        CrashLog.logError("target w:" + targetW + " h:" + targetH, e);
        return null;
      }
    }
  }

  public static void drawBitmapToCanvas(@NonNull final Canvas canvas, @NonNull final Bitmap bitmap,
      int left, int top) throws OutOfMemoryError {
    try {
      canvas.drawBitmap(bitmap, left, top, null);
      bitmap.recycle();
    } catch (OutOfMemoryError e) {
      CrashLog.logError("drawBitmapToCanvas", e);
    }
  }

  public static void drawBitmapToCanvas(@NonNull final Canvas canvas, @NonNull final Bitmap bitmap,
      Matrix matrix) throws OutOfMemoryError {
    try {
      canvas.drawBitmap(bitmap, matrix, null);
      bitmap.recycle();
    } catch (OutOfMemoryError e) {
      CrashLog.logError("drawBitmapToCanvas", e);
    }
  }

  public static void drawBlurToCanvas(@NonNull final Canvas canvas, @NonNull final Bitmap bitmap,
      int radius) throws OutOfMemoryError {
    int rad = (radius > MAX_BLUR_RADIUS) ? MAX_BLUR_RADIUS : radius;
    int widthSrc = bitmap.getWidth();
    int heightSrc = bitmap.getHeight();
    float ratio = .5f;
    int scaledW = (int) ((float) widthSrc * ratio);
    int scaledH = (int) ((float) heightSrc * ratio);
    try {
      Bitmap scaledDown = Bitmap.createScaledBitmap(bitmap, scaledW, scaledH, true);
      bitmap.recycle();
      StackBlurManager stackBlurManager = new StackBlurManager(scaledDown);
      Bitmap blur = stackBlurManager.process(rad);
      scaledDown.recycle();
      Bitmap scaledUp = Bitmap.createScaledBitmap(blur, widthSrc, heightSrc, true);
      blur.recycle();
      BitmapUtils.drawBitmapToCanvas(canvas, scaledUp, 0, 0);
    } catch (OutOfMemoryError e) {
      CrashLog.logError("drawBitmapToCanvas", e);
    }
  }

  /** http://stackoverflow.com/a/8113368 */
  public static Bitmap scaleCenterCrop(@NonNull final Bitmap source, int newWidth, int newHeight)
      throws OutOfMemoryError {
    int sourceWidth = source.getWidth();
    int sourceHeight = source.getHeight();
    //coordinate
    float xScale = (float) newWidth / sourceWidth;
    float yScale = (float) newHeight / sourceHeight;
    float scale = Math.max(xScale, yScale);
    float scaledWidth = scale * sourceWidth;
    float scaledHeight = scale * sourceHeight;
    float left = (newWidth - scaledWidth) / 2;
    float top = (newHeight - scaledHeight) / 2;
    try {
      //drawing
      RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);
      Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
      Canvas canvas = new Canvas(dest);
      canvas.drawBitmap(source, null, targetRect, null);
      source.recycle();
      return dest;
    } catch (OutOfMemoryError e) {
      CrashLog.logError("newWidth:" + newWidth + " newHight:" + newHeight, e);
      return null;
    }
  }

  /** http://github.com/yesidlazaro/BadgedImageView */
  @TargetApi(HONEYCOMB_MR1) public static Bitmap bitmapBadge(@NonNull final Context context,
      @NonNull final String badgeText, @ColorInt int badgeColor, int badgeSize) {
    final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
    final float density = displayMetrics.density;
    final float scaleDensity = displayMetrics.scaledDensity;
    final float padding = 8 * density;
    final float cornerRadius = 8 * density;
    final String text = badgeText.toUpperCase(Locale.US);
    final Rect textBounds = new Rect();
    //text
    final TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
    textPaint.setTypeface(FontUtils.getBadgeTypeface());
    textPaint.setTextSize(badgeSize * scaleDensity);
    textPaint.getTextBounds(text, 0, text.length(), textBounds);
    final int width = (int) (padding + textBounds.width() + padding);
    final int height = (int) (padding + textBounds.height() + padding);
    //construct canvas
    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    if (DeviceUtils.isCompatible(HONEYCOMB_MR1)) bitmap.setHasAlpha(true);
    final Canvas canvas = new Canvas(bitmap);
    //background
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
  // FIXME: Api 16 - template default not show ?
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
    // FIXME: w, h constant ?
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
  public static Bitmap roundedLargeIcon(@NonNull final Context context,
      @NonNull final Bitmap bitmap) {
    final int iconSize = ResUtils.getDimensionPixelSize(context, android.R.dimen.app_icon_size);
    final float halfIconSize = (float) (iconSize / (double) 2);
    Paint paint = new Paint();
    Rect rect = new Rect(0, 0, iconSize, iconSize);
    paint.setAntiAlias(true);
    Bitmap rounded = Bitmap.createBitmap(iconSize, iconSize, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(rounded);
    canvas.drawARGB(0, 0, 0, 0);
    canvas.drawCircle(halfIconSize, halfIconSize, halfIconSize, paint);
    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    final Bitmap scale = Bitmap.createScaledBitmap(bitmap, iconSize, iconSize, true);
    canvas.drawBitmap(scale, rect, rect, paint);
    scale.recycle();
    return rounded;
  }
}
