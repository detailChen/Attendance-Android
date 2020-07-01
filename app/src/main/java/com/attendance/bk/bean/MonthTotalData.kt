package com.attendance.bk.bean

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo

/**
 * 月统计数据
 *
 * @author CJL
 * @since 2016-03-28
 */
data class MonthTotalData(
        @ColumnInfo(name = "trade_month")
        var month: String = "",
        @ColumnInfo(name = "trade_in")
        var tradeIn: Double = 0.0,
        @ColumnInfo(name = "trade_out")
        var tradeOut: Double = 0.0,
        @ColumnInfo(name = "trade_count")
        var tradeCount: Int = 0,
        @ColumnInfo(name = "day_count")
        var dayCount: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readInt(),
            parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(month)
        parcel.writeDouble(tradeIn)
        parcel.writeDouble(tradeOut)
        parcel.writeInt(tradeCount)
        parcel.writeInt(dayCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MonthTotalData> {
        override fun createFromParcel(parcel: Parcel): MonthTotalData {
            return MonthTotalData(parcel)
        }

        override fun newArray(size: Int): Array<MonthTotalData?> {
            return arrayOfNulls(size)
        }
    }


}
