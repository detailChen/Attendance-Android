package com.attendance.bk.bean

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo

/**
 * Created by Chen xuJie on 2020/2/21.
 */
data class AccountItem(
        @ColumnInfo(name = "account_id")
        val accountId: String = "",
        @ColumnInfo(name = "name")
        val name: String = "",
        @ColumnInfo(name = "account_type_id")
        val accountTypeId: String = "",
        @ColumnInfo(name = "account_type_name")
        val accountTypeName: String = "",
        @ColumnInfo(name = "account_type_icon")
        val accountTypeIcon: String = "",
        @ColumnInfo(name = "account_money")
        val accountMoney: Double = 0.0,
        @ColumnInfo(name = "memo")
        val memo: String? = null) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readDouble(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(accountId)
        parcel.writeString(name)
        parcel.writeString(accountTypeId)
        parcel.writeString(accountTypeName)
        parcel.writeString(accountTypeIcon)
        parcel.writeDouble(accountMoney)
        parcel.writeString(memo)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AccountItem> {
        override fun createFromParcel(parcel: Parcel): AccountItem {
            return AccountItem(parcel)
        }

        override fun newArray(size: Int): Array<AccountItem?> {
            return arrayOfNulls(size)
        }
    }
}