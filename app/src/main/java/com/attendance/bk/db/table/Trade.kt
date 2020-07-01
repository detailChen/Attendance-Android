package com.attendance.bk.db.table

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.IntDef
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.attendance.bk.db.*


/**
 * 记账流水表
 * Created by Chen xuJie on 2018/12/14/014
 */
@Entity(
    tableName = TableName.TB_NAME_TRADE,
    indices = [
        Index("trade_id", unique = true),
        Index("money"),
        Index("date"),
        Index("type", "type_id")]
)
data class Trade(


    @PrimaryKey
    @ColumnInfo(name = "trade_id")
    var tradeId: String = "",

    @ColumnInfo(name = "money")
    var money: Double = 0.0,

    @ColumnInfo(name = "book_id")
    var bookId: String = "",

    @TradeType
    @ColumnInfo(name = "trade_type")
    var tradeType: Int = 0,

    @ColumnInfo(name = "date")
    var date: String = "",

    @ColumnInfo(name = "account_id")
    var accountId: String? = null,

    @ColumnInfo(name = "memo")
    var memo: String? = null,

    @ColumnInfo(name = "user_id")
    var userId: String = "",

    @Type
    @ColumnInfo(name = "type")
    var type: Int = TRADE_TYPE_NORMAL,

    @ColumnInfo(name = "type_id")
    var typeId: String? = null,

    @ColumnInfo(name = "bill_id")
    var billId: String = "",

    @ColumnInfo(name = "add_time")
    var addTime: String = "",//格式为"yyyy-MM-dd HH:mm:ss.SSS"

    @ColumnInfo(name = "update_time")
    var updateTime: String = "",//格式为"yyyy-MM-dd HH:mm:ss.SSS"

    @ColumnInfo(name = "version")
    var version: Long = 0,

    @BkDb.OPType
    @ColumnInfo(name = "operator_type")
    var operatorType: Int = 0


) : Parcelable {


    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readInt()
    ) {
    }

    // 交易收支类型ID
    @IntDef(TRADE_TYPE_IN, TRADE_TYPE_OUT)
    @Retention(AnnotationRetention.SOURCE)
    annotation class TradeType

    // 交易流水类型ID
    @IntDef(
        TRADE_TYPE_NORMAL,
        TRADE_TYPE_TRANSFER,
        TRADE_TYPE_LOAN
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class Type

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(tradeId)
        parcel.writeDouble(money)
        parcel.writeString(bookId)
        parcel.writeInt(tradeType)
        parcel.writeString(date)
        parcel.writeString(accountId)
        parcel.writeString(memo)
        parcel.writeString(userId)
        parcel.writeInt(type)
        parcel.writeString(typeId)
        parcel.writeString(billId)
        parcel.writeString(addTime)
        parcel.writeString(updateTime)
        parcel.writeLong(version)
        parcel.writeInt(operatorType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Trade> {
        override fun createFromParcel(parcel: Parcel): Trade {
            return Trade(parcel)
        }

        override fun newArray(size: Int): Array<Trade?> {
            return arrayOfNulls(size)
        }
    }


}
