package org.illegaller.ratabb.hishoot2i.view.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import org.illegaller.ratabb.hishoot2i.utils.BitmapUtils;

public class BadgeDrawable extends Drawable {

  private final Bitmap mBitmap;
  private final int mWidth;
  private final int mHeight;
  private final Paint mPaint;

  public BadgeDrawable(Context context, String badgeText, @ColorInt int badgeColor, int badgeSize) {
    mBitmap = BitmapUtils.bitmapBadge(context, badgeText, badgeColor, badgeSize);
    mWidth = mBitmap.getWidth();
    mHeight = mBitmap.getHeight();
    mPaint = new Paint();
  }

  @Override public int getIntrinsicWidth() {
    return mWidth;
  }

  @Override public int getIntrinsicHeight() {
    return mHeight;
  }

  @Override public void draw(Canvas canvas) {
    canvas.drawBitmap(mBitmap, getBounds().left, getBounds().top, mPaint);
  }

  @Override public void setAlpha(int i) { /*no-op*/ }

  @Override public void setColorFilter(ColorFilter colorFilter) {
    mPaint.setColorFilter(colorFilter);
  }

  @Override public int getOpacity() {
    return 0;
  }
}
