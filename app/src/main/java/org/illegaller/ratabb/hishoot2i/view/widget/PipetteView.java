package org.illegaller.ratabb.hishoot2i.view.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;
import org.greenrobot.eventbus.EventBus;
import org.illegaller.ratabb.hishoot2i.events.EventPipette;

public class PipetteView extends FloatingActionButton implements View.OnClickListener {
  /*avoid color transparent for consistency*/
  public static final int CANCEL = Color.TRANSPARENT;
  private boolean isOpen = false;
  private int color = Color.BLACK;

  public PipetteView(Context context) {
    this(context, null);
  }

  public PipetteView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public PipetteView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    setVisibility(GONE);
    setOnClickListener(this);
  }

  @Override public void onClick(View view) {
    close(false);
  }

  int getColor() {
    return color;
  }

  public void setColor(int color) {
    this.color = color;
    setBackgroundTintList(ColorStateList.valueOf(color));
  }

  public boolean isOpen() {
    return isOpen;
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
    isOpen = true;
  }

  public void close(boolean isCancel) {
    if (!isOpen()) return;
    hide();
    isOpen = false;
    EventBus.getDefault().post(new EventPipette(false, isCancel ? CANCEL : getColor()));
  }
}
