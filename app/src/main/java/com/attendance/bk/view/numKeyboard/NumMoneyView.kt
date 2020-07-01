package com.attendance.bk.view.numKeyboard

import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.Selection
import android.text.TextUtils
import android.text.method.ArrowKeyMovementMethod
import android.text.method.MovementMethod
import android.util.AttributeSet
import android.view.MotionEvent

import androidx.appcompat.widget.AppCompatTextView

/**
 * @author Chen Xujie
 * @since 2019-04-29
 */
class NumMoneyView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatTextView(context, attrs, defStyleAttr) {

    init {
        inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        setTextIsSelectable(true)
        isFocusable = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            showSoftInputOnFocus = false
        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        setSelection(text.length)//强行将光标固定在最后面
        return false
    }

    fun resetSelection() {
        setSelection(text.toString().length)
    }


    override fun onTextChanged(s: CharSequence, start: Int, lengthBefore: Int, lengthAfter: Int) {
        var money = s.toString()
        // 删除开头无用的0
        if (money.startsWith("0") && money.length > 1) {
            val c = money[1]
            if (c in '0'..'9') {
                money = money.substring(1, 2)
            }
        }
        val dotIdx = money.indexOf(".")
        // 整数位最多输入9位
        if (dotIdx > 9 || dotIdx == -1 && money.length > 9) {
            money = money.substring(0, 9)
        }
        //保留2位小数点
        if (dotIdx != -1 && money.length > dotIdx + 2) {
            money = money.substring(0, dotIdx + 3)
        }
        if (!TextUtils.equals(s.toString(), money)) {
            setText(money)
            setSelection(money.length)
            invalidate()
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
