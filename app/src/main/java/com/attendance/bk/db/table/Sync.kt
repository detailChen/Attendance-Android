package com.attendance.bk.db.table


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.attendance.bk.db.TableName
import com.attendance.bk.utils.UUIDUtil

/**
 * 同步信息表
 *
 * @author Chen xuJie
 * @since 2018-07-21
 */
@Entity(tableName = TableName.TB_NAME_SYNC)
data class Sync(

        @PrimaryKey
        @ColumnInfo(name = "sync_id")
        var syncId: String = "",

        @ColumnInfo(name = "user_id")
        var userId: String = "",

        // 同步时间
        @ColumnInfo(name = "sync_time")
        var syncTime: Long = 0
)
