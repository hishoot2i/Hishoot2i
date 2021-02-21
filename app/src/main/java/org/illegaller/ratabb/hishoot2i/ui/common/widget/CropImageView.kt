package org.illegaller.ratabb.hishoot2i.ui.common.widget

import android.annotation.SuppressLint
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
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.res.use
import androidx.core.graphics.component1
import androidx.core.graphics.component2
import androidx.core.graphics.component3
import androidx.core.graphics.component4
import androidx.core.graphics.drawable.toBitmap
import androidx.customview.view.AbsSavedState
import common.ext.displayMetrics
import org.illegaller.ratabb.hishoot2i.R

// TODO: clean this !
class CropImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = R.style.AppWidget_CropImageView
) : AppCompatImageView(context, attrs, defStyle) {
    private var mViewWidth = 0
    private var mViewHeight = 0
    private var mScale = 1.0F
    private var mImgWidth = 0.0F
    private var mImgHeight = 0.0F
    private var mIsInitialized = false
    private val mMatrix = Matrix()
    private val density = context.displayMetrics.density
    private val mPaintTransparent = Paint(Paint.FILTER_BITMAP_FLAG)
    private val mPaintFrame = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)
    private val mPaintBitmap = Paint(Paint.FILTER_BITMAP_FLAG)
    private var mFrameRect = RectF()
    private var mImageRect = RectF()
    private var mCenter = PointF()
    private var mLastX = 0F
    private var mLastY = 0F
    private var mTouchArea = TouchArea.OUT_OF_BOUNDS
    private var mCropMode: CropMode = CropMode.RATIO_1_1
    private var mGuideShowMode: ShowMode = ShowMode.SHOW_ALWAYS
    private var mHandleShowMode: ShowMode = ShowMode.SHOW_ALWAYS
    private var mMinFrameSize = density * MIN_FRAME_SIZE_IN_DP
    private var mHandleSize = density * HANDLE_SIZE_IN_DP
    private var mTouchPadding = 0F
    private var mShowGuide = true
    private var mShowHandle = true
    private var mIsCropEnabled = true
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
        // Handle styleable ////////////////////////////////////////////////////////////////////////
        context.obtainStyledAttributes(attrs, R.styleable.CropImageView, 0, defStyle).use { ta ->
            setImageDrawable(ta.getDrawable(R.styleable.CropImageView_imgSrc))
            mCropMode = CropMode.values().getOrElse(
                ta.getInt(R.styleable.CropImageView_cropMode, CropMode.RATIO_1_1.id)
            ) { CropMode.RATIO_1_1 }
            mBackgroundColor = ta.getColor(R.styleable.CropImageView_backgroundColor, TRANSPARENT)
            super.setBackgroundColor(mBackgroundColor) //
            mOverlayColor = ta.getColor(R.styleable.CropImageView_overlayColor, TRANSLUCENT_BLACK)
            mFrameColor = ta.getColor(R.styleable.CropImageView_frameColor, WHITE)
            mHandleColor = ta.getColor(R.styleable.CropImageView_handleColor, WHITE)
            mGuideColor = ta.getColor(R.styleable.CropImageView_guideColor, TRANSLUCENT_WHITE)
            mGuideShowMode = ShowMode.values().getOrElse(
                ta.getInt(R.styleable.CropImageView_guideShowMode, ShowMode.SHOW_ALWAYS.id)
            ) { ShowMode.SHOW_ALWAYS }
            mHandleShowMode = ShowMode.values().getOrElse(
                ta.getInt(R.styleable.CropImageView_handleShowMode, ShowMode.SHOW_ALWAYS.id)
            ) { ShowMode.SHOW_ALWAYS }
            setGuideShowMode(mGuideShowMode)
            setHandleShowMode(mHandleShowMode)
            mHandleSize = ta.getDimensionPixelSize(
                R.styleable.CropImageView_handleSize,
                (HANDLE_SIZE_IN_DP * this.density).toInt()
            ).toFloat()
            mTouchPadding = ta.getDimensionPixelSize(R.styleable.CropImageView_touchPadding, 0)
                .toFloat()
            mMinFrameSize = ta.getDimensionPixelSize(
                R.styleable.CropImageView_minFrameSize,
                (MIN_FRAME_SIZE_IN_DP * this.density).toInt()
            ).toFloat()
            mFrameStrokeWeight = ta.getDimensionPixelSize(
                R.styleable.CropImageView_frameStrokeWeight,
                (FRAME_STROKE_WEIGHT_IN_DP * this.density).toInt()
            ).toFloat()
            mGuideStrokeWeight = ta.getDimensionPixelSize(
                R.styleable.CropImageView_guideStrokeWeight,
                (GUIDE_STROKE_WEIGHT_IN_DP * this.density).toInt()
            ).toFloat()
            mIsCropEnabled = ta.getBoolean(R.styleable.CropImageView_cropEnabled, true)
        }
    }

    public override fun onSaveInstanceState(): Parcelable? =
        when (val superState = super.onSaveInstanceState()) {
            null -> superState
            else -> {
                SavedState(superState).apply {
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
                    customRatioX = mCustomRatio.x
                    customRatioY = mCustomRatio.y
                    frameStrokeWeight = mFrameStrokeWeight
                    guideStrokeWeight = mGuideStrokeWeight
                    isCropEnabled = mIsCropEnabled
                    handleColor = mHandleColor
                    guideColor = mGuideColor
                }
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
                mCustomRatio = PointF(state.customRatioX, state.customRatioY)
                mFrameStrokeWeight = state.frameStrokeWeight
                mGuideStrokeWeight = state.guideStrokeWeight
                mIsCropEnabled = state.isCropEnabled
                mHandleColor = state.handleColor
                mGuideColor = state.guideColor
                setImageBitmap(state.image!!)
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

    // Drawing method //////////////////////////////////////////////////////////////////////////////
    private fun drawEditFrame(canvas: Canvas) {
        if (!mIsCropEnabled) return
        mPaintTransparent.apply {
            color = mOverlayColor
            style = Paint.Style.FILL
        }
        canvas.drawRect(
            mImageRect.left, mImageRect.top, mImageRect.right, mFrameRect.top,
            mPaintTransparent
        )
        canvas.drawRect(
            mImageRect.left, mFrameRect.bottom, mImageRect.right, mImageRect.bottom,
            mPaintTransparent
        )
        canvas.drawRect(
            mImageRect.left, mFrameRect.top, mFrameRect.left, mFrameRect.bottom,
            mPaintTransparent
        )
        canvas.drawRect(
            mFrameRect.right, mFrameRect.top, mImageRect.right, mFrameRect.bottom,
            mPaintTransparent
        )
        mPaintFrame.apply {
            style = Paint.Style.STROKE
            color = mFrameColor
            strokeWidth = mFrameStrokeWeight
        }
        canvas.drawRect(
            mFrameRect.left, mFrameRect.top, mFrameRect.right, mFrameRect.bottom, mPaintFrame
        )
        if (mShowGuide) {
            mPaintFrame.apply {
                color = mGuideColor
                strokeWidth = mGuideStrokeWeight
            }
            val h1 = mFrameRect.left + (mFrameRect.right - mFrameRect.left) / 3.0f
            val h2 = mFrameRect.right - (mFrameRect.right - mFrameRect.left) / 3.0f
            val v1 = mFrameRect.top + (mFrameRect.bottom - mFrameRect.top) / 3.0f
            val v2 = mFrameRect.bottom - (mFrameRect.bottom - mFrameRect.top) / 3.0f
            canvas.drawLine(h1, mFrameRect.top, h1, mFrameRect.bottom, mPaintFrame)
            canvas.drawLine(h2, mFrameRect.top, h2, mFrameRect.bottom, mPaintFrame)
            canvas.drawLine(mFrameRect.left, v1, mFrameRect.right, v1, mPaintFrame)
            canvas.drawLine(mFrameRect.left, v2, mFrameRect.right, v2, mPaintFrame)
        }
        if (mShowHandle) {
            mPaintFrame.apply {
                style = Paint.Style.FILL
                color = mHandleColor
            }
            canvas.drawCircle(mFrameRect.left, mFrameRect.top, mHandleSize, mPaintFrame)
            canvas.drawCircle(mFrameRect.right, mFrameRect.top, mHandleSize, mPaintFrame)
            canvas.drawCircle(mFrameRect.left, mFrameRect.bottom, mHandleSize, mPaintFrame)
            canvas.drawCircle(mFrameRect.right, mFrameRect.bottom, mHandleSize, mPaintFrame)
        }
    }

    private fun setMatrix() {
        mMatrix.reset()
        mMatrix.setTranslate(mCenter.x - mImgWidth * 0.5f, mCenter.y - mImgHeight * 0.5f)
        mMatrix.postScale(mScale, mScale, mCenter.x, mCenter.y)
        mMatrix.postRotate(/*mAngle*/0.0F, mCenter.x, mCenter.y)
    }

    // Initializer /////////////////////////////////////////////////////////////////////////////////
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
        mCenter = PointF(paddingLeft + w * 0.5f, paddingTop + h * 0.5f)
        mScale = scale
        initCropFrame()
        adjustRatio()
        mIsInitialized = true
    }

    private fun initCropFrame() {
        setMatrix()
        val arrayOfFloat = floatArrayOf(
            0.0F, 0.0F, 0.0F, mImgHeight,
            mImgWidth, 0.0F, mImgWidth, mImgHeight
        )
        mMatrix.mapPoints(arrayOfFloat)
        val l = arrayOfFloat[0]
        val t = arrayOfFloat[1]
        val r = arrayOfFloat[6]
        val b = arrayOfFloat[7]
        mFrameRect = RectF(l, t, r, b)
        mImageRect = RectF(l, t, r, b)
    }

    // Touch Event /////////////////////////////////////////////////////////////////////////////////
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!mIsInitialized) return false
        if (!mIsCropEnabled) return false
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                onDown(event)
                true
            }
            MotionEvent.ACTION_MOVE -> {
                onMove(event)
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

    private fun onDown(e: MotionEvent) {
        invalidate()
        mLastX = e.x
        mLastY = e.y
        checkTouchArea(e.x, e.y)
    }

    private fun onMove(e: MotionEvent) {
        val diffX = e.x - mLastX
        val diffY = e.y - mLastY
        when (mTouchArea) {
            TouchArea.CENTER -> moveFrame(diffX, diffY)
            TouchArea.LEFT_TOP -> moveHandleLT(diffX, diffY)
            TouchArea.RIGHT_TOP -> moveHandleRT(diffX, diffY)
            TouchArea.LEFT_BOTTOM -> moveHandleLB(diffX, diffY)
            TouchArea.RIGHT_BOTTOM -> moveHandleRB(diffX, diffY)
            TouchArea.OUT_OF_BOUNDS -> {
            }
        }
        invalidate()
        mLastX = e.x
        mLastY = e.y
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

    private fun isInsideFrame(x: Float, y: Float): Boolean = with(mFrameRect) {
        (x in left..right) and (y in top..bottom)
    }

    private fun isInsideCornerLeftTop(x: Float, y: Float): Boolean {
        val dx = x - mFrameRect.left
        val dy = y - mFrameRect.top
        val d = dx * dx + dy * dy
        return sq(mHandleSize + mTouchPadding) >= d
    }

    private fun isInsideCornerRightTop(x: Float, y: Float): Boolean {
        val dx = x - mFrameRect.right
        val dy = y - mFrameRect.top
        val d = dx * dx + dy * dy
        return sq(mHandleSize + mTouchPadding) >= d
    }

    private fun isInsideCornerLeftBottom(x: Float, y: Float): Boolean {
        val dx = x - mFrameRect.left
        val dy = y - mFrameRect.bottom
        val d = dx * dx + dy * dy
        return sq(mHandleSize + mTouchPadding) >= d
    }

    private fun isInsideCornerRightBottom(x: Float, y: Float): Boolean {
        val dx = x - mFrameRect.right
        val dy = y - mFrameRect.bottom
        val d = dx * dx + dy * dy
        return sq(mHandleSize + mTouchPadding) >= d
    }

    private fun moveFrame(x: Float, y: Float) {
        mFrameRect.left += x
        mFrameRect.right += x
        mFrameRect.top += y
        mFrameRect.bottom += y
        checkMoveBounds()
    }

    private fun moveHandleLT(diffX: Float, diffY: Float) {
        if (mCropMode == CropMode.RATIO_FREE) {
            mFrameRect.left += diffX
            mFrameRect.top += diffY
            if (isWidthTooSmall) {
                val offsetX = mMinFrameSize - frameW
                mFrameRect.left -= offsetX
            }
            if (isHeightTooSmall) {
                val offsetY = mMinFrameSize - frameH
                mFrameRect.top -= offsetY
            }
            checkScaleBounds()
        } else {
            val dy = diffX * ratioY / ratioX
            mFrameRect.left += diffX
            mFrameRect.top += dy
            if (isWidthTooSmall) {
                val offsetX = mMinFrameSize - frameW
                mFrameRect.left -= offsetX
                val offsetY = offsetX * ratioY / ratioX
                mFrameRect.top -= offsetY
            }
            if (isHeightTooSmall) {
                val offsetY = mMinFrameSize - frameH
                mFrameRect.top -= offsetY
                val offsetX = offsetY * ratioX / ratioY
                mFrameRect.left -= offsetX
            }
            var ox: Float
            var oy: Float
            if (!isInsideHorizontal(mFrameRect.left)) {
                ox = mImageRect.left - mFrameRect.left
                mFrameRect.left += ox
                oy = ox * ratioY / ratioX
                mFrameRect.top += oy
            }
            if (!isInsideVertical(mFrameRect.top)) {
                oy = mImageRect.top - mFrameRect.top
                mFrameRect.top += oy
                ox = oy * ratioX / ratioY
                mFrameRect.left += ox
            }
        }
    }

    private fun moveHandleRT(diffX: Float, diffY: Float) {
        if (mCropMode == CropMode.RATIO_FREE) {
            mFrameRect.right += diffX
            mFrameRect.top += diffY
            if (isWidthTooSmall) {
                val offsetX = mMinFrameSize - frameW
                mFrameRect.right += offsetX
            }
            if (isHeightTooSmall) {
                val offsetY = mMinFrameSize - frameH
                mFrameRect.top -= offsetY
            }
            checkScaleBounds()
        } else {
            val dy = diffX * ratioY / ratioX
            mFrameRect.right += diffX
            mFrameRect.top -= dy
            if (isWidthTooSmall) {
                val offsetX = mMinFrameSize - frameW
                mFrameRect.right += offsetX
                val offsetY = offsetX * ratioY / ratioX
                mFrameRect.top -= offsetY
            }
            if (isHeightTooSmall) {
                val offsetY = mMinFrameSize - frameH
                mFrameRect.top -= offsetY
                val offsetX = offsetY * ratioX / ratioY
                mFrameRect.right += offsetX
            }
            var ox: Float
            var oy: Float
            if (!isInsideHorizontal(mFrameRect.right)) {
                ox = mFrameRect.right - mImageRect.right
                mFrameRect.right -= ox
                oy = ox * ratioY / ratioX
                mFrameRect.top += oy
            }
            if (!isInsideVertical(mFrameRect.top)) {
                oy = mImageRect.top - mFrameRect.top
                mFrameRect.top += oy
                ox = oy * ratioX / ratioY
                mFrameRect.right -= ox
            }
        }
    }

    private fun moveHandleLB(diffX: Float, diffY: Float) {
        if (mCropMode == CropMode.RATIO_FREE) {
            mFrameRect.left += diffX
            mFrameRect.bottom += diffY
            if (isWidthTooSmall) {
                val offsetX = mMinFrameSize - frameW
                mFrameRect.left -= offsetX
            }
            if (isHeightTooSmall) {
                val offsetY = mMinFrameSize - frameH
                mFrameRect.bottom += offsetY
            }
            checkScaleBounds()
        } else {
            val dy = diffX * ratioY / ratioX
            mFrameRect.left += diffX
            mFrameRect.bottom -= dy
            if (isWidthTooSmall) {
                val offsetX = mMinFrameSize - frameW
                mFrameRect.left -= offsetX
                val offsetY = offsetX * ratioY / ratioX
                mFrameRect.bottom += offsetY
            }
            if (isHeightTooSmall) {
                val offsetY = mMinFrameSize - frameH
                mFrameRect.bottom += offsetY
                val offsetX = offsetY * ratioX / ratioY
                mFrameRect.left -= offsetX
            }
            var ox: Float
            var oy: Float
            if (!isInsideHorizontal(mFrameRect.left)) {
                ox = mImageRect.left - mFrameRect.left
                mFrameRect.left += ox
                oy = ox * ratioY / ratioX
                mFrameRect.bottom -= oy
            }
            if (!isInsideVertical(mFrameRect.bottom)) {
                oy = mFrameRect.bottom - mImageRect.bottom
                mFrameRect.bottom -= oy
                ox = oy * ratioX / ratioY
                mFrameRect.left += ox
            }
        }
    }

    private fun moveHandleRB(diffX: Float, diffY: Float) {
        if (mCropMode == CropMode.RATIO_FREE) {
            mFrameRect.right += diffX
            mFrameRect.bottom += diffY
            if (isWidthTooSmall) {
                val offsetX = mMinFrameSize - frameW
                mFrameRect.right += offsetX
            }
            if (isHeightTooSmall) {
                val offsetY = mMinFrameSize - frameH
                mFrameRect.bottom += offsetY
            }
            checkScaleBounds()
        } else {
            val dy = diffX * ratioY / ratioX
            mFrameRect.right += diffX
            mFrameRect.bottom += dy
            if (isWidthTooSmall) {
                val offsetX = mMinFrameSize - frameW
                mFrameRect.right += offsetX
                val offsetY = offsetX * ratioY / ratioX
                mFrameRect.bottom += offsetY
            }
            if (isHeightTooSmall) {
                val offsetY = mMinFrameSize - frameH
                mFrameRect.bottom += offsetY
                val offsetX = offsetY * ratioX / ratioY
                mFrameRect.right += offsetX
            }
            var ox: Float
            var oy: Float
            if (!isInsideHorizontal(mFrameRect.right)) {
                ox = mFrameRect.right - mImageRect.right
                mFrameRect.right -= ox
                oy = ox * ratioY / ratioX
                mFrameRect.bottom -= oy
            }
            if (!isInsideVertical(mFrameRect.bottom)) {
                oy = mFrameRect.bottom - mImageRect.bottom
                mFrameRect.bottom -= oy
                ox = oy * ratioX / ratioY
                mFrameRect.right -= ox
            }
        }
    }

    // Frame position correction ///////////////////////////////////////////////////////////////////
    private fun checkScaleBounds() {
        val lDiff = mFrameRect.left - mImageRect.left
        val rDiff = mFrameRect.right - mImageRect.right
        val tDiff = mFrameRect.top - mImageRect.top
        val bDiff = mFrameRect.bottom - mImageRect.bottom
        if (lDiff < 0) {
            mFrameRect.left -= lDiff
        }
        if (rDiff > 0) {
            mFrameRect.right -= rDiff
        }
        if (tDiff < 0) {
            mFrameRect.top -= tDiff
        }
        if (bDiff > 0) {
            mFrameRect.bottom -= bDiff
        }
    }

    private fun checkMoveBounds() {
        var diff = mFrameRect.left - mImageRect.left
        if (diff < 0) {
            mFrameRect.left -= diff
            mFrameRect.right -= diff
        }
        diff = mFrameRect.right - mImageRect.right
        if (diff > 0) {
            mFrameRect.left -= diff
            mFrameRect.right -= diff
        }
        diff = mFrameRect.top - mImageRect.top
        if (diff < 0) {
            mFrameRect.top -= diff
            mFrameRect.bottom -= diff
        }
        diff = mFrameRect.bottom - mImageRect.bottom
        if (diff > 0) {
            mFrameRect.top -= diff
            mFrameRect.bottom -= diff
        }
    }

    private fun isInsideHorizontal(x: Float): Boolean = with(mImageRect) { x in left..right }

    private fun isInsideVertical(y: Float): Boolean = with(mImageRect) { y in top..bottom }

    private val isWidthTooSmall: Boolean
        get() = frameW < mMinFrameSize

    private val isHeightTooSmall: Boolean
        get() = frameH < mMinFrameSize

    // Frame aspect ratio correction ///////////////////////////////////////////////////////////////
    private fun adjustRatio() {
        val imgW = mImageRect.right - mImageRect.left
        val imgH = mImageRect.bottom - mImageRect.top
        val frameW = if (mCropMode == CropMode.RATIO_FREE) imgW else ratioX
        val frameH = if (mCropMode == CropMode.RATIO_FREE) imgH else ratioY
        val imgRatio = imgW / imgH
        val frameRatio = frameW / frameH
        var (l, t, r, b) = mImageRect
        if (frameRatio >= imgRatio) {
            val hy = (mImageRect.top + mImageRect.bottom) * 0.5f
            val hh = imgW / frameRatio * 0.5f
            t = hy - hh
            b = hy + hh
        } else if (frameRatio < imgRatio) {
            val hx = (mImageRect.left + mImageRect.right) * 0.5f
            val hw = imgH * frameRatio * 0.5f
            l = hx - hw
            r = hx + hw
        }
        val w = r - l
        val h = b - t
        mFrameRect = RectF(l + w / 8, t + h / 8, r - w / 8, b - h / 8)
        invalidate()
    }

    private val ratioX: Float
        get() = when (mCropMode) {
            CropMode.RATIO_FIT_IMAGE -> mImgWidth
            CropMode.RATIO_4_3 -> 4.0f
            CropMode.RATIO_3_4 -> 3.0f
            CropMode.RATIO_16_9 -> 16.0f
            CropMode.RATIO_9_16 -> 9.0f
            CropMode.RATIO_FREE, CropMode.RATIO_1_1 -> 1.0f
            CropMode.RATIO_CUSTOM -> mCustomRatio.x
        }

    private val ratioY: Float
        get() = when (mCropMode) {
            CropMode.RATIO_FIT_IMAGE -> mImgHeight
            CropMode.RATIO_4_3 -> 3.0f
            CropMode.RATIO_3_4 -> 4.0f
            CropMode.RATIO_16_9 -> 9.0f
            CropMode.RATIO_9_16 -> 16.0f
            CropMode.RATIO_FREE, CropMode.RATIO_1_1 -> 1.0f
            CropMode.RATIO_CUSTOM -> mCustomRatio.y
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
    override fun setImageBitmap(bitmap: Bitmap) {
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
     */
    val croppedBitmap: Bitmap
        get() {
            val l = (mFrameRect.left / mScale).toInt()
            val t = (mFrameRect.top / mScale).toInt()
            val r = (mFrameRect.right / mScale).toInt()
            val b = (mFrameRect.bottom / mScale).toInt()
            val x = l - (mImageRect.left / mScale).toInt()
            val y = t - (mImageRect.top / mScale).toInt()
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

    /**
     * Set whether to show crop frame.
     *
     * @param enabled should show crop frame?
     */
    @Suppress("unused")
    fun setCropEnabled(enabled: Boolean) {
        mIsCropEnabled = enabled
        invalidate()
    }

    private val frameW: Float
        get() = mFrameRect.right - mFrameRect.left

    private val frameH: Float
        get() = mFrameRect.bottom - mFrameRect.top

    // Enum ////////////////////////////////////////////////////////////////////////////////////////
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
        var customRatioX = 0F
        var customRatioY = 0F
        var frameStrokeWeight = 0F
        var guideStrokeWeight = 0F
        var isCropEnabled = false
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
            customRatioX = source.readFloat()
            customRatioY = source.readFloat()
            frameStrokeWeight = source.readFloat()
            guideStrokeWeight = source.readFloat()
            isCropEnabled = source.readInt() != 0
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
            out.writeFloat(customRatioX)
            out.writeFloat(customRatioY)
            out.writeFloat(frameStrokeWeight)
            out.writeFloat(guideStrokeWeight)
            out.writeInt(if (isCropEnabled) 1 else 0)
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
        // Constants //////////////////////////////////////////////////////////////////////////
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
