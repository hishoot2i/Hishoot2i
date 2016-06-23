package org.illegaller.ratabb.hishoot2i.utils;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static android.content.pm.PackageManager.NameNotFoundException;

public class ResUtils {
  protected ResUtils() {
    throw new AssertionError("no instance");
  }

  public static InputStream openStreamFromAsset(final Context context, final String packageName,
      final String assetName) throws NameNotFoundException, IOException {
    return ResUtils.createPackageContext(context, packageName).getAssets().open(assetName);
  }

  public static Point getPointBitmapTemplate(final Context context, final String packageName,
      final String drawableName) throws NameNotFoundException {
    final Context contextTemplate = ResUtils.createPackageContext(context, packageName);
    int resId = ResUtils.getResIdTemplate(contextTemplate, packageName, drawableName);
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeResource(contextTemplate.getResources(), resId, options);
    return new Point(options.outWidth, options.outHeight);
  }

  public static String getStringFilePathHtz(final File currentPath, String fileName) {
    return UILHelper.stringFiles(new File(currentPath, fileName));
  }

  public static String getStringFilePath(final Context context, final String packageName,
      final String fileName) throws NameNotFoundException {
    @DrawableRes int drawableRes = ResUtils.getResIdTemplate(context, packageName, fileName);
    return UILHelper.stringTemplateApp(packageName, drawableRes);
  }

  @DrawableRes public static int getResIdTemplate(final Context context, final String packageName,
      final String resourceName) throws NameNotFoundException {
    final Context contextTarget = ResUtils.createPackageContext(context, packageName);
    return contextTarget.getResources().getIdentifier(resourceName, "drawable", packageName);
  }

  @ColorInt public static int getColorInt(final Context context, @ColorRes int colorRes) {
    return ContextCompat.getColor(context, colorRes);
  }

  public static Context createPackageContext(final Context context, final String packageName)
      throws NameNotFoundException {
    return context.createPackageContext(packageName, 0);
  }

  public static int getDimensionPixelSize(final Context context, @DimenRes int dimenId) {
    return context.getResources().getDimensionPixelSize(dimenId);
  }

  public static Drawable getDrawable(final Context context, @DrawableRes int drawableRes) {
    return ContextCompat.getDrawable(context, drawableRes);
  }

  public static Drawable getVectorDrawable(final Context context, @DrawableRes int drawableRes) {
    return VectorDrawableCompat.create(context.getResources(), drawableRes, context.getTheme());
  }
}
