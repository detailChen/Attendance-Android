//package com.attendance.bk.db.table
//
//
//import android.os.Parcel
//import android.os.Parcelable
//import android.text.TextUtils
//import androidx.annotation.IntDef
//import androidx.room.ColumnInfo
//import androidx.room.Entity
//import androidx.room.Index
//import androidx.room.PrimaryKey
//import com.attendance.bk.db.BkDb
//import com.attendance.bk.db.TableName
//import com.attendance.bk.db.IMAGE_STATE_UPLOAD_NO
//import com.attendance.bk.db.IMAGE_STATE_UPLOAD_YES
//import com.fasterxml.jackson.annotation.JsonIgnore
//
//
///**
// * 图片表
// * Created by CXJ
// * Created date 2019/1/9/009
// */
//@Entity(tableName = TableName.TB_NAME_IMAGE, indices = [Index("image_name", unique = true)])
//data class Image(
//
//
//        @PrimaryKey
//        @ColumnInfo(name = "image_name")
//        var imageName: String = "",
//
//        @ColumnInfo(name = "foreign_id")
//        var foreignId: String = "",
//
//        @UploadState
//        @JsonIgnore
//        @ColumnInfo(name = "upload_state")
//        var uploadState: Int = IMAGE_STATE_UPLOAD_NO,
//
//        @ColumnInfo(name = "user_id")
//        var userId: String = "",
//
//        @ColumnInfo(name = "add_time")
//        var addTime: String = "",//格式为"yyyy-MM-dd HH:mm:ss.SSS"
//
//        @ColumnInfo(name = "update_time")
//        var updateTime: String = "",//格式为"yyyy-MM-dd HH:mm:ss.SSS"
//
//        @ColumnInfo(name = "version")
//        var version: Long = 0,
//
//        @BkDb.OPType
//        @ColumnInfo(name = "operator_type")
//        var operatorType: Int = 0
//
//
//) : Parcelable {
//
//
//
//    constructor(parcel: Parcel) : this(
//            parcel.readString(),
//            parcel.readString(),
//            parcel.readInt(),
//            parcel.readString(),
//            parcel.readString(),
//            parcel.readString(),
//            parcel.readLong(),
//            parcel.readInt())
//
//
//    override fun hashCode(): Int {
//        return imageName.hashCode()
//    }
//
//    override fun equals(other: Any?): Boolean {
//        return TextUtils.equals(imageName, (other as Image).imageName)
//    }
//
//
//    @Retention(AnnotationRetention.SOURCE)
//    @IntDef(IMAGE_STATE_UPLOAD_NO, IMAGE_STATE_UPLOAD_YES)
//    annotation class UploadState
//
//    override fun writeToParcel(parcel: Parcel, flags: Int) {
//        parcel.writeString(imageName)
//        parcel.writeString(foreignId)
//        parcel.writeInt(uploadState)
//        parcel.writeString(userId)
//        parcel.writeString(addTime)
//        parcel.writeString(updateTime)
//        parcel.writeLong(version)
//        parcel.writeInt(operatorType)
//    }
//
//    override fun describeContents(): Int {
//        return 0
//    }
//
//    companion object CREATOR : Parcelable.Creator<Image> {
//        override fun createFromParcel(parcel: Parcel): Image {
//            return Image(parcel)
//        }
//
//        override fun newArray(size: Int): Array<Image?> {
//            return arrayOfNulls(size)
//        }
//    }
//
//
//}
//
//
