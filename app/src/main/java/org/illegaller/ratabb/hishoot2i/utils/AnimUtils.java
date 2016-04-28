package org.illegaller.ratabb.hishoot2i.utils;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.support.v4.view.ViewCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class AnimUtils {
  private static final long sANIM_DURATION = 300;
  private static final AccelerateInterpolator sINTERPOLATOR = new AccelerateInterpolator(1.0f);

  private AnimUtils() { /*no instance*/ }

  public static void height(final View view, int from, int to) {
    AnimUtils.height(view, from, to, sANIM_DURATION);
  }

  public static void height(final View view, int from, int to, long duration) {
    ValueAnimator animator = ValueAnimator.ofFloat(from, to);
    animator.setDuration(duration);
    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override public void onAnimationUpdate(ValueAnimator valueAnimator) {
        float animatedValue = (float) valueAnimator.getAnimatedValue();
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) animatedValue;
        view.setLayoutParams(layoutParams);
        view.requestLayout();
      }
    });
    animator.start();
  }

  public static void translateY(final View view, int from, int to) {
    AnimUtils.translateY(view, from, to, sANIM_DURATION);
  }

  public static void translateY(final View view, int from, int to, long duration) {
    ValueAnimator animator = ValueAnimator.ofFloat(from, to);
    animator.setDuration(duration);
    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override public void onAnimationUpdate(ValueAnimator valueAnimator) {
        float value = (float) valueAnimator.getAnimatedValue();
        view.setTranslationY(value);
        view.requestLayout();
      }
    });
    animator.start();
  }

  public static Animation animTranslateY(float fromY, float toY) {
    TranslateAnimation ta =
        new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, fromY, Animation.RELATIVE_TO_PARENT, toY);
    ta.setDuration(sANIM_DURATION);
    ta.setInterpolator(sINTERPOLATOR);
    return ta;
  }

  public static void fadeIn(View view) {
    fadeIn(view, sANIM_DURATION);
  }

  public static void fadeIn(View view, long duration) {
    view.setVisibility(View.VISIBLE);
    view.setAlpha(0f);
    ViewCompat.animate(view).alpha(1f).setDuration(duration).setInterpolator(sINTERPOLATOR);
  }

  public static void fadeOut(View view) {
    fadeOut(view, sANIM_DURATION);
  }

  public static void fadeOut(View view, long duration) {
    view.setVisibility(View.VISIBLE);
    view.setAlpha(1f);
    ViewCompat.animate(view).alpha(0f).setDuration(duration).setInterpolator(sINTERPOLATOR);
  }

  @TargetApi(21) public static void reveal(final View view) {
    int cx = view.getWidth() - (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24,
        view.getResources().getDisplayMetrics());
    int cy = view.getHeight() / 2;
    int finalRadius = Math.max(view.getWidth(), view.getHeight());
    Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
    view.setVisibility(View.VISIBLE);
    anim.start();
  }
}
