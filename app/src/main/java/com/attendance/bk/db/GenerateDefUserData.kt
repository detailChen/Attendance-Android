package com.attendance.bk.db

import com.attendance.bk.BkApp
import com.attendance.bk.R
import com.attendance.bk.db.table.*
import com.attendance.bk.utils.CoverUtil
import com.attendance.bk.utils.DateUtil
import com.attendance.bk.utils.SPKey
import com.attendance.bk.utils.UUIDUtil
import com.blankj.utilcode.util.SPStaticUtils
import com.fasterxml.jackson.core.type.TypeReference
import io.reactivex.Single
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.atomic.AtomicBoolean


/**
 * 生成默认用户的数据
 * Created by Chen xuJie on 2018/12/19/019
 */
object GenerateDefUserData {


    fun generateDefUserData(): Single<Boolean> {
        return Single.create<Boolean> {
            BkDb.instance.runInTransaction {
                val res = AtomicBoolean()
                val currTime = DateUtil.updateTime
                val version = 1L
                try {
                    val userId = addDefUser(currTime)
                    addDefBook(userId, currTime, version)
                    addDefAccount(userId, currTime, version)
                    res.set(true)
                } catch (e: Exception) {
                    res.set(false)
                    throw RuntimeException(e)
                }
                it.onSuccess(res.get())
            }
        }
    }

    private fun addDefUser(currTime: String): String {
        val user = User(
            userId = UUIDUtil.uuid(),
            nickname = "游客",
            userIsVisitor = 0,
            addTime = currTime,
            updateTime = currTime,
            operatorType = BkDb.TYPE_ADD
        )
        BkDb.instance.userDao().insert(user)


        //保存當前用戶id
        SPStaticUtils.put(SPKey.SP_KEY_USER_ID, user.userId)

        return user.userId
    }


    /**
     * 添加默认用户的userExtra数据
     */
    private fun addDefUserExtra(userId: String, bookId: String) {
        val userExtra = UserExtra(
            userId = userId,
            currBookId = bookId
        )
        BkDb.instance.userExtraDao().insert(userExtra)
    }


    /**
     * 添加默认账本
     *
     * @param userId
     * @param version
     * @return
     */
    private fun addDefBook(userId: String, time: String, version: Long) {
        val book = Book(
            bookId = UUIDUtil.uuid(),
            name = "默认账本",
            cover = CoverUtil.randomBookCover,
            orderNum = 1,
            bookTypeId = "1",
            userId = userId,
            addTime = time,
            updateTime = time,
            version = version,
            operatorType = BkDb.TYPE_ADD
        )
        BkDb.instance.bookDao().insert(book)
        addDefBillType(userId, book.bookId, time, version)
        addDefUserExtra(userId, book.bookId)//设置UserExtra
    }


    private fun addDefBillType(
        userId: String,
        bookId: String,
        updateTime: String,
        version: Long
    ): Int {
        val count = 0
        val btOutIs: InputStream? = BkApp.appContext.resources.openRawResource(R.raw.def_bt_out)
        val btInIs: InputStream? = BkApp.appContext.resources.openRawResource(R.raw.def_bt_in)
        if (btOutIs == null || btInIs == null) {
            return 0
        }

        try {
            val btOutList = BkApp.bkJackson.readValue<List<BillType>>(
                btOutIs, object : TypeReference<List<BillType>>() {})
            for ((order, billType) in btOutList.withIndex()) {
                val bt = newBillType(billType, userId, bookId, version, order, updateTime)
                BkDb.instance.billTypeDao().insert(bt)
            }
            val btInList = BkApp.bkJackson.readValue<List<BillType>>(
                btInIs, object : TypeReference<List<BillType>>() {})
            for ((order, billType) in btInList.withIndex()) {
                val bt = newBillType(billType, userId, bookId, version, order, updateTime)
                BkDb.instance.billTypeDao().insert(bt)
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return count
    }


    private fun newBillType(
        billType: BillType, userId: String, bookId: String, version: Long, order: Int, time: String
    ): BillType {
        return BillType(
            billId = UUIDUtil.uuid(),
            name = billType.name,
            bookId = bookId,
            clickIcon = billType.clickIcon,
            normalIcon = billType.normalIcon,
            orderNum = order,
            userId = userId,
            addTime = time,
            updateTime = time,
            version = version,
            operatorType = BkDb.TYPE_ADD
        )
    }

    private fun addDefAccount(userId: String, currTime: String, version: Long) {
        val accountIs = BkApp.appContext.resources.openRawResource(R.raw.account_def)
        val accountList: List<Account> =
            BkApp.bkJackson.readValue(accountIs, object : TypeReference<List<Account>>() {
            })
        for (account in accountList) {
            account.accountId = UUIDUtil.uuid()
            account.userId = userId
            account.addTime = currTime
            account.updateTime = currTime
            account.operatorType = BkDb.TYPE_ADD
            account.version = version
        }
        BkDb.instance.accountDao().insert(accountList)
    }

}
