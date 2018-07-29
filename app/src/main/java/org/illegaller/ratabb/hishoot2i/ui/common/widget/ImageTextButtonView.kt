/*
package org.illegaller.ratabb.hishoot2i.ui.common.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import org.illegaller.ratabb.hishoot2i.R

class ImageTextButtonView(
    context: Context,
    attributeSet: AttributeSet
) : View(context, attributeSet) {
    private var text: String? = null
    private var iconColor: Int = Color.WHITE
    private var icon: Drawable? = null
    //
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        val ta: TypedArray = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.ImageTextButtonView,
            0,
            0
        )
        ////// Icon
        if (ta.hasValue(R.styleable.ImageTextButtonView_iconColor)) {
            iconColor = ta.getColor(R.styleable.ImageTextButtonView_iconColor, iconColor)
        }
        if (ta.hasValue(R.styleable.ImageTextButtonView_android_src)) {
            icon = ta.getDrawable(R.styleable.ImageTextButtonView_android_src)
        }
        ////// Text
        if (ta.hasValue(R.styleable.ImageTextButtonView_android_text)) {
            text = ta.getString(R.styleable.ImageTextButtonView_android_text)
        }
        if (ta.hasValue(R.styleable.ImageTextButtonView_android_textAppearance)){

        }
        ta.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // TODO
    }

    override fun onDraw(canvas: Canvas?) {
        // TODO
        canvas?.apply {
            icon?.draw(this)
            drawText(text, paddingLeft.toFloat(), paddingTop.toFloat(), textPaint)
        }
    }
}*/
