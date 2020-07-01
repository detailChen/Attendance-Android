package com.attendance.bk.view.pieview


/**
 *
 * Created by CXJ
 * Created date 2019/7/4/004
 *
 */
class PieData constructor(var color: Int, var percent: Float, var name: String?) {

    // 饼图外圆上饼块中心点
    var p1x: Float = 0f
    var p1y: Float = 0f

    // 连线中心转折点
    var p2x: Float = 0f
    var p2y: Float = 0f

    // 文字边缘点
    var p3x: Float = 0f
    var p3y: Float = 0f


}

