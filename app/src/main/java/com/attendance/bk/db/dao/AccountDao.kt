package com.attendance.bk.db.dao

import android.text.TextUtils
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.attendance.bk.bean.AccountItem
import com.attendance.bk.bean.DayTotalData
import com.attendance.bk.bean.MonthTotalData
import com.attendance.bk.bean.TradeItemData
import com.attendance.bk.db.BkDb
import com.attendance.bk.db.table.Account
import com.attendance.bk.utils.DateUtil
import io.reactivex.Single

/**
 * Created by Chen xuJie on 2018/12/17/017
 */
@Dao
abstract class AccountDao {
    @Insert
    abstract fun insert(account: Account)

    @Insert
    abstract fun insert(accountList: List<Account>)

    @Update
    abstract fun update(account: Account)


    @Update
    abstract fun update(accountList: List<Account>)

    @Query("select * from tb_account where  user_id = :userId and operator_type != 2 order by order_num")
    abstract fun getAllAccount(userId: String): Single<List<Account>>


    @Query("select * from tb_account where user_id = :userId and account_id = :accountId and operator_type != 2")
    abstract fun queryForAccountId(userId: String, accountId: String): Single<Account>

    @Query("select * from tb_account where user_id = :userId and account_id = :accountId ")
    abstract fun getAccountById(userId: String, accountId: String): Account?

    @Query("select account_id from tb_account where user_id = :userId and name = :name and operator_type != 2")
    abstract fun querySameNameAccountId(userId: String, name: String): String?

    fun addVisitorUserAccount(account: Account): Single<Any> {
        return Single.create {
            val sameNameAccountId = querySameNameAccountId(account.userId, account.name)
            if (sameNameAccountId != null && TextUtils.equals(sameNameAccountId, account.accountId)
                    .not()
            ) {
                it.onSuccess(-1)
            } else {
                val currTime = DateUtil.updateTime
                val currVersion = System.currentTimeMillis()
                account.addTime = currTime
                account.updateTime = currTime
                account.version = currVersion
                account.operatorType = BkDb.TYPE_ADD
                insert(account)
                it.onSuccess(account)
            }
        }
    }


    fun addModifyAccount(account: Account?, isModify: Boolean) {
        account?.run {
            if (isModify) update(account) else insert(account)
        }
    }

    @Query(
        """select ifnull(tf.account_money,0) as account_money,a.account_id,a.name,a.account_type_id,a.account_type_name,a.account_type_icon,a.memo 
                    from tb_account as a 
                    left join 
                    (select TOTAL(CASE t.trade_type WHEN 0 THEN money ELSE -money END) as account_money,t.account_id as account_id 
                    from tb_trade as t 
                    where t.user_id =:userId 
                        and t.account_id in(:accountIds) 
                        and t.operator_type != 2 
                        group by account_id) tf 
                    on a.account_id = tf.account_id
                    where a.user_id = :userId
                    and a.account_id in (:accountIds)
                    and a.operator_type != 2
                    order by a.order_num
    """
    )
    abstract fun getAccountListMoney(
        userId: String,
        accountIds: List<String>
    ): Single<List<AccountItem>>


    @Query(
        """SELECT date(t.date, 'start of month') AS trade_month,
                        TOTAL(CASE t.trade_type WHEN 0 THEN t.money ELSE 0 END) AS trade_in,
                        TOTAL(CASE t.trade_type WHEN 1 THEN t.money ELSE 0 END) AS trade_out,
                        COUNT(t.trade_id) AS trade_count, COUNT(DISTINCT t.date) AS day_count
                    FROM tb_trade t
                    WHERE t.operator_type != 2
                        AND t.user_id= :userId
                        AND t.account_id = :accountId
                        AND t.date <= :date
                    GROUP BY date(t.date, 'start of month')
                    ORDER BY t.date DESC
    """
    )
    abstract fun getAccountMonthTotalData(
        userId: String,
        accountId: String,
        date: String
    ): Single<List<MonthTotalData>>


    @Query(
        """SELECT t.date, TOTAL(CASE t.trade_type WHEN 0 THEN t.money ELSE -t.money END) AS day_money,
                   COUNT(t.trade_id) AS trade_count
                    FROM tb_trade t
                    WHERE t.user_id= :userId
                        AND t.account_id = :accountId
                        AND t.operator_type != 2
                        AND t.date >= :monthStart
                        AND t.date <= :monthEnd
                    GROUP BY t.date
                    ORDER BY t.date DESC
    """
    )
    abstract fun getAccountDayTotalData(
        userId: String,
        accountId: String,
        monthStart: String,
        monthEnd: String
    ): Single<List<DayTotalData>>


    @Query(
        """SELECT t.trade_id,t.date,t.money,t.book_id,t.trade_type,t.type,t.type_id,t.user_id,t.add_time,t.update_time,
                  bt.bill_id,bt.name,bt.click_icon as icon,t.memo,a.account_id ,a.name as account_name 
            FROM tb_trade AS t  
            LEFT JOIN tb_bill_type AS bt ON t.user_id = bt.user_id AND t.bill_id = bt.bill_id  
            LEFT JOIN tb_account as a ON t.account_id = a.account_id AND t.user_id = a.user_id
            WHERE t.user_id = :userId
                AND t.date < :sEnd
                AND t.date >= :sStart
                AND t.account_id = :accountId
                AND t.operator_type != 2
              ORDER BY t.date DESC,t.add_time DESC"""
    )
    abstract fun getAccountTradeData(
        userId: String,
        accountId: String,
        sStart: String,
        sEnd: String
    ): Single<List<TradeItemData>>

    @Query(
        """select ifnull(TOTAL(CASE t.trade_type WHEN 0 THEN money ELSE -money END),0.0) 
                    from tb_trade as t 
                    where t.user_id =:userId 
                        and t.account_id  = :accountId 
                        and t.operator_type != 2 
    """
    )
    abstract fun getAccountLeftMoney(userId: String, accountId: String): Single<Double>
}