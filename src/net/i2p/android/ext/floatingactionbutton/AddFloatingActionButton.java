/*
    Copyright (C) 2014 str4d
    Copyright (C) 2014 Jerzy Chalupski

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */
package net.i2p.android.ext.floatingactionbutton;

import org.illegaller.ratabb.hishoot2i.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;

public class AddFloatingActionButton extends FloatingActionButton {
	int mPlusColor;

	public AddFloatingActionButton(Context context) {
		this(context, null);
	}

	public AddFloatingActionButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AddFloatingActionButton(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	void init(Context context, AttributeSet attributeSet) {
		TypedArray attr = context.obtainStyledAttributes(attributeSet,
				R.styleable.AddFloatingActionButton, 0, 0);
		mPlusColor = attr.getColor(
				R.styleable.AddFloatingActionButton_fab_plusIconColor,
				getColor(android.R.color.white));
		attr.recycle();

		super.init(context, attributeSet);
	}

	/**
	 * @return the current Color of plus icon.
	 */
	public int getPlusColor() {
		return mPlusColor;
	}

	public void setPlusColorResId(@ColorRes int plusColor) {
		setPlusColor(getColor(plusColor));
	}

	public void setPlusColor(int color) {
		if (mPlusColor != color) {
			mPlusColor = color;
			updateBackground();
		}
	}

	@Override
	public void setIcon(@DrawableRes int icon) {
		throw new UnsupportedOperationException(
				"Use FloatingActionButton if you want to use custom icon");
	}

	@Override
	Drawable getIconDrawable() {
		final float iconSize = getDimension(R.dimen.fab_icon_size);
		final float iconHalfSize = iconSize / 2f;

		final float plusSize = getDimension(R.dimen.fab_plus_icon_size);
		final float plusHalfStroke = getDimension(R.dimen.fab_plus_icon_stroke) / 2f;
		final float plusOffset = (iconSize - plusSize) / 2f;

		final Shape shape = new Shape() {
			@Override
			public void draw(Canvas canvas, Paint paint) {
				canvas.drawRect(plusOffset, iconHalfSize - plusHalfStroke,
						iconSize - plusOffset, iconHalfSize + plusHalfStroke,
						paint);
				canvas.drawRect(iconHalfSize - plusHalfStroke, plusOffset,
						iconHalfSize + plusHalfStroke, iconSize - plusOffset,
						paint);
			}
		};

		ShapeDrawable drawable = new ShapeDrawable(shape);

		final Paint paint = drawable.getPaint();
		paint.setColor(mPlusColor);
		paint.setStyle(Style.FILL);
		paint.setAntiAlias(true);

		return drawable;
	}
}
