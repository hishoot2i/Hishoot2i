package org.illegaller.ratabb.hishoot2i.view.fragment.templateview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import javax.inject.Inject;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import org.illegaller.ratabb.hishoot2i.view.common.BaseActivity;

public class TemplateRecyclerView extends RecyclerView {
  static final OnScrollListener mON_SCROLL_LISTENER = new OnScrollListener() {
    @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
      if (newState != RecyclerView.SCROLL_STATE_DRAGGING) return;
      for (int i = 0, count = recyclerView.getChildCount(); i < count; i++) {
        View childAt = recyclerView.getChildAt(i);
        TemplateViewHolder viewHolder =
            (TemplateViewHolder) recyclerView.getChildViewHolder(childAt);
        if (viewHolder.isOpen()) viewHolder.swipeRevealLayout.close(true);
      }
    }
  };
  @Inject TemplateAdapter mTemplateAdapter;

  public TemplateRecyclerView(Context context) {
    this(context, null);
  }

  public TemplateRecyclerView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public TemplateRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    ((BaseActivity) getContext()).getActivityComponent().inject(this);
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();
    setOrientation();
    setHasFixedSize(true);
    setItemAnimator(new FadeInAnimator(new OvershootInterpolator(1f)));
    setAdapter(new AlphaInAnimationAdapter(mTemplateAdapter));
    addOnScrollListener(mON_SCROLL_LISTENER);
  }

  @Override protected void onDetachedFromWindow() {
    removeOnScrollListener(mON_SCROLL_LISTENER);
    super.onDetachedFromWindow();
  }

  private void setOrientation() {
    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
    layoutManager.setOrientation(VERTICAL);
    setLayoutManager(layoutManager);
  }
}
