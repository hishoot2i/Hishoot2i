package org.illegaller.ratabb.hishoot2i.view.fragment.colorpick;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import org.illegaller.ratabb.hishoot2i.utils.Utils;

public class ColorPickerDialogBuilder {
  private static ColorPickerDialogBuilder mInstance;
  private @ColorInt int mColor = Color.CYAN;
  private ColorChangeListener mListener = null;

  private ColorPickerDialogBuilder() {    //no instance
  }

  public static ColorPickerDialogBuilder create() {
    if (mInstance == null) mInstance = InstanceHolder.sINSTANCE;
    return mInstance;
  }

  public ColorPickerDialogBuilder colorInit(@ColorInt int color) {
    this.mColor = color;
    return this;
  }

  public ColorPickerDialogBuilder listener(ColorChangeListener listener) {
    this.mListener = listener;
    return this;
  }

  public ColorPickerDialog build() {
    Utils.checkNotNull(mListener, "listener(:ColorChangeListener)");
    ColorPickerDialog dialog = ColorPickerDialog.newInstance(mColor);
    dialog.setListener(mListener);
    return dialog;
  }

  static final class InstanceHolder {
    static final ColorPickerDialogBuilder sINSTANCE = new ColorPickerDialogBuilder();

    private InstanceHolder() {
      throw new UnsupportedOperationException("no instance");
    }
  }
}
