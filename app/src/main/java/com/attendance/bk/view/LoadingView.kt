package com.attendance.bk.view

import android.content.Context
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View


import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.attendance.bk.R
import com.blankj.utilcode.util.ConvertUtils

/**
 * loading View
 *
 *
 *
 * @author CXJ
 * @since 2019-01-23
 */
class LoadingView : View {


    private var mDrawable: Animatable? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (visibility == VISIBLE) {
            if (mDrawable != null) {
                mDrawable!!.start()
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (mDrawable != null) {
            mDrawable!!.stop()
        }
    }

    private fun init(context: Context) {
        val loadingDrawable = CircularProgressDrawable(context)
        loadingDrawable.setColorSchemeColors(*context.resources.getIntArray(R.array.loading_colors))
        loadingDrawable.alpha = 255 // SUPPRESS CHECKSTYLE
        loadingDrawable.strokeWidth = ConvertUtils.dp2px(2.5f).toFloat()
        background = loadingDrawable
    }

    override fun setBackground(background: Drawable?) {
        if (mDrawable === background) {
            return
        }
        if (background != null) {
            if (mDrawable != null) {
                mDrawable!!.stop()
            }

            if (background is Animatable) {
                mDrawable = background
                if (visibility == VISIBLE) {
                    mDrawable!!.start()
                }
            } else {
                mDrawable = null
            }
        }
        super.setBackground(background)
    }

    // CHECKSTYLE:ON
}
