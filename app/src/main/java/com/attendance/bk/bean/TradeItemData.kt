package com.attendance.bk.bean

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo

/**
 * 流水信息
 * Created by CXJ
 * Created date 2019/1/24/024
 */
data class TradeItemData(
    @ColumnInfo(name = "trade_id")
    var tradeId: String = "",

    @ColumnInfo(name = "date")
    var date: String = "",

    @ColumnInfo(name = "money")
    var money: Double = 0.0,

    @ColumnInfo(name = "book_id")
    var bookId: String = "",

    @ColumnInfo(name = "trade_type")
    var tradeType: Int = 0,

    @ColumnInfo(name = "type")
    var type: Int = 0,

    @ColumnInfo(name = "type_id")
    var typeId: String? = null,

    @ColumnInfo(name = "user_id")
    var userId: String = "",

    @ColumnInfo(name = "memo")
    var memo: String? = null,

    @ColumnInfo(name = "add_time")
    var addTime: String? = null,

    @ColumnInfo(name = "update_time")
    var updateTime: String = "",


    @ColumnInfo(name = "bill_id")
    var billId: String = "",

    @ColumnInfo(name = "name")
    var name: String? = null,

    @ColumnInfo(name = "icon")
    var icon: String? = null,

    @ColumnInfo(name = "color")
    var color: String? = null,

    @ColumnInfo(name = "account_id")
    var accountId: String? = null,

    @ColumnInfo(name = "account_name")
    var accountName: String? = null

    ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(tradeId)
        parcel.writeString(date)
        parcel.writeDouble(money)
        parcel.writeString(bookId)
        parcel.writeInt(tradeType)
        parcel.writeInt(type)
        parcel.writeString(typeId)
        parcel.writeString(userId)
        parcel.writeString(memo)
        parcel.writeString(addTime)
        parcel.writeString(updateTime)
        parcel.writeString(billId)
        parcel.writeString(name)
        parcel.writeString(icon)
        parcel.writeString(color)
        parcel.writeString(accountId)
        parcel.writeString(accountName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TradeItemData> {
        override fun createFromParcel(parcel: Parcel): TradeItemData {
            return TradeItemData(parcel)
        }

        override fun newArray(size: Int): Array<TradeItemData?> {
            return arrayOfNulls(size)
        }
    }


}
