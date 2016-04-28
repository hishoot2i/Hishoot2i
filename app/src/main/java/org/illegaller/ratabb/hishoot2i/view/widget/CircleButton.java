package org.illegaller.ratabb.hishoot2i.view.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;

public class CircleButton extends FloatingActionButton {
  public CircleButton(Context context) {
    this(context, null);
  }

  public CircleButton(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public CircleButton(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public void setColor(int color) {
    setBackgroundTintList(ColorStateList.valueOf(color));
  }
}
