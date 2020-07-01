package com.attendance.bk.view.numKeyboard

import android.text.TextUtils
import android.view.View
import com.attendance.bk.utils.BkUtil
import java.math.BigDecimal
import java.util.*

/**
 * Created by CXJ
 * Created date 2019/6/18/018
 */
internal class NumInputHelper(
    /**
         * 没有算数运算时金额的输入框
         */
        private val mNumMoney: NumMoneyView,
    /**
         * 算数运算表达式输入框
         */
        private val mNumEquation: NumEquationView
) {


    private val mNumInputs = ArrayList<NumInput>()

    /**
     * 用户当前是在输入金额还是在计算
     */
    private var mInputType = TYPE_MONEY

    /**
     * 拼接当前算式字符串
     *
     * @return 算式字符串
     */
    private fun getEquation(): String {
        val sb = StringBuilder()
        for (input in mNumInputs) {
            if (input.isOperator) {
                when (input.operator) {
                    NumInput.OP_MINUS -> sb.append("-")
                    NumInput.OP_PLUS -> sb.append("+")
                    NumInput.OP_NON -> {
                    }
                    else -> {
                    }
                }
            } else {
                sb.append(input.number)
            }
            sb.append(" ")
        }
        return sb.toString()
    }


    private val isLastInputOperator: Boolean
        get() = mNumInputs.size > 0 && mNumInputs[mNumInputs.size - 1].isOperator

    /**
     * 输入数字
     *
     * @param number 数字
     */
    fun onInputNumber(number: String) {
        if (mInputType == TYPE_MONEY) {
            val etNum = mNumMoney.text
            etNum.insert(mNumMoney.selectionStart, number)
            //保留两位小数
            var sNum = etNum.toString()
            val dotIdx = sNum.lastIndexOf(".")
            if (dotIdx != -1 && sNum.length > dotIdx + 2) {
                sNum = sNum.substring(0, dotIdx + 3)
            }
            if (mNumInputs.size > 0) {
                mNumInputs[0] = NumInput(sNum)
            } else {
                mNumInputs.add(NumInput(sNum))
            }
        } else {
            val etEquation = mNumEquation.text
            etEquation.insert(mNumEquation.selectionStart, number)

            if (isLastInputOperator) {//上一次输入的是加减符号
                mNumInputs.add(NumInput(number))
            } else {//上一次输入的是数字，则拼接上去
                var lastNum = mNumInputs[mNumInputs.size - 1].number
                if (lastNum.contains(".")) {
                    val dotIdx = lastNum.indexOf(".")
                    if (lastNum.length <= dotIdx + 2) {//已经是两位小数了，则不再拼接上去
                        lastNum += number
                    }
                } else {
                    if (lastNum == "0") {//最后一个是0，则用现在这个替换掉0
                        lastNum = number
                    } else {
                        if (lastNum.length < 9) {//最大只能输9位，如果已经9为了，则不再拼接上去
                            lastNum += number
                        }
                    }
                }
                mNumInputs[mNumInputs.size - 1] = NumInput(lastNum)
            }
            mNumMoney.setText(Calculator.calcResult(mNumInputs))
        }
    }


    /**
     * 输入删除字符
     */
    fun onInputDelete() {
        if (mInputType == TYPE_MONEY) {
            val et = mNumMoney.text
            if (TextUtils.isEmpty(et)) {
                return
            }
            val ss = mNumMoney.selectionStart
            val se = mNumMoney.selectionEnd
            if (ss == se) {
                if (et.toString().startsWith(".") && ss == 1) {
                    et.replace(ss - 1, ss, "0")
                }
                if (ss > 0) {
                    et.delete(ss - 1, ss)
                }
            } else {
                et.delete(ss, se)
            }
            if (TextUtils.isEmpty(et.toString())) {
                mNumInputs.clear()
            } else {
                if (mNumInputs.size > 0) {
                    mNumInputs[0] = NumInput(et.toString())
                }
            }
        } else {
            val et = mNumEquation.text
            if (TextUtils.isEmpty(et)) {
                return
            }
            val cLast = et[et.length - 1]//要在删除之前获得
            val ss = mNumEquation.selectionStart
            val se = mNumEquation.selectionEnd
            if (ss == se) {
                if (et.toString().startsWith(".") && ss == 1) {
                    et.replace(ss - 1, ss, "0")
                }
                if (ss > 0) {
                    et.delete(ss - 1, ss)
                }
            } else {
                et.delete(ss, se)
            }
            if (cLast == '+' || cLast == '-') {//最后一个是符号，直接删掉
                mNumInputs.removeAt(mNumInputs.size - 1)
            } else {
                if (cLast != ' ') {//小数点或数字
                    var lastNum = mNumInputs[mNumInputs.size - 1].number
                    if (lastNum.length == 1) {//个位数，直接删掉
                        mNumInputs.removeAt(mNumInputs.size - 1)
                    } else {
                        lastNum = lastNum.substring(0, lastNum.length - 1)
                        mNumInputs[mNumInputs.size - 1] = NumInput(lastNum)
                    }
                }
            }
            mNumMoney.setText(Calculator.calcResult(mNumInputs))
            //上面的删至只剩一个数字的时候，模式变更
            if (isNumString(et.toString().trim { it <= ' ' })) {//要去掉空格
                changeInputType(TYPE_MONEY)
            }
        }
    }


    /**
     * 输入小数点
     */
    fun onInputDecimalPoint() {
        if (mInputType == TYPE_MONEY) {
            val etDot = mNumMoney.text
            if (isLastInputOperator) { // 之前输入完操作符
                etDot.replace(0, etDot.length, "0.")
                mNumMoney.setSelection(2)
            } else {
                val cNum = etDot.toString()
                if (cNum.isEmpty()) {
                    etDot.append("0.")
                } else if (!cNum.contains(".")) {
                    etDot.append('.')
                } else {
                    mNumMoney.setSelection(cNum.indexOf('.') + 1)
                }
            }
            if (mNumInputs.size > 0) {
                mNumInputs[0] = NumInput(etDot.toString())
            } else {
                mNumInputs.add(NumInput(etDot.toString()))
            }
        } else {
            val etDot = mNumEquation.text
            val cNum = etDot.toString().trim { it <= ' ' }
            val c = cNum[cNum.length - 1]
            if (c == '+' || c == '-') {
                etDot.append("0.")
            } else if (c == '.') {
                mNumEquation.setSelection(cNum.lastIndexOf('.') + 1)
            } else {
                val lastNum = mNumInputs[mNumInputs.size - 1].number
                if (isInteger(BigDecimal(BkUtil.parseMoney(lastNum)))) {
                    etDot.append('.')
                }
            }
            if (isLastInputOperator) {//上次输入的是符号
                mNumInputs.add(NumInput("0."))//这里的点不能省略
            } else {
                var lastNum = mNumInputs[mNumInputs.size - 1].number
                if (!lastNum.contains(".")) {
                    lastNum = "$lastNum."
                }
                mNumInputs[mNumInputs.size - 1] = NumInput(lastNum)
            }
        }
    }

    /**
     * 输入运算符
     *
     * @param operator 运算符
     */
    fun onInputOperator(@NumInput.Operator operator: Int) {
        changeInputType(TYPE_EQUATION)
        val equation = mNumEquation.text.toString().trim { it <= ' ' }
        if (!TextUtils.isEmpty(equation)) {
            val cLast = equation[equation.length - 1]
            if (cLast == '+' || cLast == '-') {// 替换操作符
                mNumInputs[mNumInputs.size - 1] = NumInput(operator)
            } else {
                mNumInputs.add(NumInput(operator))
            }
        } else {//第一次输入符号
            var firstNum = mNumMoney.text.toString()
            if (TextUtils.isEmpty(firstNum)) {//什么也不输，直接输入符号
                mNumInputs.add(NumInput("0"))
            } else {//输入符号之前输了数字，数字格式可能有误，如5.这种格式，校正
                if (firstNum.endsWith(".")) {
                    firstNum = firstNum.substring(0, firstNum.length - 1)//输入的是5.这种格式，去掉后面的点
                }
                //存在一种情况，就是输入框mEtMoney已经输入数字，但是还没有加到mInput中去，
                //(比如在首页记一笔，进来编辑流水，会将mEtMoney设置为之前的金额，由于设
                //置操作是在其他页面操作的，所以并没有将该金额内容加到mInput里面去)，这里对这种情况做处理
                if (mNumInputs.isEmpty()) {
                    mNumInputs.add(NumInput(firstNum))
                } else {
                    mNumInputs[0] = NumInput(firstNum)
                }
            }
            mNumInputs.add(NumInput(operator))
        }

        mNumEquation.setText(getEquation())
        mNumEquation.setSelection(mNumEquation.length())
        mNumMoney.setText(Calculator.calcResult(mNumInputs))

    }


    fun clearResetInput() {
        mNumInputs.clear()
        mNumMoney.text.clear()
        mNumEquation.text.clear()
        changeInputType(TYPE_MONEY)
    }

    private fun changeInputType(editType: Int) {
        mInputType = editType
        if (mInputType == TYPE_MONEY) {
            mNumEquation.visibility = View.GONE
            mNumEquation.text.clear()
            mNumMoney.isEnabled = true
            mNumMoney.requestFocus()
            mNumMoney.setSelection(mNumMoney.length())
        } else {
            mNumEquation.visibility = View.VISIBLE
            mNumEquation.requestFocus()
            mNumMoney.isEnabled = false
        }
    }


    /**
     * 该字符串是否是数字
     *
     * @return 是否是数字
     */
    private fun isNumString(numString: String): Boolean {
        for (i in 0 until numString.length) {
            val c = numString[i]
            if ((c > '9' || c < '0') && c != '.') {
                return false
            }
        }
        return true
    }

    /**
     * 是否是整数
     */
    private fun isInteger(bd: BigDecimal): Boolean {
        return bd.signum() == 0 || bd.scale() <= 0 || bd.stripTrailingZeros().scale() <= 0
    }

    companion object {
        const val TYPE_MONEY = 0//输入金额
        const val TYPE_EQUATION = 1//计算
    }

}
