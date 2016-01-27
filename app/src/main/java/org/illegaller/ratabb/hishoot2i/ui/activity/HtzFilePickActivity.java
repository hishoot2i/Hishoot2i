package org.illegaller.ratabb.hishoot2i.ui.activity;

import com.nononsenseapps.filepicker.AbstractFilePickerFragment;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.nononsenseapps.filepicker.FilePickerFragment;

import android.os.Environment;
import android.support.annotation.NonNull;

import java.io.File;

public class HtzFilePickActivity extends FilePickerActivity {

    @Override protected AbstractFilePickerFragment<File> getFragment(String startPath, int mode,
                                                                     boolean allowMultiple,
                                                                     boolean allowCreateDir) {
        AbstractFilePickerFragment<File> fragment = new HtzFilePickFragment();
        fragment.setArgs(startPath != null ? startPath :
                Environment.getExternalStorageDirectory().getPath(), mode, allowMultiple, allowCreateDir);
        return fragment;
    }

    public static class HtzFilePickFragment extends FilePickerFragment {
        private static final String EXTENSION = ".htz";

        private String getExtension(@NonNull File file) {
            String path = file.getPath();
            int i = path.lastIndexOf(".");
            if (i < 0) {
                return null;
            } else {
                return path.substring(i);
            }
        }

        @Override
        protected boolean isItemVisible(final File file) {
            boolean ret = super.isItemVisible(file);
            if (ret && !isDir(file) && (mode == MODE_FILE || mode == MODE_FILE_AND_DIR)) {
                ret = EXTENSION.equalsIgnoreCase(getExtension(file));
            }
            return ret;
        }
    }
}
