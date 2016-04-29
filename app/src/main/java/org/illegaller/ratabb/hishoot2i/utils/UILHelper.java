package org.illegaller.ratabb.hishoot2i.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.illegaller.ratabb.hishoot2i.BuildConfig;

/**
 * <b>UIL: Universal Image Loader</b> Helper
 */
public class UILHelper {
  private static final String FILES = "file://";
  private static final String TEMPLATE_APP = "template_app://";
  private static final String DRAWABLES = "drawable://";
  private static final String SEPARATOR = "/";

  private UILHelper() { /*no instance*/ }

  /**
   * Image path from app template
   *
   * @param templateId packageName app template.
   * @param resId drawable resources identity
   * @return {@linkplain String} <b>template_app://{@code templateId/resId }</b>
   */
  public static String stringTemplateApp(String templateId, @DrawableRes int resId) {
    return TEMPLATE_APP + templateId + SEPARATOR + resId;
  }

  /**
   * Image path from default template
   *
   * @param resId drawable resources identity
   * @return {@linkplain String}  <b>drawable://{@code resId}</b>
   */
  public static String stringDrawables(@DrawableRes int resId) {
    return DRAWABLES + resId;
  }

  /**
   * Image path from user storage
   *
   * @param file image file
   * @return {@linkplain String}  <b>file://{@code absolute-path-of-file}</b>
   */
  public static String stringFiles(File file) {
    return FILES + file.getAbsolutePath();
  }

  /**
   * Initialize <b>UIL</b> with customize configuration
   *
   * @param context context from app
   * @param width device width
   * @param height device height
   */
  public static void init(@NonNull final Context context, int width, int height) {
    final File cacheDir = StorageUtils.getCacheDirectory(context);
    DiskCache diskCache = null;
    long cacheMaxSize = 50 * 1024 * 1024; //50MB
    try {
      diskCache = new LruDiskCache(cacheDir, new HashCodeFileNameGenerator(), cacheMaxSize);
    } catch (IOException e) {
      CrashLog.logError("diskCache on init UIL", e);
    }
    ImageLoaderConfiguration.Builder config =
        new ImageLoaderConfiguration.Builder(context).memoryCacheExtraOptions(width, height)
            .diskCacheExtraOptions(width, height, null)
            .threadPoolSize(3)
            .threadPriority(Thread.NORM_PRIORITY - 2)
            .tasksProcessingOrder(QueueProcessingType.FIFO)
            .denyCacheImageMultipleSizesInMemory()
            .imageDecoder(new BaseImageDecoder(BuildConfig.DEBUG))
            .imageDownloader(new TemplateImageDownloader(context))
            .defaultDisplayImageOptions(DisplayImageOptions.createSimple());
        /*if (BuildConfig.DEBUG) config.writeDebugLogs();*/
    if (null != diskCache) config.diskCache(diskCache);
    ImageLoader.getInstance().init(config.build());
  }

  public static void displayPreview(final ImageView imageView, final String pathImage) {
    ImageLoader.getInstance()
        .displayImage(pathImage, imageView, UILHelper.getDisplayImageOptions(true));
  }

  public static long sizeCache() {
    DiskCache diskCache = null;
    long result = 0L;
    try {
      diskCache = UILHelper.diskCache();
    } catch (IllegalStateException e) {
      CrashLog.logError("diskCache", e);
    }
    if (diskCache != null) {
      for (File cache : diskCache.getDirectory().listFiles()) result += cache.length();
    }
    return result;
  }

  public static void clearCache() throws IllegalStateException {
    DiskCache diskCache = UILHelper.diskCache();
    if (diskCache != null) diskCache.clear();
  }

  static DiskCache diskCache() throws IllegalStateException {
    return ImageLoader.getInstance().getDiskCache();
  }

  @Nullable public static Bitmap loadImage(final String pathImage) {
    return ImageLoader.getInstance()
        .loadImageSync(pathImage, UILHelper.getDisplayImageOptions(false));
  }

  @Nullable public static Bitmap loadImage(final String pathImage, final Point point) {
    return ImageLoader.getInstance()
        .loadImageSync(pathImage, new ImageSize(point.x, point.y),
            UILHelper.getDisplayImageOptions(false));
  }

  static DisplayImageOptions getDisplayImageOptions(boolean isPreview) {
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inScaled = isPreview;
    DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder().bitmapConfig(
        isPreview ? Bitmap.Config.RGB_565 : Bitmap.Config.ARGB_8888)
        .cacheOnDisk(isPreview)
        .cacheInMemory(false)
        .imageScaleType(isPreview ? ImageScaleType.IN_SAMPLE_POWER_OF_2 : ImageScaleType.NONE)
        .decodingOptions(options);
    return builder.build();
  }

  /**
   *  ***
   * {@link UILHelper#stringTemplateApp(String, int)}<br>
   * inspired by: http://stackoverflow.com/a/28010629
   */
  static class TemplateImageDownloader extends BaseImageDownloader {

    public TemplateImageDownloader(Context context) {
      super(context);
    }

    @Override protected InputStream getStreamFromOtherSource(String imageUri, Object extra)
        throws IOException {
            /* imageUri = TEMPLATE_APP://APP_PACKAGE_NAME/RESOURCE_ID*/
      if (imageUri.startsWith(TEMPLATE_APP)) {
        try {
                    /* drawableString = APP_PACKAGE_NAME/RESOURCE_ID */
          String drawableString = imageUri.replace(TEMPLATE_APP, "");
                     /* location={ APP_PACKAGE_NAME, RESOURCE_ID  }*/
          String[] location = drawableString.split(SEPARATOR);
          PackageManager packageManager = context.getPackageManager();
          Resources res = packageManager.getResourcesForApplication(location[0]);
          return res.openRawResource(Integer.parseInt(location[1]));
        } catch (PackageManager.NameNotFoundException e) {
          throw new UnsupportedOperationException(imageUri);
        }
      } else {
        throw new UnsupportedOperationException(imageUri);
      }
    }
  }
}
