package com.attendance.bk.bus

/**
 * Created by Chen xuJie on 2020/4/20.
 */

import androidx.annotation.IntDef
import com.attendance.bk.bean.WXLoginResult
import com.attendance.bk.db.BkDb
import com.attendance.bk.db.table.BillType
import com.attendance.bk.db.table.Trade

/**
 * 同步数据事件
 * Created by Chen xuJie on 2019/8/12.
 */
data class SyncDataEvent(@StateType val syncState: Int)

@IntDef(SYNC_FAIL, SYNC_OK)
@Retention(AnnotationRetention.SOURCE)
annotation class StateType

const val SYNC_OK = 1
const val SYNC_FAIL = 0

data class RequestWXInfoSuccessEvent(val wxLoginResult: WXLoginResult)

data class BillTypeEvent(val billType: BillType?, @BkDb.OPType val type: Int)

data class TradeEvent(val trade: Trade?, @BkDb.OPType val type: Int)
