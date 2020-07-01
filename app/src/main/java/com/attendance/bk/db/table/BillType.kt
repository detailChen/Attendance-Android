package com.attendance.bk.db.table

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.IntDef
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.attendance.bk.db.BkDb
import com.attendance.bk.db.TableName
import com.attendance.bk.db.BILL_TYPE_IN
import com.attendance.bk.db.BILL_TYPE_OUT

/**
 * 一级分类收支类别
 * Created by Chen xuJie on 2018/12/14/014
 */
@Entity(tableName = TableName.TB_NAME_BILL_TYPE, indices = [Index("bill_id", unique = true)])
data class BillType(


    @PrimaryKey
    @ColumnInfo(name = "bill_id")
    var billId: String = "",

    @ColumnInfo(name = "name")
    var name: String = "",

    @ColumnInfo(name = "click_icon")
    var clickIcon: String = "",

    @ColumnInfo(name = "normal_icon")
    var normalIcon: String = "",

    @ColumnInfo(name = "book_id")
    var bookId: String = "",

    @Type
    @ColumnInfo(name = "type")
    var type: Int = 0,

    @ColumnInfo(name = "order_num")
    var orderNum: Int = 0,

    @ColumnInfo(name = "user_id")
    var userId: String = "",

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


    // 交易收支类型ID
    @IntDef(BILL_TYPE_IN, BILL_TYPE_OUT)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Type {}


    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(billId)
        parcel.writeString(name)
        parcel.writeString(clickIcon)
        parcel.writeString(normalIcon)
        parcel.writeString(bookId)
        parcel.writeInt(type)
        parcel.writeInt(orderNum)
        parcel.writeString(userId)
        parcel.writeString(addTime)
        parcel.writeString(updateTime)
        parcel.writeLong(version)
        parcel.writeInt(operatorType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BillType> {
        override fun createFromParcel(parcel: Parcel): BillType {
            return BillType(parcel)
        }

        override fun newArray(size: Int): Array<BillType?> {
            return arrayOfNulls(size)
        }
    }


}
