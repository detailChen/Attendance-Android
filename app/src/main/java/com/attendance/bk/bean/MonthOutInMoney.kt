package com.attendance.bk.bean

import androidx.room.ColumnInfo

/**
 * Created by Chen xuJie on 2020/4/20.
 */
data class MonthOutInMoney(
    val money: Double,
    @ColumnInfo(name = "trade_type")
    val tradeType: Int) {
}