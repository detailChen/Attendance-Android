package com.attendance.bk.view.pieview

import com.attendance.bk.utils.BkUtil
import com.attendance.bk.bean.PieViewListData
import java.util.*
import kotlin.math.abs

/**
 * Created by CXJ
 * Created date 2019/7/4/004
 */
object PieDataHelper {


    private const val MAX_DATA_COUNT = 12

    /**
     * 转换数据，从传入参数转换为画图数据
     */
    fun readPieDataFromFormData(datas: MutableList<PieViewListData>): ArrayList<PieData> {
        val sortedList = ArrayList(datas)
        sortedList.sort()
        sortedList.reverse()

        var otherMoney = 0.0
        var totalMoney = 0.0

        var i = 0
        val size = sortedList.size
        while (i < size) {
            val f = sortedList[i]
            val money = Math.abs(f.money)
            totalMoney += money
            if (i >= MAX_DATA_COUNT) {
                otherMoney += money
                datas.remove(f)
            }
            i++
        }

        if (otherMoney > 0) { // 超出部分改为其它
            datas.add(generateOtherData(otherMoney))
        }

        val pieDataList = ArrayList<PieData>()
        for (f in datas) {
            pieDataList.add(PieData(BkUtil.parseColor(f.color), (abs(f.money) / totalMoney).toFloat(), f.name))
        }

        return pieDataList
    }


    private fun generateOtherData(money: Double): PieViewListData {
        return PieViewListData(
                name = "其余类别",
                money = money,
                color = "#ff565656"
        )
    }


}
