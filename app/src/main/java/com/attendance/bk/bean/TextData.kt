package com.attendance.bk.bean


/**
 *
 * Created by CXJ
 * Created date 2019/9/29/029
 *
 */
class TextData {

    var textType: Int = -1
    var textContent: String? = null

    constructor(textType: Int, textContent: String?) {
        this.textType = textType
        this.textContent = textContent
    }
}