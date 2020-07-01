package com.attendance.bk.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View


import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatEditText
import com.attendance.bk.R


/**
 * @anthor Chen Xujie
 * @since 2017/3/23
 */
class ClearEditText : AppCompatEditText, TextWatcher, View.OnFocusChangeListener {


    private var mHasFocus: Boolean = false
    private var mIsDownInRange = false
    private var mDeleteIcon: Drawable? = null
    private var mListener: OnClearListener? = null

    interface OnClearListener {
        fun onClear()
    }

    fun setOnClearListener(listener: OnClearListener) {
        mListener = listener
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
        val ta = context.obtainStyledAttributes(attrs, R.styleable.ClearEditText)
        var deleteIconSize = ta.getDimension(R.styleable.ClearEditText_deleteIconSize, 0f)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mDeleteIcon = ta.getDrawable(R.styleable.ClearEditText_deleteIcon)
        } else {
            val drawableRightId = ta.getResourceId(R.styleable.ClearEditText_deleteIcon, -1)
            if (drawableRightId != -1) {
                mDeleteIcon = AppCompatResources.getDrawable(context, drawableRightId)
            }
        }
        setCompoundDrawablesWithIntrinsicBounds(null, null, mDeleteIcon, null)
        ta.recycle()

        if (mDeleteIcon == null) {
            return
        }
        deleteIconSize = if (deleteIconSize == 0f) mDeleteIcon!!.intrinsicHeight.toFloat() else deleteIconSize
        mDeleteIcon!!.setBounds(0, 0, deleteIconSize.toInt(), deleteIconSize.toInt())

        setDeleteDrawableVisible(false)
        addTextChangedListener(this)
        onFocusChangeListener = this
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (compoundDrawables[2] != null) {
                mIsDownInRange = event.x >= width - totalPaddingRight
            }
        }

        if (event.action == MotionEvent.ACTION_UP) {
            if (mIsDownInRange) {
                if (compoundDrawables[2] != null) {
                    if (event.x >= width - totalPaddingRight) {
                        setText("")
                        if (mListener != null) {
                            mListener!!.onClear()
                        }
                    }
                }
            }
        }

        return super.onTouchEvent(event)
    }

    private fun setDeleteDrawableVisible(visible: Boolean) {
        val right = if (visible && isEnabled) mDeleteIcon else null
        setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], right, compoundDrawables[3])
    }


    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (mHasFocus) {
            setDeleteDrawableVisible(s.isNotEmpty())
        }
    }

    override fun afterTextChanged(s: Editable) {

    }

    override fun onFocusChange(v: View, hasFocus: Boolean) {
        this.mHasFocus = hasFocus
        if (hasFocus) {
            setDeleteDrawableVisible(text!!.length > 0)
        } else {
            setDeleteDrawableVisible(false)
        }
    }
}
