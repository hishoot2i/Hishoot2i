package org.illegaller.ratabb.hishoot2i.view.common;

import android.content.Context;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.view.View;
import jp.wasabeef.recyclerview.animators.holder.AnimateViewHolder;

import static android.support.v4.view.ViewCompat.animate;
import static android.support.v4.view.ViewCompat.setAlpha;
import static android.support.v4.view.ViewCompat.setTranslationY;
import static org.illegaller.ratabb.hishoot2i.utils.Utils.checkNotNull;

public abstract class BaseAnimateViewHolder<T> extends AnimateViewHolder {
  private static final long sDURATION = 300L;
  private static final float sHALF_HEIGHT = 0.3f;

  public BaseAnimateViewHolder(View itemView) {
    super(itemView);
  }

  protected Context getContext() {
    return checkNotNull(itemView, "itemView == null").getContext();
  }

  protected abstract void onBind(T model);

  @Override public void preAnimateRemoveImpl() {
    setTranslationY(itemView, 1);
    setAlpha(itemView, 1);
  }

  @Override public void preAnimateAddImpl() {
    setTranslationY(itemView, -itemView.getHeight() * sHALF_HEIGHT);
    setAlpha(itemView, 0);
  }

  @Override public void animateRemoveImpl(ViewPropertyAnimatorListener listener) {
    animate(itemView).translationY(-itemView.getHeight() * sHALF_HEIGHT)
        .alpha(0)
        .setDuration(sDURATION)
        .setListener(listener)
        .start();
  }

  @Override public void animateAddImpl(ViewPropertyAnimatorListener listener) {
    animate(itemView).translationY(0).alpha(1).setDuration(sDURATION).setListener(listener).start();
  }
}
