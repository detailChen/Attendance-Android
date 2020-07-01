package com.attendance.bk.view

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView

import com.blankj.utilcode.util.ConvertUtils

import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.attendance.bk.R


/**
 * 字母索引
 */
class LetterSlideBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatButton(context, attrs, defStyleAttr) {

    private var mPaint: Paint? = null
    private var mTouchLetter: TextView? = null
    private var mPopupWindow: PopupWindow? = null
    private var mSelectIndex = -1
    private val mAttachActivity: Activity = context as Activity
    private var mTouchLetterLayout: LinearLayout? = null
    private var mListener: OnTouchLetterListener? = null

    private var mWidth: Int = 0
    private var mHeight: Int = 0

    init {
        init()
    }

    private fun init() {
        mPaint = Paint()
        mPaint!!.isAntiAlias = true
        mTouchLetterLayout = LayoutInflater.from(mAttachActivity).inflate(R.layout.touch_letter_layout, null) as LinearLayout
        mTouchLetter = mTouchLetterLayout!!.findViewById(R.id.letter)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val interval = mHeight / LETTER_TEXT.size

        for (i in LETTER_TEXT.indices) {
            mPaint!!.textSize = ConvertUtils.dp2px(12f).toFloat()
            mPaint!!.color = ContextCompat.getColor(context, R.color.text_second)
            if (i == mSelectIndex) {
                mPaint!!.color = ContextCompat.getColor(context, R.color.text_primary)
                mPaint!!.isFakeBoldText = true
                mPaint!!.textSize = ConvertUtils.dp2px(14f).toFloat()
            }
            val xPos = mWidth / 2 - mPaint!!.measureText(LETTER_TEXT[i]) / 2 //计算字母的X坐标
            val yPos = (interval * i + interval).toFloat()//计算字母的Y坐标
            canvas.drawText(LETTER_TEXT[i], xPos, yPos, mPaint!!)
            mPaint!!.reset()
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val index = (event.y / height * LETTER_TEXT.size).toInt()
        if (index >= 0 && index < LETTER_TEXT.size) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    mSelectIndex = index
                    showLetter(LETTER_TEXT[mSelectIndex])
                    if (mListener != null) {
                        mListener!!.onTouchLetterListener(LETTER_TEXT[mSelectIndex])
                    }
                }
                MotionEvent.ACTION_UP -> {
                    hideLetter()
                    mSelectIndex = -1
                }
                MotionEvent.ACTION_MOVE -> if (mSelectIndex != index) {
                    mSelectIndex = index
                    showLetter(LETTER_TEXT[mSelectIndex])
                    if (mListener != null) {
                        mListener!!.onTouchLetterListener(LETTER_TEXT[mSelectIndex])
                    }
                }
            }
        } else {
            mSelectIndex = -1
            hideLetter()
        }
        invalidate()
        return true

    }

    /**
     * 显示弹出的字符串
     *
     * @param string
     */
    private fun showLetter(string: String) {
        if (mPopupWindow != null) {
            mTouchLetter!!.text = string
        } else {
            mPopupWindow = PopupWindow(mTouchLetterLayout, 200, 200, false)
            mPopupWindow!!.showAtLocation(mAttachActivity.window.decorView, Gravity.CENTER, 0, 0)
        }
        mTouchLetter!!.text = string

    }

    private fun hideLetter() {
        if (mPopupWindow != null) {
            mPopupWindow!!.dismiss()
            mPopupWindow = null
        }
    }

    interface OnTouchLetterListener {
        fun onTouchLetterListener(s: String)
    }

    fun setOnTouchLetterListener(listener: OnTouchLetterListener) {
        this.mListener = listener
    }

    companion object {


        private val LETTER_TEXT = arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")
    }
}
