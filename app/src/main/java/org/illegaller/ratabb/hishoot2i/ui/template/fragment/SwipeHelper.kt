package org.illegaller.ratabb.hishoot2i.ui.template.fragment

import android.graphics.Canvas
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.v7.widget.helper.ItemTouchHelper.LEFT
import android.support.v7.widget.helper.ItemTouchHelper.RIGHT

class SwipeHelper(private val callback: Listener) :
    ItemTouchHelper.SimpleCallback(0, LEFT or RIGHT) {
    interface Listener {
        fun onSwipedToLeft(position: Int)
        fun onSwipedToRight(position: Int)
    }

    override fun onMove(
        recyclerView: RecyclerView?,
        viewHolder: ViewHolder?,
        target: ViewHolder?
    ): Boolean = false

    override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        when (direction) {
            LEFT -> callback.onSwipedToLeft(position)
            RIGHT -> callback.onSwipedToRight(position)
        }
    }

    override fun getSwipeDirs(recyclerView: RecyclerView?, holder: ViewHolder?): Int {
        return if (holder is TemplateAdapter.TemplateHolder) {
            if (holder.isItemDeletable()) {
                LEFT or RIGHT
            } else RIGHT
        } else super.getSwipeDirs(recyclerView, holder)
    }

    override fun clearView(rv: RecyclerView?, holder: ViewHolder?) {
        if (holder is TemplateAdapter.TemplateHolder) {
            getDefaultUIUtil().clearView(holder.foregroundView)
        } else super.clearView(rv, holder)
    }

    override fun onSelectedChanged(viewHolder: ViewHolder?, actionState: Int) {
        if (viewHolder is TemplateAdapter.TemplateHolder) {
            getDefaultUIUtil().onSelected(viewHolder.foregroundView)
        } else super.onSelectedChanged(viewHolder, actionState)
    }

    override fun onChildDraw(
        c: Canvas?,
        rv: RecyclerView?,
        holder: ViewHolder?,
        dX: Float,
        dY: Float,
        state: Int,
        active: Boolean
    ) {
        if (holder is TemplateAdapter.TemplateHolder) {
            holder.handleIndicatorVisible(dX, active)
            getDefaultUIUtil().onDraw(c, rv, holder.foregroundView, dX, dY, state, active)
        } else super.onChildDraw(c, rv, holder, dX, dY, state, active)
    }

    override fun onChildDrawOver(
        c: Canvas?,
        rv: RecyclerView?,
        holder: ViewHolder?,
        dX: Float,
        dY: Float,
        state: Int,
        active: Boolean
    ) {
        if (holder is TemplateAdapter.TemplateHolder) {
            getDefaultUIUtil().onDrawOver(c, rv, holder.foregroundView, dX, dY, state, active)
        } else super.onChildDrawOver(c, rv, holder, dX, dY, state, active)
    }
}