package org.illegaller.ratabb.hishoot2i.view.fragment;

import org.illegaller.ratabb.hishoot2i.view.common.IVew;

import android.graphics.Bitmap;

public interface MainFragmentView extends IVew {
    void showProgress();

    void hideProgress();

    void setImagePreview(Bitmap bitmap);

    void showMessage(String message);
}
