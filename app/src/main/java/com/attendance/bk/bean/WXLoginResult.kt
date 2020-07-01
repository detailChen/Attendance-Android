package com.attendance.bk.bean

import android.os.Parcel
import android.os.Parcelable

/**
 * 微信登陆返回的用户信息
 * Created by Chen xuJie on 2019/8/14.
 */
data class WXLoginResult(val openId: String,
                         val unionId: String,
                         val nickname: String,
                         val icon: String,
                         val gender: String,
                         val location: String) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(openId)
        parcel.writeString(unionId)
        parcel.writeString(nickname)
        parcel.writeString(icon)
        parcel.writeString(gender)
        parcel.writeString(location)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WXLoginResult> {
        override fun createFromParcel(parcel: Parcel): WXLoginResult {
            return WXLoginResult(parcel)
        }

        override fun newArray(size: Int): Array<WXLoginResult?> {
            return arrayOfNulls(size)
        }
    }
}


