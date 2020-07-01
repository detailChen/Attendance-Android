package com.attendance.bk.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.util.LruCache
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatDrawableManager
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.LogUtils
import com.attendance.bk.utils.DrawableUtil
import com.attendance.bk.utils.workerThreadChange
import com.attendance.bk.R
import com.attendance.bk.utils.Optional
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.disposables.Disposable
import kotlin.math.min

/**
 * ImageView， 支持改变内容图片为指定颜色、图片外边添加圆圈，图片圆形区域填充指定颜色(图片变为白色)
 *
 * @author CJL
 * @since 2016-01-04
 */
class BkImageView : AppCompatImageView {

    private var mState = 0
    private var mStrokeColor: Int = 0
    private var mFillColor: Int = 0
    private var mImageColor: Int = 0
    private var mStrokeWidth: Int = 0
    private var mCornerSize: Float = 0.0f

    private var mPath: Path = Path()
    private var mRect: RectF = RectF()
    private var mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var mDispose: Disposable? = null

    private val mTagKey = R.id.BkImageViewDrawableTag

    /**
     * @return NULL_COLOR 表示未设置颜色
     */
    val strokeColor: Int
        get() = if (hasFlag(SHIFT_STROKE)) mStrokeColor else NULL_COLOR

    /**
     * @return NULL_COLOR 表示未设置颜色
     */
    val fillColor: Int
        get() = if (hasFlag(SHIFT_FILL)) mFillColor else NULL_COLOR

