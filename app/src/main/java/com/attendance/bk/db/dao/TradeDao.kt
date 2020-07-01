package com.attendance.bk.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.attendance.bk.bean.MonthOutInMoney
import com.attendance.bk.bean.TradeItemData
import com.attendance.bk.db.BkDb
import com.attendance.bk.db.table.Trade
import com.attendance.bk.utils.DateUtil
import com.attendance.bk.db.TRADE_TYPE_IN
import io.reactivex.Single
import java.util.*

/**
 * Created by Chen xuJie on 2018/12/17/017
 */
@Dao
abstract class TradeDao {

    @Insert
    abstract fun insert(trade: Trade)

    @Update
    abstract fun update(trade: Trade)

    @Update
    abstract fun update(tradeList: List<Trade>)


    fun addVisitorUserTrade(trade: Trade): Single<Boolean> {
        return Single.create {
            try {
                val currTime = DateUtil.updateTime
                val version = System.currentTimeMillis()
                trade.addTime = currTime
                trade.updateTime = currTime
                trade.version = version
                trade.operatorType = BkDb.TYPE_ADD
                insert(trade)
                it.onSuccess(true)
            } catch (e: Exception) {
                it.onSuccess(false)
                throw RuntimeException("addVisitorUserTrade failed->", e)
            }
        }
    }

    fun addModifyTrade(trade: Trade?, isModify: Boolean = false) {
        BkDb.instance.runInTransaction {
            trade?.apply {
                if (isModify) update(this) else insert(this)
            }
//            imageList?.apply {
//                val imageDao = BkDb.instance.imageDao()
//                if (!isModify) {
//                    imageDao.insert(this)
//                } else {
//                    for (image in this) {
//                        val localImage = imageDao.queryImageByName(image.imageName)
//                        if (localImage == null) {
//                            imageDao.insert(image)
//                        } else {
//                            imageDao.update(image)
//                        }
//                    }
//                }
//            }
        }
    }

    /**
     * 读取年和月的交易流水数据
     *
     * @param groupId
     * @param yearMonth
     * @param type          交易流水类型  总的合计，应收款，应付款，
     * @param queryDayCount 查询的天数
     * @param lastTradeDate 该时间段内最后一天
     * @return
     */
    fun getYearMonthTradeList(
        userId: String,
        bookId: String,
        yearMonth: Date,
        queryDayCount: Int,
        lastTradeDate: String
    ): Single<List<TradeItemData>> {
        return Single.create { emitter ->
            val cal = DateUtil.dayZeroTimeCal
            cal.time = yearMonth
            val day = cal.get(Calendar.DAY_OF_MONTH)

            val sStart = if (day == 31) {//查询的是某一年的数据
                cal.set(Calendar.MONTH, Calendar.JANUARY)
                cal.set(Calendar.DAY_OF_MONTH, 1)
                DateUtil.formatDate(cal.time)
            } else {//查询的是某个月的数据
                cal.set(Calendar.DAY_OF_MONTH, 1)
                DateUtil.formatDate(cal.time)
            }
            val sEnd: String = lastTradeDate

            val data: List<TradeItemData> =
                getTradeList(userId, bookId, sStart, sEnd, queryDayCount)
            emitter.onSuccess(data)
        }
    }

    @Query(
        """SELECT t.trade_id,t.date,t.money,t.book_id,t.trade_type,t.type,t.type_id,t.user_id,t.add_time,t.update_time,
                  bt.bill_id,bt.name,bt.icon,bt.color,t.memo,a.account_id ,a.name as account_name 
            FROM tb_trade AS t  
            LEFT JOIN tb_bill_type AS bt ON t.user_id = bt.user_id AND t.bill_id = bt.bill_id  
            LEFT JOIN tb_account as a ON t.account_id = a.account_id AND t.user_id = a.user_id
            INNER JOIN (
            SELECT DISTINCT t1.date
                            FROM tb_trade t1
                            WHERE t1.user_id = :userId
                                AND t1.date < :sEnd
                                AND t1.date >= :sStart
                                AND t1.book_id = :bookId
                                AND length(t1.bill_id) >= 4
                                AND t1.operator_type != 2
                            ORDER BY t1.date DESC
                            LIMIT :queryDayCount
                            ) d ON t.date = d.date 
            WHERE t.user_id = :userId 
              AND t.book_id =:bookId 
              AND t.operator_type != 2 
              ORDER BY t.date DESC,t.add_time DESC"""
    )
    abstract fun getTradeList(
        userId: String,
        bookId: String,
        sStart: String,
        sEnd: String,
        queryDayCount: Int
    ): List<TradeItemData>


    fun getYearMonthTradeTotalMoney(
        userId: String,
        bookId: String,
        yearMonth: Date
    ): Single<Double> {
        return Single.create { emitter ->
            val cal = DateUtil.dayZeroTimeCal
            cal.time = yearMonth
            val day = cal.get(Calendar.DAY_OF_MONTH)

            val sStart: String
            val sEnd: String
            if (day == 31) {//查询的是某一年的数据
                cal.set(Calendar.MONTH, Calendar.JANUARY)
                cal.set(Calendar.DAY_OF_MONTH, 1)
                sStart = DateUtil.formatDate(cal.time)
                cal.add(Calendar.YEAR, 1)
                cal.add(Calendar.DAY_OF_MONTH, -1)
                sEnd = DateUtil.formatDate(cal.time)
            } else {//查询的是某个月的数据
                cal.set(Calendar.DAY_OF_MONTH, 1)
                sStart = DateUtil.formatDate(cal.time)
                cal.add(Calendar.MONTH, 1)
                cal.add(Calendar.DAY_OF_MONTH, -1)
                sEnd = DateUtil.formatDate(cal.time)
            }

            val monthOutInMoneyList = getMonthTradeMoney(userId,bookId,sStart,sEnd)
            monthOutInMoneyList.forEach {

                if (it.tradeType == TRADE_TYPE_IN){

                }
            }
//            val monthOutInMoney = Pair<Double,Double>()

        }
    }

    @Query(
        """select TOTAL(t.money),t.trade_type
             FROM tb_trade t
             WHERE t.user_id =:userId
             AND t.book_id =:bookId
             AND t.date >= :start
             AND t.date <= :end
             AND length(t.bill_id) > 4
             AND t.operator_type != 2 
             group by t.trade_type """
    )
    abstract fun getMonthTradeMoney(
        userId: String, bookId: String, start: String, end: String): List<MonthOutInMoney>
}
