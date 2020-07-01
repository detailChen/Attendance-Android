package com.attendance.bk.db.table

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.attendance.bk.db.BkDb
import com.attendance.bk.db.TableName
import com.attendance.bk.utils.UUIDUtil

/**
 * 账户表
 * Created by Chen xuJie on 2018/12/14/014
 */
@Entity(tableName = TableName.TB_NAME_ACCOUNT)
data class Account(


    @PrimaryKey
    @ColumnInfo(name = "account_id")
    var accountId: String = "",

    @ColumnInfo(name = "name")
    var name: String = "",

    @ColumnInfo(name = "memo")
    var memo: String? = null,

    @ColumnInfo(name = "account_type_id")
    var accountTypeId: String = "",

    @ColumnInfo(name = "account_type_icon")
    var accountTypeIcon: String = "",

    @ColumnInfo(name = "account_type_name")
    var accountTypeName: String = "",

    @ColumnInfo(name = "order_num")
    var orderNum: Int = 0,

    @ColumnInfo(name = "type")//类型，资产还是负债，0表示资产账户，1表示负债账户
    var type: Int = 0,

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
    constructor(parcel: Parcel) : this(
        parcel.readString(),
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
        parcel.writeString(accountId)
        parcel.writeString(name)
        parcel.writeString(memo)
        parcel.writeString(accountTypeId)
        parcel.writeString(accountTypeIcon)
        parcel.writeString(accountTypeName)
        parcel.writeInt(orderNum)
        parcel.writeInt(type)
        parcel.writeString(userId)
        parcel.writeString(addTime)
        parcel.writeString(updateTime)
        parcel.writeLong(version)
        parcel.writeInt(operatorType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Account> {
        override fun createFromParcel(parcel: Parcel): Account {
            return Account(parcel)
        }

        override fun newArray(size: Int): Array<Account?> {
            return arrayOfNulls(size)
        }
    }


}