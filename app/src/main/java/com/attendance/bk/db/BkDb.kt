package com.attendance.bk.db


import androidx.annotation.IntDef
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.attendance.bk.BkApp
import com.attendance.bk.db.BkDb.Companion.DB_VERSION
import com.attendance.bk.db.dao.*
import com.attendance.bk.db.table.*
import com.boss.bk.db.dao.*


/**
 * Created by Chen xuJie on 2018/12/16/016
 */
@Database(
    entities = [
        User::class,
        Book::class,
        Trade::class,
        BillType::class,
        Account::class,
        Sync::class,
        UserExtra::class
    ], version = DB_VERSION, exportSchema = false
)
abstract class BkDb : RoomDatabase() {


    @IntDef(TYPE_ADD, TYPE_MODIFY, TYPE_DELETE)
    @Retention(AnnotationRetention.SOURCE)
    annotation class OPType

    abstract fun userDao(): UserDao

    abstract fun bookDao(): BookDao

    abstract fun tradeDao(): TradeDao

    abstract fun accountDao(): AccountDao

    abstract fun billTypeDao(): BillTypeDao

    abstract fun userExtraDao(): UserExtraDao


    abstract fun syncDao(): SyncDao


    abstract fun dataSyncDao(): DataSyncDao

    private class DbCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
        }
    }

    companion object {

        const val TYPE_ADD = 0
        const val TYPE_MODIFY = 1
        const val TYPE_DELETE = 2

        const val DB_VERSION = 3//当前数据库版本


        private var INSTANCE: BkDb? = null
        private val sLock = Any()

        val instance: BkDb
            get() = synchronized(sLock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(BkApp.appContext, BkDb::class.java, "bk.db")
                        .allowMainThreadQueries()
                        .addCallback(DbCallback())
                        .build()
                }
                return INSTANCE as BkDb
            }

    }

}