    /**
     * @return NULL_COLOR 表示未设置颜色
     */
    /**
     * 更改图片颜色
     */
    var imageColor: Int
        get() = if (hasFlag(SHIFT_COLOR)) mImageColor else NULL_COLOR
        set(color) {
            mImageColor = color
            setFlag(SHIFT_COLOR, true)
            updateDrawableState()
        }

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.BkImageView)

        mState = mState or a.getInt(R.styleable.BkImageView_showFlags, 0)
        mImageColor = a.getColor(R.styleable.BkImageView_bkImageColor, NULL_COLOR)
        mStrokeColor = a.getColor(R.styleable.BkImageView_bkStrokeColor, NULL_COLOR)
        mFillColor = a.getColor(R.styleable.BkImageView_bkFillColor, NULL_COLOR)
        mCornerSize = a.getDimension(R.styleable.BkImageView_bkCornerSize, 0f)
        mStrokeWidth = a.getDimensionPixelSize(R.styleable.BkImageView_bkStrokeWidth, ConvertUtils.dp2px(1f))

        if (mCornerSize > 0) {
            setCornerSize(mCornerSize)
        }

        a.recycle()
        updateDrawableState()
    }


    @SuppressLint("CheckResult", "RestrictedApi")
    override fun setBackgroundResource(@DrawableRes resId: Int) {
        Observable
                .create(ObservableOnSubscribe<Optional<Drawable>> { emitter ->
                    val d = AppCompatDrawableManager.get().getDrawable(context, resId)
                    emitter.onNext(Optional.ofNullable<Drawable>(d))
                    emitter.onComplete()
                })
                .workerThreadChange()
                .subscribe { opDrawable ->
                    setBackgroundDrawable(opDrawable.opGet())
                    updateDrawableState()
                }
    }


    @SuppressLint("RestrictedApi")
    override fun setImageResource(resId: Int) {
        mDispose?.dispose()

        if (resId == 0) {
            super.setImageResource(0)
            return
        }
        val tagStr = "d$resId"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.setImageResource(resId)
            updateDrawableState()
            return
        } else {
            val cache = SimpleCache.getCacheByName(tagStr)
            if (cache != null) {
                setImageDrawable(cache)
                updateDrawableState()
                return
            }
        }

        setTag(mTagKey, tagStr)
        mDispose = Observable.create(ObservableOnSubscribe<Optional<Drawable>> { emitter ->
            run {
                val d = AppCompatDrawableManager.get().getDrawable(context, resId)
                emitter.onNext(Optional.ofNullable<Drawable>(d))
                emitter.onComplete()
            }
        }).workerThreadChange().subscribe { opDrawable ->
            run {
                if (tagStr != getTag(mTagKey)) {
                    LogUtils.e("setImageResource after change to other drawable")
                } else {
                    var drawable = opDrawable.opGet()
                    drawable = SimpleCache.cacheIfNecessary(tagStr, drawable)
                    setImageDrawable(drawable)
                    updateDrawableState()
                }
            }
        }
    }

    fun setImageName(name: String) {
        setImageName(name,null)
    }

    fun setImageName(name: String, color: String) {
        setImageName(name, getColorInt(color))
    }

    fun setImageName(name: String, color: Int) {
        val setter = State()
        setter.name(name)
        if (hasFlag(SHIFT_FILL)) {
            setter.fillColor(color)
        }
        if (hasFlag(SHIFT_STROKE)) {
            setter.strokeColor(color)
        }
        if (hasFlag(SHIFT_COLOR)) {
            setter.imageColor(color)
        }
        setImageState(setter)
    }

    @SuppressLint("CheckResult")
    private fun setImageName(name: String, afterGetDrawable: Runnable? = null) {
        mDispose?.dispose()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.setImageDrawable(DrawableUtil.getDrawableByName(name))
            if (afterGetDrawable != null) {
                afterGetDrawable.run()
            } else {
                updateDrawableState()
            }
            return
        } else {
            val cache = SimpleCache.getCacheByName(name)
            if (cache != null) {
                setImageDrawable(cache)
                if (afterGetDrawable != null) {
                    afterGetDrawable.run()
                } else {
                    updateDrawableState()
                }
                return
            }
        }
        val tagStr = "n$name"
        setTag(mTagKey, tagStr)

        mDispose = Observable
                .create(ObservableOnSubscribe<Optional<Drawable>> { emitter ->
                    val d = DrawableUtil.getDrawableByName(name)
                    emitter.onNext(Optional.ofNullable<Drawable>(d))
                    emitter.onComplete()
                })
                .workerThreadChange()
                .subscribe(
                        { opDrawable ->
                            if (tagStr != getTag(mTagKey)) {
                                LogUtils.e("setImageResource after change to other drawable")
                            } else {
                                var drawable = opDrawable.opGet()
                                drawable = SimpleCache.cacheIfNecessary(tagStr, drawable)
                                setImageDrawable(drawable)
                                updateDrawableState()
                            }
                        },
                        { throwable -> LogUtils.e("setImageName failed ! ->$name", throwable) });

    }

    fun setImageState(state: State) {
        val runnable: Runnable? = Runnable {
            reset()
            for ((key, value) in state.setter) {
                when (key) {
                    "strokeColor" -> setStroke(value as Int)
                    "imageColor" -> imageColor = value as Int
                    "fillColor" -> setFill(value as Int)
                    "strokeWidth" -> setStrokeWidth(value as Int)
                }
            }
        }

        val name = state.setter["nickname"] as String
        if (!TextUtils.isEmpty(name)) {
            setImageName(name, runnable)
        } else {
            runnable?.run()
        }
    }

    override fun onDetachedFromWindow() {
        mDispose?.dispose()

        super.onDetachedFromWindow()
    }

    /**
     * 清除所有特效
     */
    fun reset() {
        mState = 0
        updateDrawableState()
    }

    /**
     * 设置圆形边框颜色
     */
    fun setStroke(strokeColor: Int) {
        mStrokeColor = strokeColor
        setFlag(SHIFT_STROKE, true)
        updateDrawableState()
    }

    /**
     * 设置边框线粗细
     */
    fun setStrokeWidth(strokeWidth: Int) {
        mStrokeWidth = strokeWidth
        invalidate()
    }

    /**
     * 移除圆形边框
     */
    fun removeStroke() {
        if (hasFlag(SHIFT_STROKE)) {
            setFlag(SHIFT_STROKE, false)
            updateDrawableState()
        }
    }

    /**
     * 设置图片填充颜色
     *
     * @param changeImgColor 是否改变图片颜色
     */
    @JvmOverloads
    fun setFill(fillColor: Int, changeImgColor: Boolean = false) {
        mFillColor = fillColor
        setFlag(SHIFT_FILL, true)
        if (changeImgColor) {
            mImageColor = ContextCompat.getColor(context, R.color.text_primary)
            setFlag(SHIFT_COLOR, true)
        }
        updateDrawableState()
    }

    /**
     * 取消填充颜色
     */
    fun removeFill() {
        if (hasFlag(SHIFT_FILL)) {
            setFlag(SHIFT_FILL, false)
            updateDrawableState()
        }
    }

    /**
     * 移除图片颜色
     */
    fun removeImageColor() {
        if (hasFlag(SHIFT_COLOR)) {
            setFlag(SHIFT_COLOR, false)
            updateDrawableState()
        }
    }

    /**
     * 设置圆角
     *
     * @param cornerSize 圆角大小
     */
    fun setCornerSize(cornerSize: Float) {
        mCornerSize = cornerSize
        if (mCornerSize > 0) {
            setLayerType(LAYER_TYPE_SOFTWARE, null)
        } else {
            setLayerType(LAYER_TYPE_HARDWARE, null)
            postInvalidate()
        }
    }

    /**
     * 更新Drawable 的 ColorFilter值
     */
    fun updateDrawableState() {
        val d = drawable ?: return

        d.mutate()
        if (hasFlag(SHIFT_COLOR)) {
            d.setColorFilter(mImageColor, PorterDuff.Mode.SRC_IN)
        } else {
            d.clearColorFilter()
        }
    }

    private fun hasFlag(shift: Int): Boolean {
        return mState shr shift and 1 == 1
    }

    private fun setFlag(shift: Int, flag: Boolean) {
        mState = if (!flag) {
            mState and (1 shl shift).inv()
        } else {
            mState or (1 shl shift)
        }
    }

    override fun onDraw(canvas: Canvas) {
        val d = drawable
        if (mCornerSize > 0) {
            mPath.reset()
            mRect.set(0f, 0f, width.toFloat(), height.toFloat())
            mPath.addRoundRect(mRect, mCornerSize, mCornerSize, Path.Direction.CW)
            canvas.clipPath(mPath)
        }
        if (d == null || mState == 0) {
            super.onDraw(canvas)
            return
        }


        val r = min(width, height) / 2
        if (hasFlag(SHIFT_FILL)) { // 填充
            mPaint.color = mFillColor
            mPaint.style = Paint.Style.FILL
            canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), r.toFloat(), mPaint)
        }

        if (hasFlag(SHIFT_STROKE)) { // Stroke
            mPaint.color = mStrokeColor
            mPaint.style = Paint.Style.STROKE
            mPaint.strokeWidth = mStrokeWidth.toFloat()
            canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), r - mStrokeWidth / 2f, mPaint)
        }
        try {
            super.onDraw(canvas)
        } catch (e: Exception) {
            Log.e("---", "draw vector failed!" + getTag(mTagKey), e)
        }

    }

    class State {
        internal var setter: MutableMap<String, Any> = LinkedHashMap(5)

        fun name(name: String): State {
            setter["nickname"] = name
            return this
        }

        fun strokeColor(color: Int): State {
            setter["strokeColor"] = color
            return this
        }

        fun strokeColor(color: String): State {
            return strokeColor(getColorInt(color))
        }

        fun fillColor(color: Int): State {
            setter["fillColor"] = color
            return this
        }

        fun fillColor(color: String): State {
            return fillColor(getColorInt(color))
        }

        fun imageColor(color: Int): State {
            setter["imageColor"] = color
            return this
        }

        fun imageColor(color: String): State {
            return imageColor(getColorInt(color))
        }

        fun strokeWidth(width: Int): State {
            setter["strokeWidth"] = width
            return this
        }
    }

    private object SimpleCache {
        private const val MAX_CACHE_COUNT = 100
        private val vdCache = LruCache<String, Drawable.ConstantState>(MAX_CACHE_COUNT)

        /**
         * 缓存矢量图标
         */
        internal fun cacheIfNecessary(name: String, d: Drawable?): Drawable? {
            if (d != null && d is VectorDrawableCompat) {
                val cs = d.constantState
                if (cs != null) {
                    vdCache.put(name, cs)
                }
            }
            return d
        }

        internal fun getCacheByName(name: String): Drawable? {
            val d = vdCache.get(name)
            return d?.newDrawable()
        }

        internal fun onLowMemory() {
            if (vdCache.maxSize() == MAX_CACHE_COUNT) {
                vdCache.resize(MAX_CACHE_COUNT shr 1)
            }
        }
    }

    companion object {

        const val SHIFT_STROKE = 1
        const val SHIFT_FILL = 2
        const val SHIFT_COLOR = 3

        const val NULL_COLOR = Integer.MAX_VALUE

        private fun getColorInt(color: String): Int {
            var c = color
            if (TextUtils.isEmpty(c)) {
                return Color.BLACK
            }
            if (!c.startsWith("#")) {
                c = "#$c"
            }
            return try {
                Color.parseColor(c)
            } catch (e: Exception) {
                Color.BLACK
            }

        }

        fun onLowMemory() {
            SimpleCache.onLowMemory()
        }
    }
}
/**
 * 设置图片填充颜色 填充后图片颜色本身会变为白色
 */
