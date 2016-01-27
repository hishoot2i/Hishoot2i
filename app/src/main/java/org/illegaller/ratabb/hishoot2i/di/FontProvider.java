package org.illegaller.ratabb.hishoot2i.di;

import org.illegaller.ratabb.hishoot2i.utils.Utils;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class FontProvider {
    private static final String[] pathFonts = new String[]{"fonts", "Fonts"};
    private final Map<String, File> mapFontFile;// fontName ,fontFile
    private FileExtensionFilter filter = new FileExtensionFilter("ttf");
    private List<File> fontList;

    public FontProvider() {
        this.mapFontFile = new HashMap<>();
        provideFontSdcard();
    }

    public List<File> asListFile() {
        if (fontList == null) {
            fontList = new ArrayList<>(mapFontFile.values());
        }
        return fontList;
    }

    public List<String> asListName() {
        List<File> list = asListFile();
        List<String> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            result.add(i, Utils.getFileNameWithoutExtension(list.get(i).getAbsolutePath()));
        }
        return result;
    }

    @Nullable public File find(@NonNull final String name) {
        return mapFontFile.get(name);
    }

    /////// private method ////////
    private void provideFontSdcard() {
        for (String font : pathFonts) {
            File dir = new File(Environment.getExternalStorageDirectory(), font);
            if (dir.isDirectory()) {
                File[] files = dir.listFiles(filter);
                for (File file : files)if (file!=null)putToMapFonts(file);
            }
        }
    }

    private void putToMapFonts(@NonNull final File file) {
        final String name = Utils.getFileNameWithoutExtension(file.getAbsolutePath());
        if (mapFontFile.containsKey(name) && !file.canRead()) return;
        mapFontFile.put(name, file);
    }

    class FileExtensionFilter implements FilenameFilter {
        private Set<String> extSet = new HashSet<>();

        FileExtensionFilter(String... extension) {
            for (String ext : extension) {
                extSet.add("." + ext.toLowerCase(Locale.US).trim());
            }
        }

        @Override public boolean accept(File file, String s) {
            for (String ext : extSet) {
                if (s.toLowerCase(Locale.US).endsWith(ext)) return true;
            }
            return false;
        }
    }
}
