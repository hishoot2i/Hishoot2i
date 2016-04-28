package org.illegaller.ratabb.hishoot2i.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class FileExtensionFilter implements FilenameFilter {
  private Set<String> extSet = new HashSet<>();

  public FileExtensionFilter(String... extension) {
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
