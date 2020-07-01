package com.attendance.bk.view.pieview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.annotation.FloatRange
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.ColorUtils
import com.attendance.bk.R
import com.attendance.bk.bean.PieViewListData
import java.text.DecimalFormat
import kotlin.math.*


/**
 * 饼图
 *
 */
class PieView : View {

    private var mDefaultColor: Int = 0

    private var mTextSize: Int = 0
    private var mTextColor: Int = 0
    private var mChartSizePercent: Float = 0.0f
    private var mChartStrokePercent: Float = 0.0f
    private var mChartLineBrokePercent: Float = 0.0f
    private var mChartTextLineLength: Float = 0.0f

    private var mChartProgress = 0f
    private val colors = intArrayOf(-0xb2b1, -0x85bb, -0x552ec, -0x7e387c, -0x7a5a01, -0x4c8015, -0x8a655)

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    // 第一个块起始角度
    private val mStartAngle = 30f
    private val mRectF = RectF()

    private val df = DecimalFormat("0.1%")

    private val mData = ArrayList<PieData>()

    private var mAnimator: ValueAnimator? = null


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
        val a = context.obtainStyledAttributes(attrs, R.styleable.PieView)
        mTextSize = a.getDimensionPixelSize(R.styleable.PieView_pvTextSize, 20)
        mTextColor = a.getColor(R.styleable.PieView_pvTextColor, Color.BLACK)

        mChartSizePercent = a.getFloat(R.styleable.PieView_pvChartPercent, 170f / 610)
        mChartStrokePercent = a.getFloat(R.styleable.PieView_pvChartStrokePercent, 81f / 610)
        mChartLineBrokePercent = a.getFloat(R.styleable.PieView_pvChartLineBreakPercent, 175f / 610)
        mChartProgress = a.getFloat(R.styleable.PieView_pvChartProgress, 100f)
        mChartTextLineLength = a.getDimensionPixelSize(R.styleable.PieView_pvChartTextLineLength, 10).toFloat()
        a.recycle()

