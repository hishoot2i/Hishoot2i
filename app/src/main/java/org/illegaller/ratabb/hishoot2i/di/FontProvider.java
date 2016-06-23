package org.illegaller.ratabb.hishoot2i.di;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.illegaller.ratabb.hishoot2i.utils.FileExtensionFilter;

import static org.illegaller.ratabb.hishoot2i.utils.Utils.checkNotNull;
import static org.illegaller.ratabb.hishoot2i.utils.Utils.getFileNameWithoutExtension;

public class FontProvider {
  private static final String[] mPathFonts = new String[] { "fonts", "Fonts" };
  private final Map<String, File> mFontFileMap = new HashMap<>(); /* fontName, fontFile*/
  private final FileExtensionFilter mFileExtFilter = new FileExtensionFilter("ttf");
  private List<File> mFontFileList;

  @Inject FontProvider() {
    provideFontSdcard();
  }

  private List<File> asListFile() {
    if (mFontFileList == null) mFontFileList = new ArrayList<>(mFontFileMap.values());
    return mFontFileList;
  }

  public List<String> asListName() {
    List<File> list = asListFile();
    List<String> result = new ArrayList<>();
    for (int i = 0; i < list.size(); i++) {
      result.add(i, getFileNameWithoutExtension(list.get(i).getAbsolutePath()));
    }
    return result;
  }

  @Nullable public File find(@NonNull final String name) {
    if (mFontFileMap.containsKey(checkNotNull(name, "name == null"))) return mFontFileMap.get(name);
    return null;
  }

  /////// private method ////////
  private void provideFontSdcard() {
    for (String font : mPathFonts) {
      File dir = new File(Environment.getExternalStorageDirectory(), font);
      if (dir.isDirectory()) {
        File[] files = dir.listFiles(mFileExtFilter);
        for (File file : files) {
          if (file.exists()) putToMapFonts(file);
        }
      }
    }
  }

  private void putToMapFonts(@NonNull final File file) {
    checkNotNull(file, "file == null");
    final String name = getFileNameWithoutExtension(file.getAbsolutePath());
    /* avoid duplicate font file, and file cannot read */
    if (!mFontFileMap.containsKey(name) && file.canRead()) mFontFileMap.put(name, file);
  }
}
