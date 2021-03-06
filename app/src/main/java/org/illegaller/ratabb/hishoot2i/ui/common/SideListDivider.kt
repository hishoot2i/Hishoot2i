package org.illegaller.ratabb.hishoot2i.ui.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.graphics.component1
import androidx.core.graphics.component2
import androidx.core.graphics.component3
import androidx.core.graphics.component4
import androidx.core.graphics.toRectF
import androidx.core.view.forEach
import androidx.recyclerview.widget.RecyclerView
import common.content.dp2px
import common.graphics.themeColorOrDefault
import org.illegaller.ratabb.hishoot2i.R
import kotlin.math.roundToInt
import com.google.android.material.R as MaterialR

class SideListDivider private constructor(
    @ColorInt colorDivider: Int,
    @ColorInt colorBack: Int,
    @Px private val sizeDivider: Float,
    @Px private val space: Float
) : RecyclerView.ItemDecoration() {

    companion object {
        @JvmStatic
        fun addItemDecorToRecyclerView(view: RecyclerView) {
            view.addItemDecoration(SideListDivider(view.context))
        }
    }

    constructor(context: Context) : this(
        colorDivider = context.themeColorOrDefault(MaterialR.attr.colorOnSurface, Color.LTGRAY),
        colorBack = context.themeColorOrDefault(R.attr.colorSurfaceHalfAlpha, Color.DKGRAY),
        sizeDivider = context.dp2px(0.5f),
        space = context.dp2px(4.0f)
    )

    private val paintLine: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = colorDivider
        strokeWidth = sizeDivider
    }

    private val paintBack = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = colorBack
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.apply {
            left = space.roundToInt()
            top = space.roundToInt()
            bottom = (sizeDivider + space).roundToInt()
            right = (sizeDivider + space).roundToInt()
        }
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        parent.forEach { child: View ->
            if (parent.getChildAdapterPosition(child) < state.itemCount) {
                canvas.drawRect(child.getRelativePosition(), paintBack)
            }
        }
    }

    override fun onDrawOver(
        canvas: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        parent.forEach { child: View ->
            if (parent.getChildAdapterPosition(child) < state.itemCount) {
                val (l, t, r, b) = child.getRelativePosition()
                canvas.drawLines(
                    floatArrayOf(
                        /* bottom side*/
                        l, b, r, b,
                        /* right side */
                        r, t, r, b
                    ),
                    paintLine
                )
            }
        }
    }

    private fun View.getRelativePosition(): RectF = Rect(left, top, right, bottom).toRectF()
}
