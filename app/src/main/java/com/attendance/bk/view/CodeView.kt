package com.attendance.bk.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.attendance.bk.R
import com.blankj.utilcode.util.KeyboardUtils
import java.util.*

class CodeView : LinearLayout, TextWatcher, View.OnKeyListener, OnTouchListener {


    //验证码的位数
    private var codeNumber = 0
    //两个验证码之间的距离
    private var codeSpace = 0
    //验证码边框的边长
    private var lengthSide = 0
    //验证码的大小
    private var textSize = 0f
    //字体颜色
    private val textColor = Color.BLACK
    private var inputType = 2
    private var mEditParams: LayoutParams? = null
    private var mViewParams: LayoutParams? = null
    private var mContext: Context? = null
    private val mEditTextList: MutableList<EditText> = ArrayList()
    private var currentPosition = 0
    private var mTextColor = 0
    private var mCodeBg: Drawable? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        mContext = context
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CodeView)
        codeNumber = typedArray.getInteger(R.styleable.CodeView_codeNumber, 4)
        codeSpace = typedArray.getDimensionPixelSize(R.styleable.CodeView_codeSpace, 20)
        lengthSide = typedArray.getDimensionPixelSize(R.styleable.CodeView_lengthSide, 50)
        textSize = typedArray.getInteger(R.styleable.CodeView_codeTextSize, 20).toFloat()
        mTextColor = typedArray.getColor(R.styleable.CodeView_codeTextColor, 0x000000)
        inputType = typedArray.getInteger(R.styleable.CodeView_inputType, 2)
        typedArray.recycle()
        mEditParams = LayoutParams(lengthSide, lengthSide)
        mViewParams = LayoutParams(codeSpace, lengthSide)
        mCodeBg = ContextCompat.getDrawable(context, R.drawable.bg_code_edit)
        initView()
        setOnTouchListener(this)
    }

    fun setCodeTextColor(color: Int) {
        mTextColor = color
        updateEditViewColor()
    }

    /**
     * 初始化输入框
     */
    private fun initView() {
        for (i in 0 until codeNumber) {
            val editText = EditText(mContext)
            editText.run {
                isCursorVisible = false
                setOnKeyListener(this@CodeView)
                textSize = textSize
                setTextColor(mTextColor)
                inputType = inputType
                setTextColor(textColor)
                setPadding(0, 0, 0, 0)
                filters = arrayOf<InputFilter>(LengthFilter(1))
                addTextChangedListener(this@CodeView)
                layoutParams = mEditParams
                gravity = Gravity.CENTER
                setBackgroundDrawable(mCodeBg)
            }
            addView(editText)
            mEditTextList.add(editText)
            if (i != codeNumber - 1) {
                val view = View(mContext)
                view.layoutParams = mViewParams
                addView(view)
            }
        }
    }

    private fun updateEditViewColor() {
        if (mEditTextList.size > 0) {
            for (editText in mEditTextList) {
                editText.setBackgroundDrawable(mCodeBg)
                editText.setTextColor(mTextColor)
            }
        }
    }

    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        if (i == 0 && i2 >= 1 && currentPosition != mEditTextList.size - 1) {
            currentPosition++
            mEditTextList[currentPosition].requestFocus()
        }
    }

    override fun afterTextChanged(editable: Editable) {}


    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return true
    }

    /**
     * 监听删除键
     *
     * @param view
     * @param i
     * @param keyEvent
     * @return
     */
    override fun onKey(view: View, i: Int, keyEvent: KeyEvent): Boolean {
        val editText = view as EditText
        if (i == KeyEvent.KEYCODE_DEL && editText.text.isEmpty()) {
            val action = keyEvent.action
            if (currentPosition != 0 && action == KeyEvent.ACTION_DOWN) {
                currentPosition--
                mEditTextList[currentPosition].requestFocus()
                mEditTextList[currentPosition].setText("")
            }
        }
        return false
    }

    /**
     * 输入错误,清空code
     */
    fun clearCode() {
        if (mEditTextList.size > 0) {
            for (editText in mEditTextList) {
                editText.setText("")
            }
            mEditTextList[0].requestFocus()
            currentPosition = 0
        }
        KeyboardUtils.showSoftInput(mEditTextList[currentPosition])
    }

    /**
     * 获取验证码
     *
     * @return
     */
    val verificationCode: String
        get() {
            val stringBuffer = StringBuilder()
            for (i in mEditTextList.indices) {
                if (i < mEditTextList.size - 1) {
                    stringBuffer.append(mEditTextList[i].text.toString()).append("  ")
                } else {
                    stringBuffer.append(mEditTextList[i].text.toString())
                }
            }
            return stringBuffer.toString()
        }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        KeyboardUtils.showSoftInput(mEditTextList[currentPosition])
        return false
    }
}