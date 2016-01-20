package org.illegaller.ratabb.hishoot2i;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

public class AppConstants {

    public static final int REQ_PICK_IMG_SS1 = 0x0001;
    public static final int REQ_PICK_IMG_SS2 = 0x0002;
    public static final int REQ_PICK_IMG_BG = 0x0003;
    public static final int REQ_PICK_HTZ = 0x0004;

    public static final int BACKGROUND_IMAGE_BLUR_RADIUS = 25;
    public static final int BADGE_COLOR = Color.WHITE;
    public static final int BADGE_SIZE = 24;
    public static final String BADGE_TEXT = "HISHOOT";
    public static final String BADGE_TYPEFACE = "Default";
    /**
     * <strong>Category Hishoot Template App</strong><br><br>
     * Constant value: <i>"dcsms.hishoot.SKINTEMPLATE"</i>
     */
    public static final String CATEGORY_TEMPLATE_APK = "dcsms.hishoot.SKINTEMPLATE";

    /**
     * <strong>META-DATA Hishoot Template App v2</strong><br><br>
     * Constant value: <i>"org.illegaller.ratabb.hishoot2i.TEMPLATE"</i>
     */
    public static final String META_DATA_TEMPLATE = "org.illegaller.ratabb.hishoot2i.TEMPLATE";

    public static final String DEFAULT_TEMPLATE_ID = "default";

    protected AppConstants() {
        throw new AssertionError("AppConstants no construction");
    }

    /**
     * File directory Hishoot on external Storage,<br>
     * create new file if we don't have it
     *
     * @return a {@link File} directory
     */
    public static File getHishootDir() {// TODO: check storage state
        final File result = new File(Environment.getExternalStorageDirectory(), "HiShoot");
        if (!result.exists()) result.mkdirs();
        return result;
    }

    /**
     * {@code /data/data/packageName/files/htz}
     */
    public static File getHishootHtzDir(final Context context) {
        File file = new File(context.getFilesDir(), "htz");
        if (!file.exists()) file.mkdirs();
        try {
            (new File(file, ".nomedia")).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
