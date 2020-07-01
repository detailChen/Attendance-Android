package com.attendance.bk.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.attendance.bk.db.table.UserExtra
import io.reactivex.Single

/**
 * Created by CXJ
 * Created date 2019/1/18/018
 */
@Dao
abstract class UserExtraDao {

    @Insert
    abstract fun insert(userExtra: UserExtra)

    @Update
    abstract fun update(userExtra: UserExtra)

    @Query("select * from tb_user_extra where user_id = :userId")
    abstract fun getUserExtra(userId: String): UserExtra


    fun updateUserExtra(userExtra: UserExtra): Single<Boolean> {
        return Single.create { emitter ->
            update(userExtra)
            emitter.onSuccess(true)
        }
    }

}
