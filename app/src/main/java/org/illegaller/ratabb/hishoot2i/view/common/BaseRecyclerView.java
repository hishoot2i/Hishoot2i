package org.illegaller.ratabb.hishoot2i.view.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.animation.OvershootInterpolator;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import org.illegaller.ratabb.hishoot2i.di.compenent.ActivityComponent;
import rx.Subscription;

public abstract class BaseRecyclerView extends RecyclerView {
  private Subscription mSubscription;

  ///////////////////////// CONSTRUCTION ///////////////////////////////////
  public BaseRecyclerView(Context context) {
    this(context, null);
  }

  public BaseRecyclerView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public BaseRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    injectComponent(((BaseActivity) getContext()).getActivityComponent());
  }

  ///////////////////////// ABSTRACTION ///////////////////////////////////
  protected abstract void injectComponent(ActivityComponent activityComponent);

  @NonNull protected abstract LayoutManager layoutManager();

  @NonNull protected abstract Subscription subscription();

  @NonNull protected abstract Adapter adapter();

  ///////////////////////// LIFECYCLE ///////////////////////////////////
  @Override protected void onFinishInflate() {
    super.onFinishInflate();
    setLayoutManager(layoutManager());
    setHasFixedSize(true);
    setItemAnimator(new FadeInAnimator(new OvershootInterpolator(1f)));
    setAdapter(new AlphaInAnimationAdapter(adapter()));
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    mSubscription = subscription();
  }

  @Override protected void onDetachedFromWindow() {
    if (mSubscription != null) {
      mSubscription.unsubscribe();
      mSubscription = null;
    }
    super.onDetachedFromWindow();
  }
}
