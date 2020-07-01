package com.attendance.bk.db.table


import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import com.attendance.bk.db.BkDb
import com.attendance.bk.db.TableName
import com.attendance.bk.utils.UUIDUtil
import com.blankj.utilcode.util.StringUtils
import com.fasterxml.jackson.annotation.JsonIgnore


/**
 * 用户表
 * Created by Chen xuJie on 2018/12/14/014
 */
@Entity(tableName = TableName.TB_NAME_USER, indices = [Index("user_id", unique = true)])
data class User(


    @PrimaryKey
    @ColumnInfo(name = "user_id")//用户id
    var userId: String = "",

    @ColumnInfo(name = "nickname")//用户昵称
    var nickname: String? = null,

    @ColumnInfo(name = "mobile")
    var mobile: String? = null,

    @ColumnInfo(name = "gender")
    var gender: String = "",

    @ColumnInfo(name = "icon")
    var icon: String? = null,

    @ColumnInfo(name = "location")
    var location: String = "",

    @ColumnInfo(name = "birthday")
    var birthday: String = "",

    @ColumnInfo(name = "register_type")
    var registerType: Int = 0,

    @ColumnInfo(name = "add_time")
    var addTime: String = "",//格式为"yyyy-MM-dd HH:mm:ss.SSS"

    @ColumnInfo(name = "update_time")
    var updateTime: String = "",//格式为"yyyy-MM-dd HH:mm:ss.SSS"

    @BkDb.OPType
    @ColumnInfo(name = "operator_type")
    var operatorType: Int = 0,

    @JsonIgnore
    @ColumnInfo(name = "user_is_visitor")
    var userIsVisitor: Int = 1,//0表示是游客，1表示正常用户

    @Ignore
    @JsonIgnore
    private var userExtra: UserExtra = UserExtra()

) : Parcelable {


    fun getUserExtra(): UserExtra {
        if (StringUtils.isEmpty(userExtra.userId)) {
            userExtra = BkDb.instance.userExtraDao().getUserExtra(userId)
            setUserExtra(userExtra)
        }
        return userExtra
    }

    fun setUserExtra(userExtra: UserExtra) {
        this.userExtra = userExtra
    }


    fun userIsVisitor(): Boolean = userIsVisitor == 0

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readParcelable(UserExtra::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeString(nickname)
        parcel.writeString(mobile)
        parcel.writeString(gender)
        parcel.writeString(icon)
        parcel.writeString(location)
        parcel.writeString(birthday)
        parcel.writeInt(registerType)
        parcel.writeString(addTime)
        parcel.writeString(updateTime)
        parcel.writeInt(operatorType)
        parcel.writeInt(userIsVisitor)
        parcel.writeParcelable(userExtra, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }


}
