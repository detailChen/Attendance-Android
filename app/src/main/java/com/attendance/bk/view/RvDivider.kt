package com.attendance.bk.view

import am.drawable.LinearDrawable.HORIZONTAL
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.LinearLayout
import android.widget.LinearLayout.VERTICAL
import androidx.annotation.ColorInt
import androidx.annotation.IntDef
import androidx.recyclerview.widget.RecyclerView

/**
 * 分割线
 * https://github.com/kymjs/RecyclerViewDemo/blob/master/RecyclerViewDemo/recycler/src/main/java/com/kymjs/recycler/Divider.java
 *
 */
class RvDivider @JvmOverloads constructor(@Orientation orientation: Int = VERTICAL, @ColorInt color: Int = DEFAULT_COLOR) : RecyclerView.ItemDecoration() {

    @Orientation
    private var mOrientation: Int = 0
    private val mDivider: ColorDrawable = ColorDrawable(color)
    private var leftMargin: Int = 0
    private var rightMargin: Int = 0
    private var topMargin: Int = 0
    private var bottomMargin: Int = 0
    private var width: Int = 0
    private var height: Int = 0
    private var mSkipLast = false
    private var mSkipFirst = false

    @IntDef(HORIZONTAL, VERTICAL)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class Orientation


    init {
        width = mDivider.intrinsicWidth
        height = mDivider.intrinsicHeight
        setOrientation(orientation)
        if (orientation == VERTICAL) {
            height = 1
        } else {
            width = 1
        }
    }

    fun setOrientation(@Orientation orientation: Int) {
        mOrientation = orientation
    }

    fun setMargin(left: Int, top: Int, right: Int, bottom: Int) {
        this.leftMargin = left
        this.topMargin = top
        this.rightMargin = right
        this.bottomMargin = bottom
    }

    fun setHeight(height:Int){
        this.height = height
    }

    fun setColor(@ColorInt color: Int) {
        mDivider.color = color
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(canvas, parent, state)
        if (mOrientation == LinearLayout.HORIZONTAL) {
            drawHorizontal(canvas, parent)
        } else {
            drawVertical(canvas, parent)
        }
    }

    private fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {
        val top = parent.paddingTop + topMargin
        val bottom = parent.height - parent.paddingBottom - bottomMargin

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            if (i == parent.adapter!!.itemCount - 1 && mSkipLast) {
                continue
            }
            if (i == 0 && mSkipFirst) {
                continue
            }
            val child = parent.getChildAt(i)
            val params = child
                    .layoutParams as RecyclerView.LayoutParams
            val left = child.right + params.rightMargin + leftMargin
            val right = left + width
            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(canvas)
        }
    }

    private fun drawVertical(c: Canvas, parent: RecyclerView) {
        val left = parent.paddingLeft + leftMargin
        val right = parent.width - parent.paddingRight - rightMargin

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            if (i == parent.adapter!!.itemCount - 1 && mSkipLast) {
                continue
            }
            if (i == 0 && mSkipFirst) {
                continue
            }
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin + topMargin
            val bottom = top + height
            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        if (parent.getChildAdapterPosition(view) == parent.adapter!!.itemCount - 1 && mSkipLast) {
            return
        }
        if (parent.getChildAdapterPosition(view) == 0 && mSkipFirst) {
            return
        }

        if (mOrientation == LinearLayout.HORIZONTAL) {
            outRect.set(0, 0, leftMargin + width + rightMargin, 0)
        } else {
            outRect.set(0, 0, 0, topMargin + height + bottomMargin)
        }
    }

    fun skipLastDivider() {
        mSkipLast = true
    }

    fun skipFirstDivider() {
        mSkipFirst = true
    }

    companion object {
        private val DEFAULT_COLOR = Color.parseColor("#dddddd")
    }

}