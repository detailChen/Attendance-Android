package com.attendance.bk.bean

import com.chad.library.adapter.base.entity.MultiItemEntity

/**
 * Created by CXJ
 * Created date 2019/4/24/024
 */
class TradeListData<T>(val data: T?, val date: String?, private val type: Int) : MultiItemEntity {

    override fun getItemType(): Int {
        return type
    }

    companion object {

        const val TYPE_TOP = 0//块内的第一条数据，一般是日期
        const val TYPE_CENTER = 1//块内的中间数据
        const val TYPE_BOTTOM = 2//块内的最后一条数据
    }
}
