package com.boss.bk.db.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.attendance.bk.db.BkDb
import com.attendance.bk.utils.SPKey
import com.blankj.utilcode.util.SPUtils
import com.attendance.bk.db.table.User

/**
 * Created by Chen xuJie on 2018/12/17/017
 */
@Dao
abstract class UserDao {


    @Insert
    abstract fun insert(user: User)

    @Update
    abstract fun update(user: User)

    fun getCurrUser(): User {
        val userId = SPUtils.getInstance().getString(SPKey.SP_KEY_USER_ID)
        return BkDb.instance.userDao().getUserById(userId)
    }

    @Query("select * from tb_user where user_id = :userId and operator_type != 2")
    abstract fun getUserById(userId: String): User

    @Query("select * from tb_user where user_is_visitor = 0 and operator_type != 2")
    abstract fun getVisitorUser(): User?



}
