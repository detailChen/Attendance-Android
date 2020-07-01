package com.attendance.bk.view.numKeyboard

import androidx.annotation.IntDef

/**
 * Created by CXJ
 * Created date 2019/6/18/018
 */
internal class NumInput {


    val isOperator: Boolean
    @Operator
    val operator: Int
    val number: String


    constructor(@Operator operator: Int) {
        this.isOperator = true
        this.operator = operator
        this.number = "0"
    }

    constructor(number: String) {
        this.isOperator = false
        this.operator = OP_NON
        this.number = number
    }

    companion object {

        const val OP_NON = -1
        const val OP_PLUS = 0
        const val OP_MINUS = 1
    }

    @IntDef(OP_NON, OP_MINUS, OP_PLUS)
    @Retention(AnnotationRetention.SOURCE)
    internal annotation class Operator
}
