@file:Suppress("MemberVisibilityCanBePrivate")

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
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.ClassLoaderCreator
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatImageView
import androidx.customview.view.AbsSavedState
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.ui.common.readBooleanCompat
import org.illegaller.ratabb.hishoot2i.ui.common.writeBooleanCompat
import timber.log.Timber

// TODO: clean this !
class CropImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = R.style.AppWidget_CropImageView
) : AppCompatImageView(context, attrs, defStyle) {
    // Member variables ////////////////////////////////////////////////////////////////////////////
    private var mViewWidth = 0
    private var mViewHeight = 0

    /**
     * Get source image bitmap
     *
     * @return src bitmap
     */
    private var imageBitmap: Bitmap? = null
    private var mScale = 1.0f
    private val mAngle = 0.0f
    private var mImgWidth = 0.0f
    private var mImgHeight = 0.0f
    private var mIsInitialized = false
    private var mMatrix: Matrix? = null
    private val mPaintTransparent: Paint
    private val mPaintFrame: Paint
    private val mPaintBitmap: Paint
    private var mFrameRect: RectF? = null
    private var mImageRect: RectF? = null
    private var mCenter = PointF()
    private var mLastX = 0f
    private var mLastY = 0f

    // Instance variables for customizable attributes //////////////////////////////////////////////
    private var mTouchArea = TouchArea.OUT_OF_BOUNDS
    private var mCropMode: CropMode? = CropMode.RATIO_1_1
    private var mGuideShowMode: ShowMode? = ShowMode.SHOW_ALWAYS
    private var mHandleShowMode: ShowMode? = ShowMode.SHOW_ALWAYS
    private var mMinFrameSize: Float
    private var mHandleSize: Int
    private var mTouchPadding = 0
    private var mShowGuide = true
    private var mShowHandle = true
    private var mIsCropEnabled = true
    private var mCustomRatio = PointF(1.0f, 1.0f)
    private var mFrameStrokeWeight = 3.0f
    private var mGuideStrokeWeight = 3.0f
    private var mBackgroundColor: Int
    private var mOverlayColor: Int
    private var mFrameColor: Int
    private var mHandleColor: Int
    private var mGuideColor: Int

    // Lifecycle methods ///////////////////////////////////////////////////////////////////////////
    public override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        superState?.let {
            val savedState = SavedState(superState)
            savedState.image = imageBitmap
            savedState.mode = mCropMode
            savedState.backgroundColor = mBackgroundColor
            savedState.overlayColor = mOverlayColor
            savedState.frameColor = mFrameColor
            savedState.guideShowMode = mGuideShowMode
            savedState.handleShowMode = mHandleShowMode
            savedState.showGuide = mShowGuide
            savedState.showHandle = mShowHandle
            savedState.handleSize = mHandleSize
            savedState.touchPadding = mTouchPadding
            savedState.minFrameSize = mMinFrameSize
            savedState.customRatioX = mCustomRatio.x
            savedState.customRatioY = mCustomRatio.y
            savedState.frameStrokeWeight = mFrameStrokeWeight
            savedState.guideStrokeWeight = mGuideStrokeWeight
            savedState.isCropEnabled = mIsCropEnabled
            savedState.handleColor = mHandleColor
            savedState.guideColor = mGuideColor
            return savedState
        } ?: run {
            return superState
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
            @SuppressLint("DrawAllocation") val localMatrix1 = Matrix()
            localMatrix1.postConcat(mMatrix)
            canvas.drawBitmap(imageBitmap!!, localMatrix1, mPaintBitmap)

            // draw edit frame
            drawEditFrame(canvas)
        }
    }

    // Handle styleable ////////////////////////////////////////////////////////////////////////////
    private fun handleStyleable(
        context: Context,
        attrs: AttributeSet?,
        defStyle: Int,
        mDensity: Float
    ) {
        val ta =
            context.obtainStyledAttributes(attrs, R.styleable.CropImageView, defStyle, 0)
        val drawable: Drawable?
        mCropMode = CropMode.RATIO_1_1
        try {
            drawable = ta.getDrawable(R.styleable.CropImageView_imgSrc)
            if (drawable != null) setImageBitmap((drawable as BitmapDrawable).bitmap)
            for (mode in CropMode.values()) {
                if (ta.getInt(R.styleable.CropImageView_cropMode, 3) == mode.id) {
                    mCropMode = mode
                    break
                }
            }
            mBackgroundColor = ta.getColor(
                R.styleable.CropImageView_backgroundColor,
                TRANSPARENT
            )
            super.setBackgroundColor(mBackgroundColor)
            mOverlayColor = ta.getColor(
                R.styleable.CropImageView_overlayColor,
                TRANSLUCENT_BLACK
            )
            mFrameColor =
                ta.getColor(R.styleable.CropImageView_frameColor, WHITE)
            mHandleColor =
                ta.getColor(R.styleable.CropImageView_handleColor, WHITE)
            mGuideColor = ta.getColor(
                R.styleable.CropImageView_guideColor,
                TRANSLUCENT_WHITE
            )
            for (mode in ShowMode.values()) {
                if (ta.getInt(R.styleable.CropImageView_guideShowMode, 1) == mode.id) {
                    mGuideShowMode = mode
                    break
                }
            }
            for (mode in ShowMode.values()) {
                if (ta.getInt(R.styleable.CropImageView_handleShowMode, 1) == mode.id) {
                    mHandleShowMode = mode
                    break
                }
            }
            setGuideShowMode(mGuideShowMode)
            setHandleShowMode(mHandleShowMode)
            mHandleSize = ta.getDimensionPixelSize(
                R.styleable.CropImageView_handleSize,
                (HANDLE_SIZE_IN_DP * mDensity).toInt()
            )
            mTouchPadding = ta.getDimensionPixelSize(R.styleable.CropImageView_touchPadding, 0)
            mMinFrameSize = ta.getDimensionPixelSize(
                R.styleable.CropImageView_minFrameSize,
                (MIN_FRAME_SIZE_IN_DP * mDensity).toInt()
            ).toFloat()
            mFrameStrokeWeight = ta.getDimensionPixelSize(
                R.styleable.CropImageView_frameStrokeWeight,
                (FRAME_STROKE_WEIGHT_IN_DP * mDensity).toInt()
            ).toFloat()
            mGuideStrokeWeight = ta.getDimensionPixelSize(
                R.styleable.CropImageView_guideStrokeWeight,
                (GUIDE_STROKE_WEIGHT_IN_DP * mDensity).toInt()
            ).toFloat()
            mIsCropEnabled = ta.getBoolean(R.styleable.CropImageView_cropEnabled, true)
        } catch (e: Exception) {
            e.printStackTrace() //
        } finally {
            ta.recycle()
        }
    }

    // Drawing method //////////////////////////////////////////////////////////////////////////////
    private fun drawEditFrame(canvas: Canvas) {
        if (!mIsCropEnabled) return
        mPaintTransparent.isFilterBitmap = true
        mPaintTransparent.color = mOverlayColor
        mPaintTransparent.style = Paint.Style.FILL
        canvas.drawRect(
            mImageRect!!.left, mImageRect!!.top, mImageRect!!.right, mFrameRect!!.top,
            mPaintTransparent
        )
        canvas.drawRect(
            mImageRect!!.left, mFrameRect!!.bottom, mImageRect!!.right, mImageRect!!.bottom,
            mPaintTransparent
        )
        canvas.drawRect(
            mImageRect!!.left, mFrameRect!!.top, mFrameRect!!.left, mFrameRect!!.bottom,
            mPaintTransparent
        )
        canvas.drawRect(
            mFrameRect!!.right, mFrameRect!!.top, mImageRect!!.right, mFrameRect!!.bottom,
            mPaintTransparent
        )
        mPaintFrame.isAntiAlias = true
        mPaintFrame.isFilterBitmap = true
        mPaintFrame.style = Paint.Style.STROKE
        mPaintFrame.color = mFrameColor
        mPaintFrame.strokeWidth = mFrameStrokeWeight
        canvas.drawRect(
            mFrameRect!!.left, mFrameRect!!.top, mFrameRect!!.right, mFrameRect!!.bottom,
            mPaintFrame
        )
        if (mShowGuide) {
            mPaintFrame.color = mGuideColor
            mPaintFrame.strokeWidth = mGuideStrokeWeight
            val h1 = mFrameRect!!.left + (mFrameRect!!.right - mFrameRect!!.left) / 3.0f
            val h2 = mFrameRect!!.right - (mFrameRect!!.right - mFrameRect!!.left) / 3.0f
            val v1 = mFrameRect!!.top + (mFrameRect!!.bottom - mFrameRect!!.top) / 3.0f
            val v2 = mFrameRect!!.bottom - (mFrameRect!!.bottom - mFrameRect!!.top) / 3.0f
            canvas.drawLine(h1, mFrameRect!!.top, h1, mFrameRect!!.bottom, mPaintFrame)
            canvas.drawLine(h2, mFrameRect!!.top, h2, mFrameRect!!.bottom, mPaintFrame)
            canvas.drawLine(mFrameRect!!.left, v1, mFrameRect!!.right, v1, mPaintFrame)
            canvas.drawLine(mFrameRect!!.left, v2, mFrameRect!!.right, v2, mPaintFrame)
        }
        if (mShowHandle) {
            mPaintFrame.style = Paint.Style.FILL
            mPaintFrame.color = mHandleColor
            canvas.drawCircle(
                mFrameRect!!.left,
                mFrameRect!!.top,
                mHandleSize.toFloat(),
                mPaintFrame
            )
            canvas.drawCircle(
                mFrameRect!!.right,
                mFrameRect!!.top,
                mHandleSize.toFloat(),
                mPaintFrame
            )
            canvas.drawCircle(
                mFrameRect!!.left,
                mFrameRect!!.bottom,
                mHandleSize.toFloat(),
                mPaintFrame
            )
            canvas.drawCircle(
                mFrameRect!!.right,
                mFrameRect!!.bottom,
                mHandleSize.toFloat(),
                mPaintFrame
            )
        }
    }

    private fun setMatrix() {
        mMatrix!!.reset()
        mMatrix!!.setTranslate(mCenter.x - mImgWidth * 0.5f, mCenter.y - mImgHeight * 0.5f)
        mMatrix!!.postScale(mScale, mScale, mCenter.x, mCenter.y)
        mMatrix!!.postRotate(mAngle, mCenter.x, mCenter.y)
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
        var scale = 1.0f
        if (imgRatio >= viewRatio) {
            scale = w / imgW
        } else if (imgRatio < viewRatio) {
            scale = h / imgH
        }
        setCenter(PointF(paddingLeft + w * 0.5f, paddingTop + h * 0.5f))
        setScale(scale)
        initCropFrame()
        adjustRatio()
        mIsInitialized = true
    }

    private fun initCropFrame() {
        setMatrix()
        val arrayOfFloat = FloatArray(8)
        arrayOfFloat[0] = 0.0f
        arrayOfFloat[1] = 0.0f
        arrayOfFloat[2] = 0.0f
        arrayOfFloat[3] = mImgHeight
        arrayOfFloat[4] = mImgWidth
        arrayOfFloat[5] = 0.0f
        arrayOfFloat[6] = mImgWidth
        arrayOfFloat[7] = mImgHeight
        mMatrix!!.mapPoints(arrayOfFloat)
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
        return if (!mIsCropEnabled) false else when (event.action) {
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
                onUp(event)
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

    private fun onUp(e: MotionEvent) {
        Timber.d(e.toString())
        if (mGuideShowMode == ShowMode.SHOW_ON_TOUCH) mShowGuide = false
        if (mHandleShowMode == ShowMode.SHOW_ON_TOUCH) mShowHandle = false
        mTouchArea = TouchArea.OUT_OF_BOUNDS
        invalidate()
    }

    private fun onCancel() {
        mTouchArea = TouchArea.OUT_OF_BOUNDS
        invalidate()
    }

    // Hit test ////////////////////////////////////////////////////////////////////////////////////
    private fun checkTouchArea(x: Float, y: Float) {
        if (isInsideCornerLeftTop(x, y)) {
            mTouchArea = TouchArea.LEFT_TOP
            if (mHandleShowMode == ShowMode.SHOW_ON_TOUCH) mShowHandle = true
            if (mGuideShowMode == ShowMode.SHOW_ON_TOUCH) mShowGuide = true
            return
        }
        if (isInsideCornerRightTop(x, y)) {
            mTouchArea = TouchArea.RIGHT_TOP
            if (mHandleShowMode == ShowMode.SHOW_ON_TOUCH) mShowHandle = true
            if (mGuideShowMode == ShowMode.SHOW_ON_TOUCH) mShowGuide = true
            return
        }
        if (isInsideCornerLeftBottom(x, y)) {
            mTouchArea = TouchArea.LEFT_BOTTOM
            if (mHandleShowMode == ShowMode.SHOW_ON_TOUCH) mShowHandle = true
            if (mGuideShowMode == ShowMode.SHOW_ON_TOUCH) mShowGuide = true
            return
        }
        if (isInsideCornerRightBottom(x, y)) {
            mTouchArea = TouchArea.RIGHT_BOTTOM
            if (mHandleShowMode == ShowMode.SHOW_ON_TOUCH) mShowHandle = true
            if (mGuideShowMode == ShowMode.SHOW_ON_TOUCH) mShowGuide = true
            return
        }
        if (isInsideFrame(x, y)) {
            if (mGuideShowMode == ShowMode.SHOW_ON_TOUCH) mShowGuide = true
            mTouchArea = TouchArea.CENTER
            return
        }
        mTouchArea = TouchArea.OUT_OF_BOUNDS
    }

    private fun isInsideFrame(x: Float, y: Float): Boolean {
        if (mFrameRect!!.left <= x && mFrameRect!!.right >= x &&
            (mFrameRect!!.top <= y && mFrameRect!!.bottom >= y)
        ) {
            mTouchArea = TouchArea.CENTER
            return true
        }
        return false
    }

    private fun isInsideCornerLeftTop(x: Float, y: Float): Boolean {
        val dx = x - mFrameRect!!.left
        val dy = y - mFrameRect!!.top
        val d = dx * dx + dy * dy
        return sq(mHandleSize + mTouchPadding.toFloat()) >= d
    }

    private fun isInsideCornerRightTop(x: Float, y: Float): Boolean {
        val dx = x - mFrameRect!!.right
        val dy = y - mFrameRect!!.top
        val d = dx * dx + dy * dy
        return sq(mHandleSize + mTouchPadding.toFloat()) >= d
    }

    private fun isInsideCornerLeftBottom(x: Float, y: Float): Boolean {
        val dx = x - mFrameRect!!.left
        val dy = y - mFrameRect!!.bottom
        val d = dx * dx + dy * dy
        return sq(mHandleSize + mTouchPadding.toFloat()) >= d
    }

    private fun isInsideCornerRightBottom(x: Float, y: Float): Boolean {
        val dx = x - mFrameRect!!.right
        val dy = y - mFrameRect!!.bottom
        val d = dx * dx + dy * dy
        return sq(mHandleSize + mTouchPadding.toFloat()) >= d
    }

    // Adjust frame ////////////////////////////////////////////////////////////////////////////////
    private fun moveFrame(x: Float, y: Float) {
        mFrameRect!!.left += x
        mFrameRect!!.right += x
        mFrameRect!!.top += y
        mFrameRect!!.bottom += y
        checkMoveBounds()
    }

    private fun moveHandleLT(diffX: Float, diffY: Float) {
        if (mCropMode == CropMode.RATIO_FREE) {
            mFrameRect!!.left += diffX
            mFrameRect!!.top += diffY
            if (isWidthTooSmall) {
                val offsetX = mMinFrameSize - frameW
                mFrameRect!!.left -= offsetX
            }
            if (isHeightTooSmall) {
                val offsetY = mMinFrameSize - frameH
                mFrameRect!!.top -= offsetY
            }
            checkScaleBounds()
        } else {
            val dy = diffX * ratioY / ratioX
            mFrameRect!!.left += diffX
            mFrameRect!!.top += dy
            if (isWidthTooSmall) {
                val offsetX = mMinFrameSize - frameW
                mFrameRect!!.left -= offsetX
                val offsetY = offsetX * ratioY / ratioX
                mFrameRect!!.top -= offsetY
            }
            if (isHeightTooSmall) {
                val offsetY = mMinFrameSize - frameH
                mFrameRect!!.top -= offsetY
                val offsetX = offsetY * ratioX / ratioY
                mFrameRect!!.left -= offsetX
            }
            var ox: Float
            var oy: Float
            if (!isInsideHorizontal(mFrameRect!!.left)) {
                ox = mImageRect!!.left - mFrameRect!!.left
                mFrameRect!!.left += ox
                oy = ox * ratioY / ratioX
                mFrameRect!!.top += oy
            }
            if (!isInsideVertical(mFrameRect!!.top)) {
                oy = mImageRect!!.top - mFrameRect!!.top
                mFrameRect!!.top += oy
                ox = oy * ratioX / ratioY
                mFrameRect!!.left += ox
            }
        }
    }

    private fun moveHandleRT(diffX: Float, diffY: Float) {
        if (mCropMode == CropMode.RATIO_FREE) {
            mFrameRect!!.right += diffX
            mFrameRect!!.top += diffY
            if (isWidthTooSmall) {
                val offsetX = mMinFrameSize - frameW
                mFrameRect!!.right += offsetX
            }
            if (isHeightTooSmall) {
                val offsetY = mMinFrameSize - frameH
                mFrameRect!!.top -= offsetY
            }
            checkScaleBounds()
        } else {
            val dy = diffX * ratioY / ratioX
            mFrameRect!!.right += diffX
            mFrameRect!!.top -= dy
            if (isWidthTooSmall) {
                val offsetX = mMinFrameSize - frameW
                mFrameRect!!.right += offsetX
                val offsetY = offsetX * ratioY / ratioX
                mFrameRect!!.top -= offsetY
            }
            if (isHeightTooSmall) {
                val offsetY = mMinFrameSize - frameH
                mFrameRect!!.top -= offsetY
                val offsetX = offsetY * ratioX / ratioY
                mFrameRect!!.right += offsetX
            }
            var ox: Float
            var oy: Float
            if (!isInsideHorizontal(mFrameRect!!.right)) {
                ox = mFrameRect!!.right - mImageRect!!.right
                mFrameRect!!.right -= ox
                oy = ox * ratioY / ratioX
                mFrameRect!!.top += oy
            }
            if (!isInsideVertical(mFrameRect!!.top)) {
                oy = mImageRect!!.top - mFrameRect!!.top
                mFrameRect!!.top += oy
                ox = oy * ratioX / ratioY
                mFrameRect!!.right -= ox
            }
        }
    }

    private fun moveHandleLB(diffX: Float, diffY: Float) {
        if (mCropMode == CropMode.RATIO_FREE) {
            mFrameRect!!.left += diffX
            mFrameRect!!.bottom += diffY
            if (isWidthTooSmall) {
                val offsetX = mMinFrameSize - frameW
                mFrameRect!!.left -= offsetX
            }
            if (isHeightTooSmall) {
                val offsetY = mMinFrameSize - frameH
                mFrameRect!!.bottom += offsetY
            }
            checkScaleBounds()
        } else {
            val dy = diffX * ratioY / ratioX
            mFrameRect!!.left += diffX
            mFrameRect!!.bottom -= dy
            if (isWidthTooSmall) {
                val offsetX = mMinFrameSize - frameW
                mFrameRect!!.left -= offsetX
                val offsetY = offsetX * ratioY / ratioX
                mFrameRect!!.bottom += offsetY
            }
            if (isHeightTooSmall) {
                val offsetY = mMinFrameSize - frameH
                mFrameRect!!.bottom += offsetY
                val offsetX = offsetY * ratioX / ratioY
                mFrameRect!!.left -= offsetX
            }
            var ox: Float
            var oy: Float
            if (!isInsideHorizontal(mFrameRect!!.left)) {
                ox = mImageRect!!.left - mFrameRect!!.left
                mFrameRect!!.left += ox
                oy = ox * ratioY / ratioX
                mFrameRect!!.bottom -= oy
            }
            if (!isInsideVertical(mFrameRect!!.bottom)) {
                oy = mFrameRect!!.bottom - mImageRect!!.bottom
                mFrameRect!!.bottom -= oy
                ox = oy * ratioX / ratioY
                mFrameRect!!.left += ox
            }
        }
    }

    private fun moveHandleRB(diffX: Float, diffY: Float) {
        if (mCropMode == CropMode.RATIO_FREE) {
            mFrameRect!!.right += diffX
            mFrameRect!!.bottom += diffY
            if (isWidthTooSmall) {
                val offsetX = mMinFrameSize - frameW
                mFrameRect!!.right += offsetX
            }
            if (isHeightTooSmall) {
                val offsetY = mMinFrameSize - frameH
                mFrameRect!!.bottom += offsetY
            }
            checkScaleBounds()
        } else {
            val dy = diffX * ratioY / ratioX
            mFrameRect!!.right += diffX
            mFrameRect!!.bottom += dy
            if (isWidthTooSmall) {
                val offsetX = mMinFrameSize - frameW
                mFrameRect!!.right += offsetX
                val offsetY = offsetX * ratioY / ratioX
                mFrameRect!!.bottom += offsetY
            }
            if (isHeightTooSmall) {
                val offsetY = mMinFrameSize - frameH
                mFrameRect!!.bottom += offsetY
                val offsetX = offsetY * ratioX / ratioY
                mFrameRect!!.right += offsetX
            }
            var ox: Float
            var oy: Float
            if (!isInsideHorizontal(mFrameRect!!.right)) {
                ox = mFrameRect!!.right - mImageRect!!.right
                mFrameRect!!.right -= ox
                oy = ox * ratioY / ratioX
                mFrameRect!!.bottom -= oy
            }
            if (!isInsideVertical(mFrameRect!!.bottom)) {
                oy = mFrameRect!!.bottom - mImageRect!!.bottom
                mFrameRect!!.bottom -= oy
                ox = oy * ratioX / ratioY
                mFrameRect!!.right -= ox
            }
        }
    }

    // Frame position correction ///////////////////////////////////////////////////////////////////
    private fun checkScaleBounds() {
        val lDiff = mFrameRect!!.left - mImageRect!!.left
        val rDiff = mFrameRect!!.right - mImageRect!!.right
        val tDiff = mFrameRect!!.top - mImageRect!!.top
        val bDiff = mFrameRect!!.bottom - mImageRect!!.bottom
        if (lDiff < 0) {
            mFrameRect!!.left -= lDiff
        }
        if (rDiff > 0) {
            mFrameRect!!.right -= rDiff
        }
        if (tDiff < 0) {
            mFrameRect!!.top -= tDiff
        }
        if (bDiff > 0) {
            mFrameRect!!.bottom -= bDiff
        }
    }

    private fun checkMoveBounds() {
        var diff = mFrameRect!!.left - mImageRect!!.left
        if (diff < 0) {
            mFrameRect!!.left -= diff
            mFrameRect!!.right -= diff
        }
        diff = mFrameRect!!.right - mImageRect!!.right
        if (diff > 0) {
            mFrameRect!!.left -= diff
            mFrameRect!!.right -= diff
        }
        diff = mFrameRect!!.top - mImageRect!!.top
        if (diff < 0) {
            mFrameRect!!.top -= diff
            mFrameRect!!.bottom -= diff
        }
        diff = mFrameRect!!.bottom - mImageRect!!.bottom
        if (diff > 0) {
            mFrameRect!!.top -= diff
            mFrameRect!!.bottom -= diff
        }
    }

    private fun isInsideHorizontal(x: Float): Boolean {
        return mImageRect!!.left <= x && mImageRect!!.right >= x
    }

    private fun isInsideVertical(y: Float): Boolean {
        return mImageRect!!.top <= y && mImageRect!!.bottom >= y
    }

    private val isWidthTooSmall: Boolean
        get() = frameW < mMinFrameSize

    private val isHeightTooSmall: Boolean
        get() = frameH < mMinFrameSize

    // Frame aspect ratio correction ///////////////////////////////////////////////////////////////
    private fun adjustRatio() {
        if (mImageRect == null) return
        val imgW = mImageRect!!.right - mImageRect!!.left
        val imgH = mImageRect!!.bottom - mImageRect!!.top
        val frameW = getRatioX(imgW)
        val frameH = getRatioY(imgH)
        val imgRatio = imgW / imgH
        val frameRatio = frameW / frameH
        var l = mImageRect!!.left
        var t = mImageRect!!.top
        var r = mImageRect!!.right
        var b = mImageRect!!.bottom
        if (frameRatio >= imgRatio) {
            l = mImageRect!!.left
            r = mImageRect!!.right
            val hy = (mImageRect!!.top + mImageRect!!.bottom) * 0.5f
            val hh = imgW / frameRatio * 0.5f
            t = hy - hh
            b = hy + hh
        } else if (frameRatio < imgRatio) {
            t = mImageRect!!.top
            b = mImageRect!!.bottom
            val hx = (mImageRect!!.left + mImageRect!!.right) * 0.5f
            val hw = imgH * frameRatio * 0.5f
            l = hx - hw
            r = hx + hw
        }
        val w = r - l
        val h = b - t
        mFrameRect = RectF(l + w / 8, t + h / 8, r - w / 8, b - h / 8)
        invalidate()
    }

    private fun getRatioX(w: Float): Float {
        return when (mCropMode) {
            CropMode.RATIO_FIT_IMAGE -> mImgWidth
            CropMode.RATIO_FREE -> w
            CropMode.RATIO_4_3 -> 4.0f
            CropMode.RATIO_3_4 -> 3.0f
            CropMode.RATIO_16_9 -> 16.0f
            CropMode.RATIO_9_16 -> 9.0f
            CropMode.RATIO_1_1 -> 1.0f
            CropMode.RATIO_CUSTOM -> mCustomRatio.x
            else -> w
        }
    }

    private fun getRatioY(h: Float): Float {
        return when (mCropMode) {
            CropMode.RATIO_FIT_IMAGE -> mImgHeight
            CropMode.RATIO_FREE -> h
            CropMode.RATIO_4_3 -> 3.0f
            CropMode.RATIO_3_4 -> 4.0f
            CropMode.RATIO_16_9 -> 9.0f
            CropMode.RATIO_9_16 -> 16.0f
            CropMode.RATIO_1_1 -> 1.0f
            CropMode.RATIO_CUSTOM -> mCustomRatio.y
            else -> h
        }
    }

    private val ratioX: Float
        get() = when (mCropMode) {
            CropMode.RATIO_FIT_IMAGE -> mImgWidth
            CropMode.RATIO_4_3 -> 4.0f
            CropMode.RATIO_3_4 -> 3.0f
            CropMode.RATIO_16_9 -> 16.0f
            CropMode.RATIO_9_16 -> 9.0f
            CropMode.RATIO_1_1 -> 1.0f
            CropMode.RATIO_CUSTOM -> mCustomRatio.x
            else -> 1.0f
        }

    private val ratioY: Float
        get() = when (mCropMode) {
            CropMode.RATIO_FIT_IMAGE -> mImgHeight
            CropMode.RATIO_4_3 -> 3.0f
            CropMode.RATIO_3_4 -> 4.0f
            CropMode.RATIO_16_9 -> 9.0f
            CropMode.RATIO_9_16 -> 16.0f
            CropMode.RATIO_1_1 -> 1.0f
            CropMode.RATIO_CUSTOM -> mCustomRatio.y
            else -> 1.0f
        }

    // Utility methods /////////////////////////////////////////////////////////////////////////////
    private val density: Float
        get() {
            val displayMetrics = DisplayMetrics()
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
                .getMetrics(displayMetrics)
            return displayMetrics.density
        }

    private fun sq(value: Float): Float {
        return value * value
    }
    // Public methods //////////////////////////////////////////////////////////////////////////////

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
            var x = 0
            var y = 0
            var w = 0
            var h = 0
            if (imageBitmap != null) {
                val l = (mFrameRect!!.left / mScale).toInt()
                val t = (mFrameRect!!.top / mScale).toInt()
                val r = (mFrameRect!!.right / mScale).toInt()
                val b = (mFrameRect!!.bottom / mScale).toInt()
                x = l - (mImageRect!!.left / mScale).toInt()
                y = t - (mImageRect!!.top / mScale).toInt()
                w = r - l
                h = b - t
            }
            return Bitmap.createBitmap(imageBitmap!!, x, y, w, h, null, false)
        }

    /**
     * Set crop mode
     *
     * @param mode crop mode
     */
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
    fun setOverlayColor(overlayColor: Int) {
        mOverlayColor = overlayColor
        invalidate()
    }

    /**
     * Set crop frame color
     *
     * @param frameColor color resId or color int(ex. 0xFFFFFFFF)
     */
    fun setFrameColor(frameColor: Int) {
        mFrameColor = frameColor
        invalidate()
    }

    /**
     * Set handle color
     *
     * @param handleColor color resId or color int(ex. 0xFFFFFFFF)
     */
    fun setHandleColor(handleColor: Int) {
        mHandleColor = handleColor
        invalidate()
    }

    /**
     * Set guide color
     *
     * @param guideColor color resId or color int(ex. 0xFFFFFFFF)
     */
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
    fun setMinFrameSizeInDp(minDp: Int) {
        mMinFrameSize = minDp * density
    }

    /**
     * Set handle radius in density-independent pixels.
     *
     * @param handleDp handle radius in density-independent pixels
     */
    fun setHandleSizeInDp(handleDp: Int) {
        mHandleSize = (handleDp * density).toInt()
    }

    /**
     * Set crop frame handle touch padding(touch area) in density-independent pixels.
     *
     *
     * handle touch area : a circle of radius R.(R = handle size + touch padding)
     *
     * @param paddingDp crop frame handle touch padding(touch area) in density-independent pixels
     */
    fun setTouchPaddingInDp(paddingDp: Int) {
        mTouchPadding = (paddingDp * density).toInt()
    }

    /**
     * Set guideline show mode.
     * (SHOW_ALWAYS/NOT_SHOW/SHOW_ON_TOUCH)
     *
     * @param mode guideline show mode
     */
    fun setGuideShowMode(mode: ShowMode?) {
        mGuideShowMode = mode
        mShowGuide = when (mode) {
            ShowMode.SHOW_ALWAYS -> true
            ShowMode.NOT_SHOW, ShowMode.SHOW_ON_TOUCH -> false
            else -> false
        }
        invalidate()
    }

    /**
     * Set handle show mode.
     * (SHOW_ALWAYS/NOT_SHOW/SHOW_ON_TOUCH)
     *
     * @param mode handle show mode
     */
    fun setHandleShowMode(mode: ShowMode?) {
        mHandleShowMode = mode
        mShowHandle = when (mode) {
            ShowMode.SHOW_ALWAYS -> true
            ShowMode.NOT_SHOW, ShowMode.SHOW_ON_TOUCH -> false
            else -> false
        }
        invalidate()
    }

    /**
     * Set frame stroke weight in density-independent pixels.
     *
     * @param weightDp frame stroke weight in density-independent pixels.
     */
    fun setFrameStrokeWeightInDp(weightDp: Int) {
        mFrameStrokeWeight = weightDp * density
        invalidate()
    }

    /**
     * Set guideline stroke weight in density-independent pixels.
     *
     * @param weightDp guideline stroke weight in density-independent pixels.
     */
    fun setGuideStrokeWeightInDp(weightDp: Int) {
        mGuideStrokeWeight = weightDp * density
        invalidate()
    }

    /**
     * Set whether to show crop frame.
     *
     * @param enabled should show crop frame?
     */
    fun setCropEnabled(enabled: Boolean) {
        mIsCropEnabled = enabled
        invalidate()
    }

    private fun setScale(mScale: Float) {
        this.mScale = mScale
    }

    private fun setCenter(mCenter: PointF) {
        this.mCenter = mCenter
    }

    private val frameW: Float
        get() = mFrameRect!!.right - mFrameRect!!.left

    private val frameH: Float
        get() = mFrameRect!!.bottom - mFrameRect!!.top

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
        var mode: CropMode? = null
        var backgroundColor = 0
        var overlayColor = 0
        var frameColor = 0
        var guideShowMode: ShowMode? = null
        var handleShowMode: ShowMode? = null
        var showGuide = false
        var showHandle = false
        var handleSize = 0
        var touchPadding = 0
        var minFrameSize = 0f
        var customRatioX = 0f
        var customRatioY = 0f
        var frameStrokeWeight = 0f
        var guideStrokeWeight = 0f
        var isCropEnabled = false
        var handleColor = 0
        var guideColor = 0

        constructor(superState: Parcelable) : super(superState)
        constructor(source: Parcel, loader: ClassLoader?) : super(source, loader) {
            image = source.readParcelable(Bitmap::class.java.classLoader)
            mode = source.readSerializable() as CropMode?
            backgroundColor = source.readInt()
            overlayColor = source.readInt()
            frameColor = source.readInt()
            guideShowMode = source.readSerializable() as ShowMode?
            handleShowMode = source.readSerializable() as ShowMode?
            showGuide = source.readBooleanCompat()
            showHandle = source.readBooleanCompat()
            handleSize = source.readInt()
            touchPadding = source.readInt()
            minFrameSize = source.readFloat()
            customRatioX = source.readFloat()
            customRatioY = source.readFloat()
            frameStrokeWeight = source.readFloat()
            guideStrokeWeight = source.readFloat()
            isCropEnabled = source.readBooleanCompat()
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
            out.writeBooleanCompat(showGuide)
            out.writeBooleanCompat(showHandle)
            out.writeInt(handleSize)
            out.writeInt(touchPadding)
            out.writeFloat(minFrameSize)
            out.writeFloat(customRatioX)
            out.writeFloat(customRatioY)
            out.writeFloat(frameStrokeWeight)
            out.writeFloat(guideStrokeWeight)
            out.writeBooleanCompat(isCropEnabled)
            out.writeInt(handleColor)
            out.writeInt(guideColor)
        }

        companion object {
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

    // Constructor /////////////////////////////////////////////////////////////////////////////////
    init {
        // TRANSPARENT = ResUtils.getColorInt(context, android.R.color.transparent);
        val mDensity = density
        mHandleSize = (mDensity * HANDLE_SIZE_IN_DP).toInt()
        mMinFrameSize = mDensity * MIN_FRAME_SIZE_IN_DP
        mFrameStrokeWeight = mDensity * FRAME_STROKE_WEIGHT_IN_DP
        mGuideStrokeWeight = mDensity * GUIDE_STROKE_WEIGHT_IN_DP
        mPaintFrame = Paint()
        mPaintTransparent = Paint()
        mPaintBitmap = Paint()
        mPaintBitmap.isFilterBitmap = true
        mMatrix = Matrix()
        mScale = 1.0f
        mBackgroundColor = TRANSPARENT
        mFrameColor = WHITE
        mOverlayColor = TRANSLUCENT_BLACK
        mHandleColor = WHITE
        mGuideColor = TRANSLUCENT_WHITE

        // handle Styleable
        handleStyleable(context, attrs, defStyle, mDensity)
    }
}
