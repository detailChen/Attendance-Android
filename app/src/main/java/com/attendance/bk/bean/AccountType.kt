package com.attendance.bk.bean

import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by Chen xuJie on 2020/2/18.
 */
data class AccountType(

        @JsonProperty("account_type_id")
        var accountTypeId: String = "",

        @JsonProperty("account_type_icon")
        var accountTypeIcon: String = "",

        @JsonProperty("account_type_name")
        var accountTypeName: String = "",

        @JsonProperty("type")
        var type: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(accountTypeId)
        parcel.writeString(accountTypeIcon)
        parcel.writeString(accountTypeName)
        parcel.writeInt(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AccountType> {
        override fun createFromParcel(parcel: Parcel): AccountType {
            return AccountType(parcel)
        }

        override fun newArray(size: Int): Array<AccountType?> {
            return arrayOfNulls(size)
        }
    }

}
