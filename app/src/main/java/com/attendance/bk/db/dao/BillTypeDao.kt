package com.attendance.bk.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.attendance.bk.db.BkDb
import com.attendance.bk.db.table.BillType
import com.attendance.bk.utils.DateUtil
import io.reactivex.Single
import java.util.*

/**
 * Created by Chen xuJie on 2018/12/17/017
 */
@Dao
abstract class BillTypeDao {


    @Insert
    abstract fun insert(bt: BillType)

    @Insert
    abstract fun insert(btList: List<BillType>)

    @Update
    abstract fun update(billType: BillType)

    @Update
    abstract fun update(btList: List<BillType>)

    /**
     * 根据Id查找对应的数据,由于类别可能被删除而对应的记账流水记录还在，这样在编辑的时候就找不到对应的类别了，所以这里连删除的类别一起查找
     *
     * @param btId
     * @return
     */
    @Query("""select * from tb_bill_type where bill_id = :btId""")
    abstract fun queryForBtId(btId: String): Single<BillType>


    @Query("""update tb_bill_type set update_time = :updateTime,version = :version,operator_type = 2 where bill_id = :btId""")
    abstract fun deleteById(btId: String, updateTime: String, version: Long)

    /**
     * 检查同名的类别是否存在
     *
     * @param userId
     * @param btId
     * @param name
     * @return
     */
    @Query("select * from tb_bill_type where user_id = :userId and bill_id != :btId and type =:btType and name = :name and operator_type != 2")
    abstract fun getBtByName(userId: String, btId: String, btType:Int,name: String): BillType?

    /**
     * 获取的收支类别
     *
     * @param userId
     * @return
     */
    @Query("select * from tb_bill_type where user_id = :userId and and book_id = :bookId and type =:btType and operator_type != 2 order by order_num")
    abstract fun getBookBtList(userId: String,bookId:String, btType: Int): Single<List<BillType>>


    @Query("select max(order_num) from tb_bill_type where user_id = :userId  and type =:btType  and operator_type != 2")
    abstract fun getMaxOrder(userId: String, btType: Int): Int


    fun addModifyBillType(bt: BillType?, isModify: Boolean) {
        bt?.also {
            if (isModify) update(it) else insert(it)
        }
    }

    fun checkBtDuplicateName(bt: BillType): Single<Boolean> {
        return Single.create {
            val oldBt = getBtByName(bt.userId, bt.billId, bt.type, bt.name)
            it.onSuccess(oldBt != null)
        }
    }


    fun deleteBillType(billType: BillType?) {
        billType?.also { update(it) }
    }


    fun saveBtOrder(btList: List<BillType>?): Single<Int> {
        return if (btList == null || btList.isEmpty()) {
            Single.just(0)
        } else BkDb.instance.syncDao().getLastVersion(btList[0].userId)
            .map { lastVersion ->
                val time = Date()
                for (i in btList.indices) {
                    val bt = btList[i]
                    val oldBt = queryForBtId(bt.billId).blockingGet()

                    oldBt?.run {
                        updateTime = DateUtil.timeFormat.format(time)
                        operatorType = BkDb.TYPE_MODIFY
                        version = lastVersion + 1
                        orderNum = i
                        update(oldBt)
                    }
                }
                btList.size
            }
    }
}
