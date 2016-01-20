package org.illegaller.ratabb.hishoot2i.utils;

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

import org.illegaller.ratabb.hishoot2i.BuildConfig;
import org.illegaller.ratabb.hishoot2i.model.Sizes;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Universal Image Loader Helper
 */
public class UILHelper {
    private static final String FILES = "file://";
    private static final String TEMPLATE_APP = "template_app://";
    private static final String DRAWABLES = "drawable://";
    private static final String SEPARATOR = "/";

    protected UILHelper() {
        throw new AssertionError("UILHelper no construction");
    }

    public static String stringTemplateApp(String templateId, @DrawableRes int resId) {
        return UILHelper.formatString(TEMPLATE_APP, templateId + SEPARATOR + resId);
    }

    public static String stringDrawables(@DrawableRes int resId) {
        return UILHelper.formatString(DRAWABLES, String.valueOf(resId));
    }

    public static String stringFiles(File file) {
        return UILHelper.formatString(FILES, file.getAbsolutePath());
    }

    public static void init(@NonNull final Context context, int width, int height) {
        final File cacheDir = StorageUtils.getCacheDirectory(context);
        DiskCache diskCache = null;
        long cacheMaxSize = 50 * 1024 * 1024;
        try {
            diskCache = new LruDiskCache(cacheDir, new HashCodeFileNameGenerator(), cacheMaxSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context)
                .memoryCacheExtraOptions(width, height)
                .diskCacheExtraOptions(width, height, null)
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .denyCacheImageMultipleSizesInMemory()
                .imageDecoder(new BaseImageDecoder(true))
                .imageDownloader(new TemplateImageDownloader(context))
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple());

        if (BuildConfig.DEBUG) config.writeDebugLogs();
        if (null != diskCache) config.diskCache(diskCache);

        ImageLoader.getInstance().init(config.build());
    }

    public static void displayPreview(final ImageView imageView, final String pathImage) {
        ImageLoader.getInstance().displayImage(pathImage, imageView,
                getDisplayImageOptionsPreview());
    }

//    @Nullable public static Bitmap loadImageX(final String pathImage) {
//        // FIXME: handle OOM
//        tryClearMemoryCache();
//        return ImageLoader.getInstance().loadImageSync(pathImage, getDisplayImageOptionsProcess());
//    }

    @Nullable public static Bitmap loadImage(final String pathImage) {
        return loadImage(pathImage, null);
    }

    @Nullable public static Bitmap loadImage(final String pathImage, final Sizes sizesTarget) {
        if (sizesTarget != null) {
            final ImageSize imageSize = new ImageSize(sizesTarget.width, sizesTarget.height);
            return ImageLoader.getInstance()
                    .loadImageSync(pathImage, imageSize, getDisplayImageOptionsProcess());
        } else {
            return ImageLoader.getInstance()
                    .loadImageSync(pathImage, getDisplayImageOptionsProcess());
        }
    }

    public static void tryClearMemoryCache() {
        ImageLoader.getInstance().clearMemoryCache();
    }

    private static String formatString(String type, String data) {
        return String.format("%s%s", type, data);
    }

    private static DisplayImageOptions getDisplayImageOptionsPreview() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = true;
        DisplayImageOptions.Builder result = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .cacheOnDisk(true)
                .cacheInMemory(false)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
//                .showImageOnLoading(R.drawable.braded_logo)
                .decodingOptions(options);
        return result.build();
    }

    private static DisplayImageOptions getDisplayImageOptionsProcess() {
        DisplayImageOptions.Builder result = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .cacheOnDisk(false)
                .cacheInMemory(false)
                .imageScaleType(ImageScaleType.NONE);
        return result.build();
    }

    /**
     * {@link UILHelper#stringTemplateApp(String, int)}
     * http://stackoverflow.com/a/28010629
     */
    private static class TemplateImageDownloader extends BaseImageDownloader {

        public TemplateImageDownloader(Context context) {
            super(context);
        }

        @Override protected InputStream getStreamFromOtherSource(String imageUri, Object extra)
                throws IOException {
            if (imageUri.startsWith(TEMPLATE_APP)) {
                /* imageUri = TEMPLATE_APP://APP_PACKAGE_NAME/RESOURCE_ID*/
                try {
                    String drawableString = imageUri.replace(TEMPLATE_APP, "");
                    /* drawableString = APP_PACKAGE_NAME/RESOURCE_ID */
                    String[] location = drawableString.split(SEPARATOR);
                    /* location={ APP_PACKAGE_NAME, RESOURCE_ID  }*/
                    PackageManager packageManager = context.getPackageManager();
                    Resources res = packageManager.getResourcesForApplication(location[0]);
                    return res.openRawResource(Integer.parseInt(location[1]));
                } catch (PackageManager.NameNotFoundException e) {
                    throw new RuntimeException(imageUri);
                }
            } else throw new UnsupportedOperationException(imageUri);
        }
    }


}
