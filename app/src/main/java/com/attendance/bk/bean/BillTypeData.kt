package com.attendance.bk.bean

import com.chad.library.adapter.base.entity.MultiItemEntity

/**
 * Created by Chen xuJie on 2019/7/11.
 */
data class BillTypeData(val name: String = "", val icons: List<Icon> = ArrayList())


data class Icon(val clickIcon: String = "", val normalIcon: String = "")


data class BTItem(
    val groupName: String? = null,
    val groupIcon: Icon? = null,
    val type: Int = TYPE_GROUP_NAME
) : MultiItemEntity {


    override fun getItemType(): Int {
        return type
    }

    companion object {

        const val TYPE_GROUP_NAME = 0
        const val TYPE_GROUP_ICON = 1
    }
}


