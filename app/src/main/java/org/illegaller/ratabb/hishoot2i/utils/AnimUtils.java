package org.illegaller.ratabb.hishoot2i.utils;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;

public class AnimUtils {
    private static final long sANIM_DURATION = 300;

    private AnimUtils() {        //no instance
    }

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
}
