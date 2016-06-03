package org.illegaller.ratabb.hishoot2i.view.common;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.view.View;
import jp.wasabeef.recyclerview.animators.holder.AnimateViewHolder;
import org.illegaller.ratabb.hishoot2i.utils.Utils;

public abstract class BaseAnimateViewHolder extends AnimateViewHolder {
  static final long sDURATION = 300L;
  static final float sHALF_HEIGHT = 0.3f;

  public BaseAnimateViewHolder(View itemView) {
    super(itemView);
  }

  protected Context getContext() {
    Utils.checkNotNull(itemView, "itemView == null");
    return itemView.getContext();
  }

  @Override public void preAnimateRemoveImpl() {
    ViewCompat.setTranslationY(itemView, 1);
    ViewCompat.setAlpha(itemView, 1);
  }

  @Override public void preAnimateAddImpl() {
    ViewCompat.setTranslationY(itemView, -itemView.getHeight() * sHALF_HEIGHT);
    ViewCompat.setAlpha(itemView, 0);
  }

  @Override public void animateRemoveImpl(ViewPropertyAnimatorListener listener) {
    ViewCompat.animate(itemView)
        .translationY(-itemView.getHeight() * sHALF_HEIGHT)
        .alpha(0)
        .setDuration(sDURATION)
        .setListener(listener)
        .start();
  }

  @Override public void animateAddImpl(ViewPropertyAnimatorListener listener) {
    ViewCompat.animate(itemView)
        .translationY(0)
        .alpha(1)
        .setDuration(sDURATION)
        .setListener(listener)
        .start();
  }
}
