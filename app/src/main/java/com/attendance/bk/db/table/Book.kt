package com.attendance.bk.db.table

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.attendance.bk.db.BkDb
import com.attendance.bk.db.TableName
import com.attendance.bk.utils.UUIDUtil

/**
 * 账本表
 * Created by Chen xuJie on 2018/12/14/014
 */
@Entity(tableName = TableName.TB_NAME_BOOK, indices = [Index("book_id", unique = true)])
data class Book(

    @PrimaryKey
    @ColumnInfo(name = "book_id")
    var bookId: String = "",

    @ColumnInfo(name = "name")
    var name: String = "",

    @ColumnInfo(name = "memo")
    var memo: String? = null,

    @ColumnInfo(name = "cover")
    var cover: String = "",

    @ColumnInfo(name = "order_num")
    var orderNum: Int = 0,

    @ColumnInfo(name = "book_type_id")
    var bookTypeId: String = "",

    @ColumnInfo(name = "base_salary")
    var baseSalary: Double = 0.0,//底薪

    @ColumnInfo(name = "hour_salary")
    var hourSalary: Double = 0.0,//时薪

    @ColumnInfo(name = "working_day_overtime_hour_salary")
    val workingDayOvertimeHourSalary: Double = 0.0,//工作日加班工资

    @ColumnInfo(name = "weekend_overtime_hour_salary")
    val weekendOvertimeHourSalary: Double = 0.0,//周末加班工资

    @ColumnInfo(name = "holiday_overtime_hour_salary")
    val holidayOvertimeHourSalary: Double = 0.0,//节假日加班工资

    @ColumnInfo(name = "daily_salary")
    val dailySalary: Double = 0.0,//日薪,如果是按日点工账本

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
        parcel.readInt(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(bookId)
        parcel.writeString(name)
        parcel.writeString(memo)
        parcel.writeString(cover)
        parcel.writeInt(orderNum)
        parcel.writeString(bookTypeId)
        parcel.writeDouble(baseSalary)
        parcel.writeDouble(hourSalary)
        parcel.writeDouble(workingDayOvertimeHourSalary)
        parcel.writeDouble(weekendOvertimeHourSalary)
        parcel.writeDouble(holidayOvertimeHourSalary)
        parcel.writeDouble(dailySalary)
        parcel.writeString(userId)
        parcel.writeString(addTime)
        parcel.writeString(updateTime)
        parcel.writeLong(version)
        parcel.writeInt(operatorType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Book> {
        override fun createFromParcel(parcel: Parcel): Book {
            return Book(parcel)
        }

        override fun newArray(size: Int): Array<Book?> {
            return arrayOfNulls(size)
        }
    }


}
