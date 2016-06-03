package org.illegaller.ratabb.hishoot2i.view.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import org.greenrobot.eventbus.EventBus;
import org.illegaller.ratabb.hishoot2i.events.EventPipette;

public class PipetteView extends FloatingActionButton {
  public static final int CANCEL = Color.TRANSPARENT; /*avoid color transparent for consistency*/
  private boolean mIsOpen = false;
  private int mColor = Color.BLACK;

  public PipetteView(Context context) {
    this(context, null);
  }

  public PipetteView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public PipetteView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();
    setVisibility(GONE);
    setOnClickListener(view -> close(false));
  }

  int getColor() {
    return mColor;
  }

  public void setColor(int color) {
    this.mColor = color;
    setBackgroundTintList(ColorStateList.valueOf(color));
  }

  public boolean isOpen() {
    return mIsOpen;
  }

  public void open() {
    open(true);
  }

  public void open(boolean animate) {
    if (isOpen()) return;
    if (animate) {
      show();
    } else {
      setVisibility(VISIBLE);
    }
    mIsOpen = true;
  }

  public void close(boolean isCancel) {
    if (!isOpen()) return;
    hide();
    mIsOpen = false;
    EventBus.getDefault().post(new EventPipette(false, isCancel ? CANCEL : getColor()));
  }
}
