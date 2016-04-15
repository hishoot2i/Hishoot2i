/*
 * Copyright (C) 2010 Daniel Nilsson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.illegaller.ratabb.hishoot2i.view.widget;


import org.illegaller.ratabb.hishoot2i.utils.BitmapUtils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;


public class AlphaPatternDrawable extends Drawable {

    private int mRectangleSize = 10;
    private Paint mPaint = new Paint();


    private Bitmap mBitmap;

    public AlphaPatternDrawable(int rectangleSize) {
        mRectangleSize = rectangleSize;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, null, getBounds(), mPaint);
    }

    @Override
    public int getOpacity() {
        return 0;
    }

    @Override
    public void setAlpha(int alpha) {
        throw new UnsupportedOperationException("Alpha is not supported by this drawable.");
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        throw new UnsupportedOperationException("ColorFilter is not supported by this drawable.");
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        generatePatternBitmap();
    }

    private void generatePatternBitmap() {
        if (getBounds().width() <= 0 || getBounds().height() <= 0) return;
        mBitmap = BitmapUtils.alphaPatternBitmap(mRectangleSize, getBounds().width(), getBounds().height());
    }
}
