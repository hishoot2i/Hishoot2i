package org.illegaller.ratabb.hishoot2i;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import java.io.File;
import java.io.IOException;
import org.illegaller.ratabb.hishoot2i.utils.CrashLog;

public class AppConstants {

  public static final int BG_IMAGE_BLUR_RADIUS = 25;
  public static final int BADGE_COLOR = Color.RED;
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
  public static final String MESSAGE_TEMPLATE_CANT_LOAD = "Template %s can't load";
  public static final String MESSAGE_TEMPLATE_CANT_FIND = "Template %s can't find";

  private AppConstants() { /*no instance*/ }

  /**
   * File directory Hishoot on external Storage,<br>
   * create new file if we don't have it
   *
   * @return a {@link File} directory
   */
  public static File getHishootDir() {
    final File result = new File(Environment.getExternalStorageDirectory(), "HiShoot");
    if (!result.exists()) {
      boolean ignored = result.mkdirs();
    }
    return result;
  }

  /**
   * {@code /data/data/packageName/files/htz}
   */
  public static File getHishootHtzDir(final Context context) {
    File file = new File(context.getFilesDir(), "htz");
    if (!file.exists()) {
      boolean ignored = file.mkdirs();
    }
    try {
      boolean ignored = (new File(file, ".nomedia")).createNewFile();
    } catch (IOException e) {
      CrashLog.logError("createNewFile .nomedia", e);
    }
    return file;
  }
}