        mDefaultColor = ContextCompat.getColor(context, R.color.text_second)
        mPaint.textAlign = Paint.Align.CENTER
        mPaint.textSize = mTextSize.toFloat()
    }


    fun updateData(formData: List<PieViewListData>?, anim: Boolean = false) {
        if (formData == null || formData.isEmpty()) {
            mData.clear()
            postInvalidate()
            return
        }
        val baseList = ArrayList<PieViewListData>()
        formData.filter { it.money != 0.0 }.forEach { baseList.add(it) }
        mData.clear()
        mData.addAll(PieDataHelper.readPieDataFromFormData(baseList))

        if (width > 0 && mData.size > 0) {
            findTextPlace()
        }

        if (anim) {
            post { startAnim() }
        } else {
            endAnim()
            invalidate()
        }
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        if (mData.size > 0) {
            findTextPlace()
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        val ts = min(width, height)
        val strokeSize = ts * mChartStrokePercent
        val chartR = ts * mChartSizePercent - strokeSize / 2

        val centerX = width / 2
        val centerY = height / 2

        if (width <= 0 || height <= 0) {
            mPaint.style = Paint.Style.STROKE
            mPaint.color = mDefaultColor
            mPaint.strokeWidth = strokeSize
            canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), chartR, mPaint)
            return
        }
        if (mChartProgress <= 0) {
            return
        }

        mRectF.set(centerX - chartR, centerY - chartR, centerX + chartR, centerY + chartR)

        var angle = mStartAngle
        val fullAngle = min(360f, mChartProgress)

        val size = mData.size
        var i = 0
        while (i < size) {
            val pd = mData[i]
            mPaint.style = Paint.Style.STROKE
            var color = pd.color
            if (color == -1) {
                color = colors[i % 7]
                pd.color = color
            }
            mPaint.color = color
            mPaint.strokeWidth = strokeSize
            val ang = fullAngle * pd.percent
            if (ang > 0) {
                canvas.drawArc(mRectF, angle, ang + 1, false, mPaint) // 此处不+1会出现间隙
            }
            if (fullAngle >= 360 && pd.name != null) {
                drawTypeText(canvas, mPaint, pd, (mChartProgress - 360) / 360)
            }
            angle += ang
            i++
        }
    }

    private fun startAnim() {
        mChartProgress = 0f
        if (mAnimator == null) {
            mAnimator = ValueAnimator.ofFloat(0f, 360f, 720f).setDuration(700)
            mAnimator?.interpolator = LinearInterpolator()
            mAnimator?.addUpdateListener { animation ->
                mChartProgress = animation.animatedValue as Float
                postInvalidate()
            }
        } else {
            mAnimator?.end()
        }
        mAnimator?.start()
        postInvalidate()
    }

    private fun endAnim() {
        mAnimator?.end()
        mChartProgress = 720f
    }

    fun setAnimProgress(@FloatRange(from = 0.0, to = 720.0) progress: Float) {
        this.mChartProgress = progress
        invalidate()
    }

    override fun onDetachedFromWindow() {
        mAnimator?.end()
        super.onDetachedFromWindow()
    }

    /**
     * 画连线与文字
     */
    private fun drawTypeText(canvas: Canvas, mPaint: Paint, pieData: PieData, progress: Float) {
        var p = progress
        mPaint.style = Paint.Style.STROKE
        mPaint.color = pieData.color
        mPaint.strokeWidth = 1.5f

        if (p <= 0.33f) { // 画连线进度
            p *= 3
            val x = pieData.p1x + (pieData.p2x - pieData.p1x) * p
            val y = pieData.p1y + (pieData.p2y - pieData.p1y) * p

            canvas.drawLine(pieData.p1x, pieData.p1y, x, y, mPaint)
            return
        } else if (p < 0.66f) {
            canvas.drawLine(pieData.p1x, pieData.p1y, pieData.p2x, pieData.p2y, mPaint)

            p = (p - 0.33f) * 3

            val x = pieData.p2x + (pieData.p3x - pieData.p2x) * p
            val y = pieData.p2y + (pieData.p3y - pieData.p2y) * p

            canvas.drawLine(pieData.p2x, pieData.p2y, x, y, mPaint)
            return
        }

        canvas.drawLine(pieData.p1x, pieData.p1y, pieData.p2x, pieData.p2y, mPaint)
        canvas.drawLine(pieData.p2x, pieData.p2y, pieData.p3x, pieData.p3y, mPaint)

        p = min((p - 0.66f) * 3, 1f)
        mPaint.alpha = (255 * p).toInt()
        mPaint.style = Paint.Style.FILL
        mPaint.color = ColorUtils.getColor(R.color.text_second)
        mPaint.textAlign = if (pieData.p3x <= width / 2f) Paint.Align.RIGHT else Paint.Align.LEFT
        val text = pieData.name + " " + df.format(pieData.percent.toDouble())
        canvas.drawText(text, pieData.p3x, pieData.p3y, mPaint)
    }


    /**
     * 给数据寻找图片和文字的合适空间
     */
    private fun findTextPlace() {
        val centerX = width / 2f
        val centerY = height / 2f
        val count = mData.size

        val d = min(width, height).toFloat()
        val cr = d * mChartSizePercent
        val cblr = d * mChartLineBrokePercent

        var leftStartPos = -1
        var leftEndPos = count - 1

        var currentAngle = mStartAngle

        // 首先，找到各个item默认位置, 饼图分左右两部分，左边的文字全部靠左排列，右边的靠右排列，从上往下
        for (i in 0 until count) {
            val pd = mData[i]
            val cAngle = currentAngle + 180 * pd.percent
            currentAngle += 360 * pd.percent

            if (cAngle < 90 || cAngle > 270) { // 右边
                if (leftEndPos == count - 1 && cAngle > 270) {
                    leftEndPos = i - 1
                }
                val a = if (cAngle < 90) cAngle else cAngle - 360
                val sinA = sin(Math.toRadians(a.toDouble()))
                val cosA = cos(Math.toRadians(a.toDouble()))

                pd.p1x = (centerX + cr * cosA).toFloat()
                pd.p1y = (centerY + cr * sinA).toFloat()
                pd.p2x = (centerX + cblr * cosA).toFloat()
                pd.p2y = (centerY + cblr * sinA).toFloat()
                pd.p3x = pd.p2x + mChartTextLineLength
                pd.p3y = pd.p2y
            } else { // 左边
                if (leftStartPos == -1) {
                    leftStartPos = i
                }
                val sinA = sin(Math.toRadians((cAngle - 90).toDouble()))
                val cosA = cos(Math.toRadians((cAngle - 90).toDouble()))
                pd.p1x = (centerX - cr * sinA).toFloat()
                pd.p1y = (centerY + cr * cosA).toFloat()
                pd.p2x = (centerX - cblr * sinA).toFloat()
                pd.p2y = (centerY + cblr * cosA).toFloat()
                pd.p3x = pd.p2x - mChartTextLineLength
                pd.p3y = pd.p2y
            }
        }

        mPaint.textSize = mTextSize.toFloat()
        val fm = mPaint.fontMetrics
        val textH = fm.bottom - fm.top

        // 调整左边文字位置
        adjustPartXY(mData, leftStartPos, leftEndPos, cblr, textH, true)

        var rightCount = if (leftEndPos >= count - 1) leftStartPos else mData.size - leftEndPos - leftStartPos + 1
        rightCount = max(1, rightCount)
        // 调整右边文字位置，由于右边列表非连续，此处重新弄个列表出来
        val rightData = ArrayList<PieData>(rightCount)
        if (leftEndPos < count - 1) {
            rightData.addAll(mData.subList(leftEndPos + 1, count))
        }
        if (leftStartPos > 0) {
            rightData.addAll(mData.subList(0, leftStartPos))
        }
        if (rightData.size > 0) {
            rightData.reverse() // 反序一下，从下往上读（保持和左侧一致，共用一个方法）
            adjustPartXY(rightData, 0, rightData.size - 1, cblr, textH, false)
        }
    }


    private fun adjustPartXY(pieData: List<PieData>, st: Int, et: Int, cblr: Float, textH: Float, isLeft: Boolean) {
        val centerX = width / 2f
        val centerY = height / 2f

        //先从上往下排一次，若重叠，则向下挪动
        var curY = pieData[et].p3y
        val firstY = curY
        var space = 0f // 可利用的空白部分高度
        for (i in et - 1 downTo st) {
            val pd = pieData[i]

            if (pd.p3y - textH < curY) {
                pd.p3y = curY + textH
                pd.p2y = pd.p3y
            } else {
                space += pd.p3y - textH - curY
            }
            curY = pd.p3y
        }

        // 从上往下排一次后，由于不断向下挪动，可能底部非常高，因此处理下偏移值，利用空白空间，向上挪动
        var delta = 0f
        if (curY > height - textH / 2) {
            delta = (curY + firstY + space) / 2 - centerY
            //            delta = curY - getHeight(); // 方案2，可以减少连线穿过圆环概率，但是整个图上下不对称
        }

        if (delta > 0) {
            for (i in st..et) {
                val pd = pieData[i]

                if (i == st) {
                    pd.p3y -= delta
                } else {
                    if (curY - textH < pd.p3y) {
                        pd.p3y = curY - textH
                    }
                }
                pd.p2y = pd.p3y
                curY = pd.p3y
            }
        }

        // 经过上面2此调整后，现在垂直方向高度一致，但是上下偏移会导致文字叠在了饼图上，需要进一步调整X轴位置
        for (pd in pieData) {
            var offX = sqrt((cblr * cblr - (pd.p2y - centerY) * (pd.p2y - centerY)).toDouble()).toFloat()
            if (java.lang.Float.isNaN(offX)) {
                offX = 0f
            }
            if (isLeft) {
                pd.p2x = centerX - offX
                if (pd.p2x > pd.p1x) { // 避免出现夹角小于90的连线
                    pd.p2x = pd.p1x
                }
                pd.p3x = pd.p2x - mChartTextLineLength
            } else {
                pd.p2x = centerX + offX
                if (pd.p2x < pd.p1x) { // 避免出现夹角小于90的连线
                    pd.p2x = pd.p1x
                }
                pd.p3x = pd.p2x + mChartTextLineLength
            }
        }
    }

}
