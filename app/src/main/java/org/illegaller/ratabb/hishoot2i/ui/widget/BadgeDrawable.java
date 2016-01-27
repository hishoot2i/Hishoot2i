package org.illegaller.ratabb.hishoot2i.ui.widget;

import org.illegaller.ratabb.hishoot2i.utils.BitmapUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;

public class BadgeDrawable extends Drawable {

    private final Bitmap bitmap;
    private final int width;
    private final int height;
    private final Paint paint;

    public BadgeDrawable(Context context, String badgeText, @ColorInt int badgeColor, int badgeSize) {
        bitmap = BitmapUtils.bitmapBadge(context, badgeText, badgeColor, badgeSize);
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        paint = new Paint();
    }

    @Override public int getIntrinsicWidth() {
        return width;
    }

    @Override public int getIntrinsicHeight() {
        return height;
    }

    @Override public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, getBounds().left, getBounds().top, paint);
    }

    @Override public void setAlpha(int i) {        //no-op
    }

    @Override public void setColorFilter(ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override public int getOpacity() {
        return 0;
    }
}
