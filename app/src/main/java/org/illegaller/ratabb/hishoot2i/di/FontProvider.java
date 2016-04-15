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
    private final Map<String, File> fontFileMap;// fontName ,fontFile
    private FileExtensionFilter filter = new FileExtensionFilter("ttf");
    private List<File> fontFileList;

    public FontProvider() {
        this.fontFileMap = new HashMap<>();
        provideFontSdcard();
    }

    public List<File> asListFile() {
        if (fontFileList == null) fontFileList = new ArrayList<>(fontFileMap.values());
        return fontFileList;
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
        return fontFileMap.get(name);
    }

    /////// private method ////////
    private void provideFontSdcard() {
        for (String font : pathFonts) {
            File dir = new File(Environment.getExternalStorageDirectory(), font);
            if (dir.isDirectory()) {
                File[] files = dir.listFiles(filter);
                for (File file : files) if (file != null) putToMapFonts(file);
            }
        }
    }

    private void putToMapFonts(@NonNull final File file) {
        final String name = Utils.getFileNameWithoutExtension(file.getAbsolutePath());
        if (fontFileMap.containsKey(name) && !file.canRead()) return;
        fontFileMap.put(name, file);
    }

    private class FileExtensionFilter implements FilenameFilter {
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
