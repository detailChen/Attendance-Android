package com.attendance.bk.db.table

import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.attendance.bk.db.TableName
import com.fasterxml.jackson.annotation.JsonIgnore

/**
 * 存储用户一些状态的表，如当前账本等,不同步
 * Created by CXJ
 * Created date 2019/1/18/018
 */
@Entity(tableName = TableName.TB_NAME_USER_EXTRA)
data class UserExtra(

        @PrimaryKey
        @ColumnInfo(name = "user_id")
        var userId: String = "",

        @ColumnInfo(name = "curr_book_id")
        var currBookId: String = ""

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeString(currBookId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserExtra> {
        override fun createFromParcel(parcel: Parcel): UserExtra {
            return UserExtra(parcel)
        }

        override fun newArray(size: Int): Array<UserExtra?> {
            return arrayOfNulls(size)
        }
    }

}
