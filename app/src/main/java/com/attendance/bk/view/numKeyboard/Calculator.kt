package com.attendance.bk.view.numKeyboard

import com.attendance.bk.utils.BkUtil

import java.math.BigDecimal

/**
 * Created by CXJ
 * Created date 2019/6/18/018
 */
internal object Calculator {


    fun calcResult(numInput: List<NumInput>): String {
        if (numInput.isEmpty()) {
            return "0"
        }
        if (numInput.size == 1) {
            return numInput[0].number
        }

        var dResult = BigDecimal(BkUtil.parseMoney(numInput[0].number))
        var i = 2
        val size = numInput.size
        while (i < size) {
            val op = numInput[i - 1].operator
            if (op == NumInput.OP_PLUS) {
                dResult = dResult.add(BigDecimal(BkUtil.parseMoney(numInput[i].number)))
            } else if (op == NumInput.OP_MINUS) {
                dResult = dResult.subtract(BigDecimal(BkUtil.parseMoney(numInput[i].number)))
            } else {
                throw RuntimeException("方程式格式错误！")
            }
            i += 2
        }
        //四舍五入保留两位小数
        var sResult = BkUtil.keepDecimalPlaces(2, dResult).toString()
        val dotIndex = sResult.indexOf(".")
        if (dotIndex == -1) {
            return sResult
        }
        var numAfterDot = sResult.substring(dotIndex + 1)
        if (numAfterDot.length > 2) {//先截取小数点后两位
            numAfterDot = numAfterDot.substring(0, 2)
        }
        if (numAfterDot.length == 2) {//小数点后面有两位小数
            if (numAfterDot.endsWith("00")) {//后两位都是0，顺序不能调换
                sResult = sResult.substring(0, dotIndex)
            } else if (numAfterDot.endsWith("0")) {//最后一位是0
                sResult = sResult.substring(0, dotIndex + 2)
            }
        } else {//小数点后面只有一位小数
            if (numAfterDot.endsWith("0")) {//最后一位是0
                sResult = sResult.substring(0, dotIndex)
            }
        }

        return sResult


    }
}
