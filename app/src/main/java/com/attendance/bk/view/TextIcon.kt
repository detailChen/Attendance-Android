package com.attendance.bk.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import com.blankj.utilcode.util.ConvertUtils
import kotlin.math.min

/**
 * Created by Chen xuJie on 2019/12/29.
 */
class TextIcon @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    private var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).also {
        it.style = Paint.Style.FILL
    }

    private var text: String? = null
    private var color: Int? = null
    private val rect: RectF = RectF()


    override fun onDraw(canvas: Canvas) {
        if (text.isNullOrEmpty() || color == null) {
            return
        }
        val w = width.toFloat()
        val h = height.toFloat()
        val radius = min(w, h) / 2
        paint.style = Paint.Style.STROKE

        canvas.drawCircle(w / 2, h / 2, radius, paint)

        rect.set(0f, 0f, w, h)
        paint.style = Paint.Style.FILL
        paint.textSize = ConvertUtils.dp2px(14f).toFloat()
        drawText2Rect(text!!, canvas, rect, paint)
    }

    /**
     * 在矩形中画文字
     *
     * @param text 文字
     * @param c    画布
     * @param r    文字在该矩形正中心
     * @param p    画笔
     */
    private fun drawText2Rect(text: String, c: Canvas, r: RectF, p: Paint) {
        if (TextUtils.isEmpty(text)) {
            return
        }
        val fm = p.fontMetrics
        val textW = p.measureText(text)
        val x = r.centerX() - textW / 2
        val y = r.top + (r.bottom - r.top - fm.bottom + fm.top) / 2 - fm.top
        // c.save();
        // c.clipRect(r);
        c.drawText(text, x, y, p)
        // c.restore();
    }


    fun setTextAndColor(text: String, color: Int) {
        this.text = text
        this.color = color
        paint.color = color
        invalidate()
    }

}