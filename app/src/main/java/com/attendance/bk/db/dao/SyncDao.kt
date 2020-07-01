package com.attendance.bk.db.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.attendance.bk.db.table.Sync
import io.reactivex.Single

/**
 * Created by Chen xuJie on 2018/12/24/024
 */
@Dao
abstract class SyncDao {

    @Insert
    abstract fun insert(sync: Sync)

    @Update
    abstract fun update(sync: Sync)


    fun getLastVersion(userId: String): Single<Long> {
        return Single.create { subscriber -> subscriber.onSuccess(getLastVersionL(userId)) }
    }

    fun getLastVersionL(userId: String): Long {
        return getLastSyncSucTime(userId) ?: 0L
    }

    @Query("select max(sync_time) from tb_sync where user_id = :userId")
    abstract fun getLastSyncSucTime(userId: String): Long?

}
