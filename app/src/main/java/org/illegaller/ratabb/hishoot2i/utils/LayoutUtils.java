package org.illegaller.ratabb.hishoot2i.utils;

import android.view.View;
import android.view.ViewGroup;

public class LayoutUtils {
    private LayoutUtils() {        //no instance
    }

    public static void layoutView(View view, int left, int top, int width, int height) {
        ViewGroup.MarginLayoutParams margins = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        final int leftWithMargins = left + margins.leftMargin;
        final int topWithMargins = top + margins.topMargin;
        view.layout(leftWithMargins, topWithMargins,
                leftWithMargins + width, topWithMargins + height);
    }

    public static int getWidthWithMargins(View child) {
        final ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
        return child.getWidth() + lp.leftMargin + lp.rightMargin;
    }

    public static int getHeightWithMargins(View child) {
        final ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
        return child.getHeight() + lp.topMargin + lp.bottomMargin;
    }

    public static int getMeasuredWidthWithMargins(View child) {
        final ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
        return child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
    }

    public static int getMeasuredHeightWithMargins(View child) {
        final ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
        return child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
    }
}
