package com.attendance.bk.bean

import android.os.Parcel
import android.os.Parcelable

import androidx.room.ColumnInfo

/**
 * 日统计信息
 *
 * @author CJL
 * @since 2016-01-06
 */
data class DayTotalData(
        /**
         * 日期
         */
        @ColumnInfo(name = "date")
        var date: String = "",
        /**
         * 结余
         */
        @ColumnInfo(name = "day_money")
        var dayMoney: Double = 0.0,
        /**
         * 总共记录数
         */
        @ColumnInfo(name = "trade_count")
        var tradeCount: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readDouble(),
            parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(date)
        parcel.writeDouble(dayMoney)
        parcel.writeInt(tradeCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DayTotalData> {
        override fun createFromParcel(parcel: Parcel): DayTotalData {
            return DayTotalData(parcel)
        }

        override fun newArray(size: Int): Array<DayTotalData?> {
            return arrayOfNulls(size)
        }
    }


}
