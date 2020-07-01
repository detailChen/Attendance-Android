package com.attendance.bk.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.attendance.bk.db.BkDb
import com.attendance.bk.db.table.*
import com.attendance.bk.sync.SyncJsonObject
import com.attendance.bk.utils.UUIDUtil
import com.blankj.utilcode.util.LogUtils

/**
 * 用于数据同步过程中数据查询、合并等操作

 * @author ChenXuJie
 * @since 2019/8/3
 **/
@Dao
abstract class DataSyncDao {


    /**
     * 合并后端返回的同步数据
     * @param obj 后端返回的同步数据
     */
    @Transaction
    open fun mergeSyncJsonObject(userId: String, obj: SyncJsonObject): Boolean {
        val syncData = obj.syncData ?: SyncJsonObject.SyncData()
        try {
            mergeUser(syncData.userList)
            mergeBook(syncData.bookList)
            mergeTrade(syncData.tradeList)
            mergeBillType(syncData.billTypeList)
            mergeAccount(syncData.accountList)
//            mergeTransfer(syncData.transferList)
//            mergeLoan(syncData.loanList)

            // 合并完成则插入一条最新的同步版本号，为下次同步做准备
            BkDb.instance.syncDao().insert(Sync(UUIDUtil.uuid(), userId, obj.syncVersion))
            return true
        } catch (e: Exception) {
            LogUtils.e("mergeSyncJsonObject failed->", e)
            return false
        }
    }


    //------------------ 下面的几个方法用于快速查询本地是否有相关记录 ----------------------

    @Query("SELECT update_time FROM tb_user WHERE user_id = :userId")
    protected abstract fun queryLocalUserSimple(userId: String): String?


    @Query("SELECT update_time FROM tb_book WHERE book_id = :bookId")
    protected abstract fun queryLocalBookSimple(bookId: String): String?

    @Query("SELECT update_time FROM tb_trade WHERE trade_id = :tradeId")
    protected abstract fun queryLocalTradeSimple(tradeId: String): String?

    @Query("SELECT update_time FROM tb_bill_type WHERE bill_id = :billId")
    protected abstract fun queryLocalBillTypeSimple(billId: String): String?


    @Query("SELECT update_time FROM tb_account WHERE account_id = :accountId")
    protected abstract fun queryLocalAccountSimple(accountId: String): String?

//    @Query("SELECT update_time FROM tb_transfer WHERE transfer_id = :transferId")
//    protected abstract fun queryLocalTransferSimple(transferId: String): String?
//
//    @Query("SELECT update_time FROM tb_loan WHERE loan_id = :loanId")
//    protected abstract fun queryLocalLoanSimple(loanId: String): String?


    //------------------ 下面的几个方法用于合并服务端的流水数据 ----------------------

    private fun mergeUser(users: Iterable<User>?) {
        if (users == null) {
            return
        }

        val userDao = BkDb.instance.userDao()
        users.forEach {
            val localUpdateTime = queryLocalUserSimple(it.userId)
            if (localUpdateTime == null) {
                userDao.insert(it)
            } else if (localUpdateTime < it.updateTime) {
                userDao.update(it)
            }
        }
    }


    private fun mergeBook(books: Iterable<Book>?) {
//        updateChangedBookVersion(groupIds, serverTime + 1, lastSyncVersion)
        if (books == null) {
            return
        }
        val bookDao = BkDb.instance.bookDao()
        books.forEach {
            val localUpdateTime = queryLocalBookSimple(it.bookId)
            if (localUpdateTime == null) {
                bookDao.insert(it)
            } else if (localUpdateTime < it.updateTime) {
                bookDao.update(it)
            }
        }
    }

    private fun mergeTrade(trades: Iterable<Trade>?) {
//        updateChangedTradeVersion(groupIds, serverTime + 1, lastSyncVersion)
        if (trades == null) {
            return
        }

        val tradeDao = BkDb.instance.tradeDao()
        trades.forEach {
            val localUpdateTime = queryLocalTradeSimple(it.tradeId)

            if (localUpdateTime == null) {
                tradeDao.insert(it)
            } else if (localUpdateTime < it.updateTime) {
                tradeDao.update(it)
            }
        }
    }


    private fun mergeBillType(billTypes: Iterable<BillType>?) {
//        updateChangedBillTypeVersion(groupIds, serverTime + 1, lastSyncVersion)
        if (billTypes == null) {
            return
        }

        val billTypeDao = BkDb.instance.billTypeDao()
        billTypes.forEach {
            val localUpdateTime = queryLocalBillTypeSimple(it.billId)

            if (localUpdateTime == null) {
                billTypeDao.insert(it)
            } else if (localUpdateTime < it.updateTime) {
                billTypeDao.update(it)
            }
        }
    }


    private fun mergeAccount(accounts: Iterable<Account>?) {
        if (accounts == null) {
            return
        }

        val accountDao = BkDb.instance.accountDao()
        accounts.forEach {
            val localUpdateTime = queryLocalAccountSimple(it.accountId)

            if (localUpdateTime == null) {
                accountDao.insert(it)
            } else if (localUpdateTime < it.updateTime) {
                accountDao.update(it)
            }
        }
    }

//    private fun mergeTransfer(transfers: Iterable<Transfer>?) {
//        if (transfers == null) {
//            return
//        }
//
//        val transferDao = BkDb.instance.transferDao()
//        transfers.forEach {
//            val localUpdateTime = queryLocalTransferSimple(it.transferId)
//
//            if (localUpdateTime == null) {
//                transferDao.insert(it)
//            } else if (localUpdateTime < it.updateTime) {
//                transferDao.update(it)
//            }
//        }
//    }
//
//    private fun mergeLoan(loans: Iterable<Loan>?) {
//        if (loans == null) {
//            return
//        }
//
//        val loanDao = BkDb.instance.loanDao()
//        loans.forEach {
//            val localUpdateTime = queryLocalLoanSimple(it.loanId)
//
//            if (localUpdateTime == null) {
//                loanDao.insert(it)
//            } else if (localUpdateTime < it.updateTime) {
//                loanDao.update(it)
//            }
//        }
//    }

}