package org.illegaller.ratabb.hishoot2i.ui.common.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.ClassLoaderCreator
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SoundEffectConstants
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.component1
import androidx.core.graphics.component2
import androidx.core.graphics.component3
import androidx.core.graphics.component4
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.minus
import androidx.customview.view.AbsSavedState
import common.content.displayMetrics
import org.illegaller.ratabb.hishoot2i.R

class CropImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = R.attr.cropImageViewStyle
) : AppCompatImageView(context, attrs, defStyle) {
    private var mViewWidth = 0
    private var mViewHeight = 0
    private var mScale = 1.0F
    private var mImgWidth = 0.0F
    private var mImgHeight = 0.0F
    private var mIsInitialized = false
    private var mLastX = 0F
    private var mLastY = 0F
    private var mTouchArea = TouchArea.OUT_OF_BOUNDS

    private val mMatrix = Matrix()
    private val density = context.displayMetrics.density
    private val mPaint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)
    private val mPaintBitmap = Paint(Paint.FILTER_BITMAP_FLAG)
    private val mFrmRect = RectF()
    private val mImgRect = RectF()
    private val mCenter = PointF()

    private var mCropMode: CropMode = CropMode.RATIO_1_1
    private var mGuideShowMode: ShowMode = ShowMode.SHOW_ALWAYS
    private var mHandleShowMode: ShowMode = ShowMode.SHOW_ALWAYS
    private var mMinFrameSize = density * MIN_FRAME_SIZE_IN_DP
    private var mHandleSize = density * HANDLE_SIZE_IN_DP
    private var mTouchPadding = 0F
    private var mShowGuide = true
    private var mShowHandle = true
    private var mCustomRatio = PointF(1.0F, 1.0F)
    private var mFrameStrokeWeight = density * FRAME_STROKE_WEIGHT_IN_DP
    private var mGuideStrokeWeight = density * GUIDE_STROKE_WEIGHT_IN_DP
    private var mBackgroundColor = TRANSPARENT
    private var mOverlayColor = TRANSLUCENT_BLACK
    private var mFrameColor = WHITE
    private var mHandleColor = WHITE
    private var mGuideColor = TRANSLUCENT_WHITE

    private var imageBitmap: Bitmap? = null

    init {
        context.withStyledAttributes(
            set = attrs,
            attrs = R.styleable.CropImageView,
            defStyleAttr = defStyle,
            defStyleRes = R.style.AppWidget_CropImageView
        ) {
            setImageDrawable(getDrawable(R.styleable.CropImageView_imgSrc))
            mCropMode = CropMode.values().getOrElse(
                getInt(R.styleable.CropImageView_cropMode, CropMode.RATIO_1_1.id)
            ) { CropMode.RATIO_1_1 }
            mBackgroundColor = getColor(R.styleable.CropImageView_backgroundColor, TRANSPARENT)
            super.setBackgroundColor(mBackgroundColor) //
            mOverlayColor = getColor(R.styleable.CropImageView_overlayColor, TRANSLUCENT_BLACK)
            mFrameColor = getColor(R.styleable.CropImageView_frameColor, WHITE)
            mHandleColor = getColor(R.styleable.CropImageView_handleColor, WHITE)
            mGuideColor = getColor(R.styleable.CropImageView_guideColor, TRANSLUCENT_WHITE)
            mGuideShowMode = ShowMode.values().getOrElse(
                getInt(R.styleable.CropImageView_guideShowMode, ShowMode.SHOW_ALWAYS.id)
            ) { ShowMode.SHOW_ALWAYS }
            setGuideShowMode(mGuideShowMode)
            mHandleShowMode = ShowMode.values().getOrElse(
                getInt(R.styleable.CropImageView_handleShowMode, ShowMode.SHOW_ALWAYS.id)
            ) { ShowMode.SHOW_ALWAYS }
            setHandleShowMode(mHandleShowMode)
            mHandleSize = getDimensionPixelSize(
                R.styleable.CropImageView_handleSize,
                (HANDLE_SIZE_IN_DP * density).toInt()
            ).toFloat()
            mTouchPadding = getDimensionPixelSize(R.styleable.CropImageView_touchPadding, 0)
                .toFloat()
            mMinFrameSize = getDimensionPixelSize(
                R.styleable.CropImageView_minFrameSize,
                (MIN_FRAME_SIZE_IN_DP * density).toInt()
            ).toFloat()
            mFrameStrokeWeight = getDimensionPixelSize(
                R.styleable.CropImageView_frameStrokeWeight,
                (FRAME_STROKE_WEIGHT_IN_DP * density).toInt()
            ).toFloat()
            mGuideStrokeWeight = getDimensionPixelSize(
                R.styleable.CropImageView_guideStrokeWeight,
                (GUIDE_STROKE_WEIGHT_IN_DP * density).toInt()
            ).toFloat()
        }
    }

    public override fun onSaveInstanceState(): Parcelable? =
        when (val superState = super.onSaveInstanceState()) {
            null -> null // superState
            else -> SavedState(superState).apply {
                image = imageBitmap
                mode = mCropMode
                backgroundColor = mBackgroundColor
                overlayColor = mOverlayColor
                frameColor = mFrameColor
                guideShowMode = mGuideShowMode
                handleShowMode = mHandleShowMode
                showGuide = mShowGuide
                showHandle = mShowHandle
                handleSize = mHandleSize
                touchPadding = mTouchPadding
                minFrameSize = mMinFrameSize
                customRatio = mCustomRatio
                frameStrokeWeight = mFrameStrokeWeight
                guideStrokeWeight = mGuideStrokeWeight
                handleColor = mHandleColor
                guideColor = mGuideColor
            }
        }

    public override fun onRestoreInstanceState(state: Parcelable) {
        when (state) {
            is SavedState -> {
                super.onRestoreInstanceState(state.superState)
                mCropMode = state.mode
                mBackgroundColor = state.backgroundColor
                mOverlayColor = state.overlayColor
                mFrameColor = state.frameColor
                mGuideShowMode = state.guideShowMode
                mHandleShowMode = state.handleShowMode
                mShowGuide = state.showGuide
                mShowHandle = state.showHandle
                mHandleSize = state.handleSize
                mTouchPadding = state.touchPadding
                mMinFrameSize = state.minFrameSize
                mCustomRatio = state.customRatio
                mFrameStrokeWeight = state.frameStrokeWeight
                mGuideStrokeWeight = state.guideStrokeWeight
                mHandleColor = state.handleColor
                mGuideColor = state.guideColor
                setImageBitmap(state.image)
                requestLayout()
            }
            else -> super.onRestoreInstanceState(state)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        val viewHeight = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(viewWidth, viewHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        mViewWidth = r - l - paddingLeft - paddingRight
        mViewHeight = b - t - paddingTop - paddingBottom
        if (imageBitmap != null) initLayout(mViewWidth, mViewHeight)
    }

    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mIsInitialized) {
            setMatrix()
            imageBitmap?.let { canvas.drawBitmap(it, mMatrix, mPaintBitmap) }
            drawEditFrame(canvas)
        }
    }

    private fun drawEditFrame(canvas: Canvas) {
        // region Draw Overlay
        mPaint.apply {
            color = mOverlayColor
            style = Paint.Style.FILL
        }
        canvas.drawRect(mImgRect.left, mImgRect.top, mImgRect.right, mFrmRect.top, mPaint)
        canvas.drawRect(mImgRect.left, mFrmRect.bottom, mImgRect.right, mImgRect.bottom, mPaint)
        canvas.drawRect(mImgRect.left, mFrmRect.top, mFrmRect.left, mFrmRect.bottom, mPaint)
        canvas.drawRect(mFrmRect.right, mFrmRect.top, mImgRect.right, mFrmRect.bottom, mPaint)
        // endregion
        // region Draw Frame
        mPaint.apply {
            style = Paint.Style.STROKE
            color = mFrameColor
            strokeWidth = mFrameStrokeWeight
        }
        canvas.drawRect(mFrmRect.left, mFrmRect.top, mFrmRect.right, mFrmRect.bottom, mPaint)
        // endregion
        // region Draw Guide
        if (mShowGuide) {
            mPaint.apply {
                color = mGuideColor
                strokeWidth = mGuideStrokeWeight
            }
            val h1 = mFrmRect.left + (mFrmRect.right - mFrmRect.left) / 3.0f
            val h2 = mFrmRect.right - (mFrmRect.right - mFrmRect.left) / 3.0f
            val v1 = mFrmRect.top + (mFrmRect.bottom - mFrmRect.top) / 3.0f
            val v2 = mFrmRect.bottom - (mFrmRect.bottom - mFrmRect.top) / 3.0f
            canvas.drawLine(h1, mFrmRect.top, h1, mFrmRect.bottom, mPaint)
            canvas.drawLine(h2, mFrmRect.top, h2, mFrmRect.bottom, mPaint)
            canvas.drawLine(mFrmRect.left, v1, mFrmRect.right, v1, mPaint)
            canvas.drawLine(mFrmRect.left, v2, mFrmRect.right, v2, mPaint)
        }
        // endregion
        // region Draw Handle
        if (mShowHandle) {
            mPaint.apply {
                style = Paint.Style.FILL
                color = mHandleColor
            }
            canvas.drawCircle(mFrmRect.left, mFrmRect.top, mHandleSize, mPaint)
            canvas.drawCircle(mFrmRect.right, mFrmRect.top, mHandleSize, mPaint)
            canvas.drawCircle(mFrmRect.left, mFrmRect.bottom, mHandleSize, mPaint)
            canvas.drawCircle(mFrmRect.right, mFrmRect.bottom, mHandleSize, mPaint)
        }
        //endregion
    }

    private fun setMatrix() {
        mMatrix.reset()
        mMatrix.setTranslate(mCenter.x - mImgWidth * 0.5F, mCenter.y - mImgHeight * 0.5F)
        mMatrix.postScale(mScale, mScale, mCenter.x, mCenter.y)
    }

    private fun initLayout(viewW: Int, viewH: Int) {
        val imgW = imageBitmap!!.width.toFloat()
        val imgH = imageBitmap!!.height.toFloat()
        mImgWidth = imgW
        mImgHeight = imgH
        val w = viewW.toFloat()
        val h = viewH.toFloat()
        val viewRatio = w / h
        val imgRatio = imgW / imgH
        val scale = when {
            imgRatio >= viewRatio -> w / imgW
            imgRatio < viewRatio -> h / imgH
            else -> 1.0F
        }
        mCenter.set(paddingLeft + w * 0.5F, paddingTop + h * 0.5F)
        mScale = scale
        initCropFrame()
        adjustRatio()
        mIsInitialized = true
    }

    private fun initCropFrame() {
        setMatrix()
        val pts = floatArrayOf(0.0F, 0.0F, 0.0F, mImgHeight, mImgWidth, 0.0F, mImgWidth, mImgHeight)
        mMatrix.mapPoints(pts)
        val l = pts[0]
        val t = pts[1]
        val r = pts[6]
        val b = pts[7]
        mFrmRect.set(l, t, r, b)
        mImgRect.set(l, t, r, b)
    }

    override fun performClick(): Boolean {
        val handle = super.performClick()
        if (!handle) playSoundEffect(SoundEffectConstants.CLICK)
        return handle
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        performClick()
        if (!mIsInitialized) return false
        val (eX, eY) = event.run { x to y }
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                onDown(eX, eY)
                true
            }
            MotionEvent.ACTION_MOVE -> {
                onMove(eX, eY)
                if (mTouchArea != TouchArea.OUT_OF_BOUNDS) {
                    parent.requestDisallowInterceptTouchEvent(true)
                }
                true
            }
            MotionEvent.ACTION_CANCEL -> {
                parent.requestDisallowInterceptTouchEvent(false)
                onCancel()
                true
            }
            MotionEvent.ACTION_UP -> {
                parent.requestDisallowInterceptTouchEvent(false)
                onUp()
                true
            }
            else -> false
        }
    }

    private fun onDown(eX: Float, eY: Float) {
        invalidate()
        mLastX = eX
        mLastY = eY
        checkTouchArea(eX, eY)
    }

    private fun onMove(eX: Float, eY: Float) {
        val diff = PointF(eX - mLastX, eY - mLastY)
        when (mTouchArea) {
            TouchArea.CENTER -> moveFrame(diff)
            TouchArea.LEFT_TOP -> moveHandleLT(diff)
            TouchArea.RIGHT_TOP -> moveHandleRT(diff)
            TouchArea.LEFT_BOTTOM -> moveHandleLB(diff)
            TouchArea.RIGHT_BOTTOM -> moveHandleRB(diff)
            TouchArea.OUT_OF_BOUNDS -> {
            }
        }
        invalidate()
        mLastX = eX
        mLastY = eY
    }

    private fun onUp() {
        if (mGuideShowMode == ShowMode.SHOW_ON_TOUCH) mShowGuide = false
        if (mHandleShowMode == ShowMode.SHOW_ON_TOUCH) mShowHandle = false
        mTouchArea = TouchArea.OUT_OF_BOUNDS
        invalidate()
    }

    private fun onCancel() {
        mTouchArea = TouchArea.OUT_OF_BOUNDS
        invalidate()
    }

    private fun checkTouchArea(x: Float, y: Float) {
        mTouchArea = when {
            isInsideCornerLeftTop(x, y) -> TouchArea.LEFT_TOP
            isInsideCornerRightTop(x, y) -> TouchArea.RIGHT_TOP
            isInsideCornerLeftBottom(x, y) -> TouchArea.LEFT_BOTTOM
            isInsideCornerRightBottom(x, y) -> TouchArea.RIGHT_BOTTOM
            isInsideFrame(x, y) -> TouchArea.CENTER
            else -> TouchArea.OUT_OF_BOUNDS
        }
        when (mTouchArea) {
            TouchArea.LEFT_TOP,
            TouchArea.RIGHT_TOP,
            TouchArea.LEFT_BOTTOM,
            TouchArea.RIGHT_BOTTOM -> {
                if (mHandleShowMode == ShowMode.SHOW_ON_TOUCH) mShowHandle = true
                if (mGuideShowMode == ShowMode.SHOW_ON_TOUCH) mShowGuide = true
            }
            TouchArea.CENTER -> {
                if (mGuideShowMode == ShowMode.SHOW_ON_TOUCH) mShowGuide = true
            }
            TouchArea.OUT_OF_BOUNDS -> {
            }
        }
    }

    private fun isInsideFrame(x: Float, y: Float): Boolean = mFrmRect.contains(x, y)

    private fun isInsideCornerLeftTop(x: Float, y: Float): Boolean {
        val dx = x - mFrmRect.left
        val dy = y - mFrmRect.top
        val d = dx * dx + dy * dy
        return sq(mHandleSize + mTouchPadding) >= d
    }

    private fun isInsideCornerRightTop(x: Float, y: Float): Boolean {
        val dx = x - mFrmRect.right
        val dy = y - mFrmRect.top
        val d = dx * dx + dy * dy
        return sq(mHandleSize + mTouchPadding) >= d
    }

    private fun isInsideCornerLeftBottom(x: Float, y: Float): Boolean {
        val dx = x - mFrmRect.left
        val dy = y - mFrmRect.bottom
        val d = dx * dx + dy * dy
        return sq(mHandleSize + mTouchPadding) >= d
    }

    private fun isInsideCornerRightBottom(x: Float, y: Float): Boolean {
        val dx = x - mFrmRect.right
        val dy = y - mFrmRect.bottom
        val d = dx * dx + dy * dy
        return sq(mHandleSize + mTouchPadding) >= d
    }

    private fun moveFrame(diff: PointF) {
        mFrmRect.offset(diff.x, diff.y)
        checkMoveBounds()
    }

    private fun moveHandleLT(diff: PointF) {
        if (mCropMode.isRatioFree) {
            mFrmRect.left += diff.x
            mFrmRect.top += diff.y
            if (isWidthTooSmall) {
                val offsetX = mMinFrameSize - frameW
                mFrmRect.left -= offsetX
            }
            if (isHeightTooSmall) {
                val offsetY = mMinFrameSize - frameH
                mFrmRect.top -= offsetY
            }
            checkScaleBounds()
        } else {
            val (ratioX, ratioY) = ratio
            val dy = diff.x * ratioY / ratioX
            mFrmRect.left += diff.x
            mFrmRect.top += dy
            if (isWidthTooSmall) {
                val offsetX = mMinFrameSize - frameW
                mFrmRect.left -= offsetX
                val offsetY = offsetX * ratioY / ratioX
                mFrmRect.top -= offsetY
            }
            if (isHeightTooSmall) {
                val offsetY = mMinFrameSize - frameH
                mFrmRect.top -= offsetY
                val offsetX = offsetY * ratioX / ratioY
                mFrmRect.left -= offsetX
            }
            var ox: Float
            var oy: Float
            if (!isInsideHorizontal(mFrmRect.left)) {
                ox = mImgRect.left - mFrmRect.left
                mFrmRect.left += ox
                oy = ox * ratioY / ratioX
                mFrmRect.top += oy
            }
            if (!isInsideVertical(mFrmRect.top)) {
                oy = mImgRect.top - mFrmRect.top
                mFrmRect.top += oy
                ox = oy * ratioX / ratioY
                mFrmRect.left += ox
            }
        }
    }

    private fun moveHandleRT(diff: PointF) {
        if (mCropMode.isRatioFree) {
            mFrmRect.right += diff.x
            mFrmRect.top += diff.y
            if (isWidthTooSmall) {
                val offsetX = mMinFrameSize - frameW
                mFrmRect.right += offsetX
            }
            if (isHeightTooSmall) {
                val offsetY = mMinFrameSize - frameH
                mFrmRect.top -= offsetY
            }
            checkScaleBounds()
        } else {
            val (ratioX, ratioY) = ratio
            val dy = diff.x * ratioY / ratioX
            mFrmRect.right += diff.x
            mFrmRect.top -= dy
            if (isWidthTooSmall) {
                val offsetX = mMinFrameSize - frameW
                mFrmRect.right += offsetX
                val offsetY = offsetX * ratioY / ratioX
                mFrmRect.top -= offsetY
            }
            if (isHeightTooSmall) {
                val offsetY = mMinFrameSize - frameH
                mFrmRect.top -= offsetY
                val offsetX = offsetY * ratioX / ratioY
                mFrmRect.right += offsetX
            }
            var ox: Float
            var oy: Float
            if (!isInsideHorizontal(mFrmRect.right)) {
                ox = mFrmRect.right - mImgRect.right
                mFrmRect.right -= ox
                oy = ox * ratioY / ratioX
                mFrmRect.top += oy
            }
            if (!isInsideVertical(mFrmRect.top)) {
                oy = mImgRect.top - mFrmRect.top
                mFrmRect.top += oy
                ox = oy * ratioX / ratioY
                mFrmRect.right -= ox
            }
        }
    }

    private fun moveHandleLB(diff: PointF) {
        if (mCropMode.isRatioFree) {
            mFrmRect.left += diff.x
            mFrmRect.bottom += diff.y
            if (isWidthTooSmall) {
                val offsetX = mMinFrameSize - frameW
                mFrmRect.left -= offsetX
            }
            if (isHeightTooSmall) {
                val offsetY = mMinFrameSize - frameH
                mFrmRect.bottom += offsetY
            }
            checkScaleBounds()
        } else {
            val (ratioX, ratioY) = ratio
            val dy = diff.x * ratioY / ratioX
            mFrmRect.left += diff.x
            mFrmRect.bottom -= dy
            if (isWidthTooSmall) {
                val offsetX = mMinFrameSize - frameW
                mFrmRect.left -= offsetX
                val offsetY = offsetX * ratioY / ratioX
                mFrmRect.bottom += offsetY
            }
            if (isHeightTooSmall) {
                val offsetY = mMinFrameSize - frameH
                mFrmRect.bottom += offsetY
                val offsetX = offsetY * ratioX / ratioY
                mFrmRect.left -= offsetX
            }
            var ox: Float
            var oy: Float
            if (!isInsideHorizontal(mFrmRect.left)) {
                ox = mImgRect.left - mFrmRect.left
                mFrmRect.left += ox
                oy = ox * ratioY / ratioX
                mFrmRect.bottom -= oy
            }
            if (!isInsideVertical(mFrmRect.bottom)) {
                oy = mFrmRect.bottom - mImgRect.bottom
                mFrmRect.bottom -= oy
                ox = oy * ratioX / ratioY
                mFrmRect.left += ox
            }
        }
    }

    private fun moveHandleRB(diff: PointF) {
        if (mCropMode.isRatioFree) {
            mFrmRect.right += diff.x
            mFrmRect.bottom += diff.y
            if (isWidthTooSmall) {
                val offsetX = mMinFrameSize - frameW
                mFrmRect.right += offsetX
            }
            if (isHeightTooSmall) {
                val offsetY = mMinFrameSize - frameH
                mFrmRect.bottom += offsetY
            }
            checkScaleBounds()
        } else {
            val (ratioX, ratioY) = ratio
            val dy = diff.x * ratioY / ratioX
            mFrmRect.right += diff.x
            mFrmRect.bottom += dy
            if (isWidthTooSmall) {
                val offsetX = mMinFrameSize - frameW
                mFrmRect.right += offsetX
                val offsetY = offsetX * ratioY / ratioX
                mFrmRect.bottom += offsetY
            }
            if (isHeightTooSmall) {
                val offsetY = mMinFrameSize - frameH
                mFrmRect.bottom += offsetY
                val offsetX = offsetY * ratioX / ratioY
                mFrmRect.right += offsetX
            }
            var ox: Float
            var oy: Float
            if (!isInsideHorizontal(mFrmRect.right)) {
                ox = mFrmRect.right - mImgRect.right
                mFrmRect.right -= ox
                oy = ox * ratioY / ratioX
                mFrmRect.bottom -= oy
            }
            if (!isInsideVertical(mFrmRect.bottom)) {
                oy = mFrmRect.bottom - mImgRect.bottom
                mFrmRect.bottom -= oy
                ox = oy * ratioX / ratioY
                mFrmRect.right -= ox
            }
        }
    }

    private fun checkScaleBounds() {
        val diff = (mFrmRect - mImgRect).bounds
        if (diff.left < 0) mFrmRect.left -= diff.left
        if (diff.right > 0) mFrmRect.right -= diff.right
        if (diff.top < 0) mFrmRect.top -= diff.top
        if (diff.bottom > 0) mFrmRect.bottom -= diff.bottom
    }

    private fun checkMoveBounds() {
        var diff = mFrmRect.left - mImgRect.left
        if (diff < 0) {
            mFrmRect.left -= diff
            mFrmRect.right -= diff
        }
        diff = mFrmRect.right - mImgRect.right
        if (diff > 0) {
            mFrmRect.left -= diff
            mFrmRect.right -= diff
        }
        diff = mFrmRect.top - mImgRect.top
        if (diff < 0) {
            mFrmRect.top -= diff
            mFrmRect.bottom -= diff
        }
        diff = mFrmRect.bottom - mImgRect.bottom
        if (diff > 0) {
            mFrmRect.top -= diff
            mFrmRect.bottom -= diff
        }
    }

    private fun isInsideHorizontal(x: Float): Boolean = mImgRect.run { x in left..right }

    private fun isInsideVertical(y: Float): Boolean = mImgRect.run { y in top..bottom }

    private val isWidthTooSmall: Boolean
        get() = frameW < mMinFrameSize

    private val isHeightTooSmall: Boolean
        get() = frameH < mMinFrameSize

    private fun adjustRatio() {
        val imgW = mImgRect.right - mImgRect.left
        val imgH = mImgRect.bottom - mImgRect.top
        val frameW = if (mCropMode.isRatioFree) imgW else ratio.x
        val frameH = if (mCropMode.isRatioFree) imgH else ratio.y
        val imgRatio = imgW / imgH
        val frameRatio = frameW / frameH
        var (l, t, r, b) = mImgRect
        if (frameRatio >= imgRatio) {
            val hy = (mImgRect.top + mImgRect.bottom) * 0.5F
            val hh = imgW / frameRatio * 0.5F
            t = hy - hh
            b = hy + hh
        } else if (frameRatio < imgRatio) {
            val hx = (mImgRect.left + mImgRect.right) * 0.5F
            val hw = imgH * frameRatio * 0.5F
            l = hx - hw
            r = hx + hw
        }
        val w = r - l
        val h = b - t
        mFrmRect.set(l + w / 8, t + h / 8, r - w / 8, b - h / 8)
        invalidate()
    }

    private val ratio: PointF
        get() = when (mCropMode) {
            CropMode.RATIO_FIT_IMAGE -> PointF(mImgWidth, mImgHeight)
            CropMode.RATIO_4_3 -> PointF(4.0F, 3.0F)
            CropMode.RATIO_3_4 -> PointF(3.0f, 4.0F)
            CropMode.RATIO_16_9 -> PointF(16.0F, 9.0F)
            CropMode.RATIO_9_16 -> PointF(9.0F, 16.0F)
            CropMode.RATIO_FREE, CropMode.RATIO_1_1 -> PointF(1.0F, 1.0F)
            CropMode.RATIO_CUSTOM -> mCustomRatio
        }

    private fun sq(value: Float): Float = value * value

    /* NOTE: Coil use this */
    override fun setImageDrawable(drawable: Drawable?) {
        if (drawable != null) setImageBitmap(drawable.toBitmap())
    }

    /**
     * Set source image bitmap
     *
     * @param bitmap src image bitmap
     */
    override fun setImageBitmap(bitmap: Bitmap?) {
        if (bitmap == null) return
        mIsInitialized = false
        if (imageBitmap != null && imageBitmap != bitmap) {
            imageBitmap = null
        }
        imageBitmap = bitmap
        if (imageBitmap != null) {
            mImgWidth = imageBitmap!!.width.toFloat()
            mImgHeight = imageBitmap!!.height.toFloat()
            initLayout(mViewWidth, mViewHeight)
        }
    }

    /**
     * Set source image resource id
     *
     * @param resId source image resource id
     */
    override fun setImageResource(resId: Int) {
        if (resId != 0) {
            val bitmap = BitmapFactory.decodeResource(resources, resId)
            setImageBitmap(bitmap)
        }
    }

    /**
     * Get cropped image bitmap
     *
     * @return cropped image bitmap
     * @throws [IllegalStateException] if [imageBitmap] is ```null```
     */
    val croppedBitmap: Bitmap
        get() {
            val l = (mFrmRect.left / mScale).toInt()
            val t = (mFrmRect.top / mScale).toInt()
            val r = (mFrmRect.right / mScale).toInt()
            val b = (mFrmRect.bottom / mScale).toInt()
            val x = l - (mImgRect.left / mScale).toInt()
            val y = t - (mImgRect.top / mScale).toInt()
            imageBitmap?.let {
                return Bitmap.createBitmap(it, x, y, r - l, b - t, null, false)
            }
            throw IllegalStateException("imageBitmap == null, did you forget to setImage?")
        }

    /**
     * Set crop mode
     *
     * @param mode crop mode
     */
    @Suppress("unused")
    fun setCropMode(mode: CropMode) {
        if (mode == CropMode.RATIO_CUSTOM) {
            setCustomRatio(1, 1)
        } else {
            mCropMode = mode
            adjustRatio()
        }
    }

    /**
     * Set custom aspect ratio to crop frame
     *
     * @param ratioX aspect ratio X
     * @param ratioY aspect ratio Y
     */
    fun setCustomRatio(ratioX: Int, ratioY: Int) {
        if (ratioX == 0 || ratioY == 0) return
        mCropMode = CropMode.RATIO_CUSTOM
        mCustomRatio = PointF(ratioX.toFloat(), ratioY.toFloat())
        adjustRatio()
    }

    /**
     * Set image overlay color
     *
     * @param overlayColor color resId or color int(ex. 0xFFFFFFFF)
     */
    @Suppress("unused")
    fun setOverlayColor(overlayColor: Int) {
        mOverlayColor = overlayColor
        invalidate()
    }

    /**
     * Set crop frame color
     *
     * @param frameColor color resId or color int(ex. 0xFFFFFFFF)
     */
    @Suppress("unused")
    fun setFrameColor(frameColor: Int) {
        mFrameColor = frameColor
        invalidate()
    }

    /**
     * Set handle color
     *
     * @param handleColor color resId or color int(ex. 0xFFFFFFFF)
     */
    @Suppress("unused")
    fun setHandleColor(handleColor: Int) {
        mHandleColor = handleColor
        invalidate()
    }

    /**
     * Set guide color
     *
     * @param guideColor color resId or color int(ex. 0xFFFFFFFF)
     */
    @Suppress("unused")
    fun setGuideColor(guideColor: Int) {
        mGuideColor = guideColor
        invalidate()
    }

    /**
     * Set view background color
     *
     * @param bgColor color resId or color int(ex. 0xFFFFFFFF)
     */
    override fun setBackgroundColor(bgColor: Int) {
        mBackgroundColor = bgColor
        super.setBackgroundColor(mBackgroundColor)
        invalidate()
    }

    /**
     * Set crop frame minimum size in density-independent pixels.
     *
     * @param minDp crop frame minimum size in density-independent pixels
     */
    @Suppress("unused")
    fun setMinFrameSizeInDp(minDp: Int) {
        mMinFrameSize = minDp * density
    }

    /**
     * Set handle radius in density-independent pixels.
     *
     * @param handleDp handle radius in density-independent pixels
     */
    @Suppress("unused")
    fun setHandleSizeInDp(handleDp: Int) {
        mHandleSize = (handleDp * density)
    }

    /**
     * Set crop frame handle touch padding(touch area) in density-independent pixels.
     *
     *
     * handle touch area : a circle of radius R.(R = handle size + touch padding)
     *
     * @param paddingDp crop frame handle touch padding(touch area) in density-independent pixels
     */
    @Suppress("unused")
    fun setTouchPaddingInDp(paddingDp: Int) {
        mTouchPadding = (paddingDp * density)
    }

    /**
     * Set guideline show mode.
     * (SHOW_ALWAYS/NOT_SHOW/SHOW_ON_TOUCH)
     *
     * @param mode guideline show mode
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun setGuideShowMode(mode: ShowMode) {
        mGuideShowMode = mode
        mShowGuide = mode == ShowMode.SHOW_ALWAYS
        invalidate()
    }

    /**
     * Set handle show mode.
     * (SHOW_ALWAYS/NOT_SHOW/SHOW_ON_TOUCH)
     *
     * @param mode handle show mode
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun setHandleShowMode(mode: ShowMode) {
        mHandleShowMode = mode
        mShowHandle = mode == ShowMode.SHOW_ALWAYS
        invalidate()
    }

    /**
     * Set frame stroke weight in density-independent pixels.
     *
     * @param weightDp frame stroke weight in density-independent pixels.
     */
    @Suppress("unused")
    fun setFrameStrokeWeightInDp(weightDp: Int) {
        mFrameStrokeWeight = weightDp * density
        invalidate()
    }

    /**
     * Set guideline stroke weight in density-independent pixels.
     *
     * @param weightDp guideline stroke weight in density-independent pixels.
     */
    @Suppress("unused")
    fun setGuideStrokeWeightInDp(weightDp: Int) {
        mGuideStrokeWeight = weightDp * density
        invalidate()
    }

    private val frameW: Float
        get() = mFrmRect.right - mFrmRect.left

    private val frameH: Float
        get() = mFrmRect.bottom - mFrmRect.top

    private enum class TouchArea {
        OUT_OF_BOUNDS, CENTER, LEFT_TOP, RIGHT_TOP, LEFT_BOTTOM, RIGHT_BOTTOM
    }

    enum class CropMode(val id: Int) {
        RATIO_FIT_IMAGE(0),
        RATIO_4_3(1),
        RATIO_3_4(2),
        RATIO_1_1(3),
        RATIO_16_9(4),
        RATIO_9_16(5),
        RATIO_FREE(6),
        RATIO_CUSTOM(7);

        val isRatioFree: Boolean
            get() = this == RATIO_FREE
    }

    enum class ShowMode(val id: Int) { SHOW_ALWAYS(1), SHOW_ON_TOUCH(2), NOT_SHOW(3); }

    // Save/Restore support ////////////////////////////////////////////////////////////////////////
    internal class SavedState : AbsSavedState {
        var image: Bitmap? = null
        var mode: CropMode = CropMode.RATIO_1_1
        var backgroundColor = 0
        var overlayColor = 0
        var frameColor = 0
        var guideShowMode: ShowMode = ShowMode.SHOW_ALWAYS
        var handleShowMode: ShowMode = ShowMode.SHOW_ALWAYS
        var showGuide = false
        var showHandle = false
        var handleSize = 0F
        var touchPadding = 0F
        var minFrameSize = 0F
        var customRatio = PointF(1.0F, 1.0F)
        var frameStrokeWeight = 0F
        var guideStrokeWeight = 0F
        var handleColor = 0
        var guideColor = 0

        constructor(superState: Parcelable) : super(superState)
        constructor(source: Parcel, loader: ClassLoader?) : super(source, loader) {
            image = source.readParcelable(Bitmap::class.java.classLoader)
            mode = source.readSerializable() as CropMode
            backgroundColor = source.readInt()
            overlayColor = source.readInt()
            frameColor = source.readInt()
            guideShowMode = source.readSerializable() as ShowMode
            handleShowMode = source.readSerializable() as ShowMode
            showGuide = source.readInt() != 0
            showHandle = source.readInt() != 0
            handleSize = source.readFloat()
            touchPadding = source.readFloat()
            minFrameSize = source.readFloat()
            source.readParcelable<PointF>(PointF::class.java.classLoader)?.let { customRatio = it }
            frameStrokeWeight = source.readFloat()
            guideStrokeWeight = source.readFloat()
            handleColor = source.readInt()
            guideColor = source.readInt()
        }

        override fun writeToParcel(out: Parcel, flag: Int) {
            super.writeToParcel(out, flag)
            out.writeParcelable(image, flag)
            out.writeSerializable(mode)
            out.writeInt(backgroundColor)
            out.writeInt(overlayColor)
            out.writeInt(frameColor)
            out.writeSerializable(guideShowMode)
            out.writeSerializable(handleShowMode)
            out.writeInt(if (showGuide) 1 else 0)
            out.writeInt(if (showHandle) 1 else 0)
            out.writeFloat(handleSize)
            out.writeFloat(touchPadding)
            out.writeFloat(minFrameSize)
            out.writeParcelable(customRatio, flag)
            out.writeFloat(frameStrokeWeight)
            out.writeFloat(guideStrokeWeight)
            out.writeInt(handleColor)
            out.writeInt(guideColor)
        }

        companion object {
            @Suppress("unused")
            @JvmField
            val CREATOR = object : ClassLoaderCreator<SavedState> {
                override fun createFromParcel(source: Parcel, loader: ClassLoader?): SavedState =
                    SavedState(source, loader)

                override fun createFromParcel(parcel: Parcel): SavedState = SavedState(parcel, null)
                override fun newArray(size: Int): Array<SavedState> = newArray(size)
            }
        }
    }

    companion object {
        private const val HANDLE_SIZE_IN_DP = 16
        private const val MIN_FRAME_SIZE_IN_DP = 50
        private const val FRAME_STROKE_WEIGHT_IN_DP = 1
        private const val GUIDE_STROKE_WEIGHT_IN_DP = 1
        private const val TRANSPARENT = Color.TRANSPARENT
        private const val TRANSLUCENT_WHITE = -0x44000001
        private const val WHITE = -0x1
        private const val TRANSLUCENT_BLACK = -0x45000000
    }
}
