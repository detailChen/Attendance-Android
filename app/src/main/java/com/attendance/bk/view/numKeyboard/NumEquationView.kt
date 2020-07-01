package com.attendance.bk.view.numKeyboard

import android.content.Context
import android.text.Editable
import android.text.Selection
import android.text.TextUtils
import android.text.method.ArrowKeyMovementMethod
import android.text.method.MovementMethod
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatTextView

/**
 * 可以做加减运算的数字输入框，类似EditText，但不触发任何输入法
 * Created by Jie on 2019/02/11.
 */

class NumEquationView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatTextView(context, attrs, defStyleAttr) {

    init {
        setTextIsSelectable(true)
        isFocusable = true
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        setSelection(text.length)//强行将光标固定在最后面
        return true
    }


    override fun onTextChanged(s: CharSequence, start: Int, lengthBefore: Int, lengthAfter: Int) {
        //如果输入小数，保证最多输入两位小数，如果是整数，虽多能输9位
        var equation = s.toString()
        if (!equation.endsWith("+") && !equation.endsWith("-")) {
            val plusMinusSignIdx = Math.max(equation.lastIndexOf("+"), equation.lastIndexOf("-"))
            var lastNum = equation.substring(plusMinusSignIdx + 1).trim { it <= ' ' }
            if (lastNum.startsWith("0") && lastNum.length > 1) {
                val c = lastNum[1]
                if (c >= '0' && c <= '9') {
                    lastNum = lastNum.substring(1, 2)
                }
            }
            val dotIdx = lastNum.indexOf(".")
            if (dotIdx != -1) {
                if (lastNum.length > dotIdx + 2) {
                    lastNum = lastNum.substring(0, dotIdx + 3)
                }
            } else {
                if (lastNum.length > 9) {
                    lastNum = lastNum.substring(0, 9)
                }
            }
            equation = equation.substring(0, plusMinusSignIdx + 1) + " " + lastNum
            if (!TextUtils.equals(s.toString(), equation)) {
                setText(equation)
                setSelection(equation.length)
                invalidate()
            }
        }
    }

    override fun getDefaultEditable(): Boolean {
        return true
    }

    override fun getDefaultMovementMethod(): MovementMethod {
        return ArrowKeyMovementMethod.getInstance()
    }

    override fun getText(): Editable {
        return super.getText() as Editable
    }

    override fun setText(text: CharSequence, type: BufferType) {
        super.setText(text, BufferType.EDITABLE)
    }


    fun setSelection(index: Int) {
        Selection.setSelection(text, index)
    }


}
