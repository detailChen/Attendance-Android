package com.attendance.bk.view.numKeyboard

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView


import androidx.annotation.IdRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.attendance.bk.R

/**
 * @author Chen Xujie
 * @since 2018-12-23
 */
class NumKeyboardView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : ConstraintLayout(context, attrs, defStyle), View.OnClickListener {


    private var mNumInputHelper: NumInputHelper? = null
    private var mKeyboardListener: OnKeyboardListener? = null

    init {
        init(context)
    }

    fun setKeyboardListener(listener: OnKeyboardListener) {
        mKeyboardListener = listener
    }

    fun setInputView(numMoney: NumMoneyView, numEquation: NumEquationView) {
        mNumInputHelper = NumInputHelper(numMoney, numEquation)
    }

    private fun init(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.view_keyboard, this, true)
        initView()
    }

    private fun initView() {
        setViewClickListener(
                R.id.kb_0, R.id.kb_1, R.id.kb_2,
                R.id.kb_3, R.id.kb_4, R.id.kb_5,
                R.id.kb_6, R.id.kb_7, R.id.kb_8,
                R.id.kb_9, R.id.kb_dot, R.id.kb_plus,
                R.id.kb_minus, R.id.kb_save, R.id.kb_delete)

        findViewById<View>(R.id.kb_delete).setOnLongClickListener { v ->
            mNumInputHelper!!.clearResetInput()
            true
        }
    }

    private fun setViewClickListener(@IdRes vararg viewIds: Int) {
        for (viewId in viewIds) {
            findViewById<View>(viewId).setOnClickListener(this)
        }
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.kb_0,
            R.id.kb_1,
            R.id.kb_2,
            R.id.kb_3,
            R.id.kb_4,
            R.id.kb_5,
            R.id.kb_6,
            R.id.kb_7,
            R.id.kb_8,
            R.id.kb_9 -> mNumInputHelper!!.onInputNumber((v as TextView).text.toString())
            R.id.kb_delete -> mNumInputHelper!!.onInputDelete()
            R.id.kb_dot -> mNumInputHelper!!.onInputDecimalPoint()
            R.id.kb_plus -> mNumInputHelper!!.onInputOperator(NumInput.OP_PLUS)
            R.id.kb_minus -> mNumInputHelper!!.onInputOperator(NumInput.OP_MINUS)
            R.id.kb_save -> if (mKeyboardListener != null) {
                mKeyboardListener!!.onSave()
            }
            else -> {
            }
        }
    }

}
