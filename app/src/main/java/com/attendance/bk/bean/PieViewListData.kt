package com.attendance.bk.bean

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Ignore
import kotlin.math.abs

/**
 * Created by CXJ
 * Created date 2019/5/24/024
 */
data class PieViewListData(
        @ColumnInfo(name = "bill_id")
        var billId: String = "",
        var name: String = "",
        var money: Double = 0.0,
        var icon: String = "",
        var color: String = "",
        @ColumnInfo(name = "trade_type")
        var tradeType: Int = 0,
        @Ignore
        var percent: Float = 0.0f // 总金额占比
) : Comparable<PieViewListData>, Parcelable {


    constructor(parcel: Parcel) : this() {
        billId = parcel.readString()
        name = parcel.readString()
        money = parcel.readDouble()
        icon = parcel.readString()
        color = parcel.readString()
        tradeType = parcel.readInt()
        percent = parcel.readFloat()
    }


    override fun compareTo(other: PieViewListData): Int {
        return java.lang.Double.valueOf(abs(other.money)).compareTo(abs(money))
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(billId)
        parcel.writeString(name)
        parcel.writeDouble(money)
        parcel.writeString(icon)
        parcel.writeString(color)
        parcel.writeInt(tradeType)
        parcel.writeFloat(percent)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PieViewListData> {
        override fun createFromParcel(parcel: Parcel): PieViewListData {
            return PieViewListData(parcel)
        }

        override fun newArray(size: Int): Array<PieViewListData?> {
            return arrayOfNulls(size)
        }
    }


}
