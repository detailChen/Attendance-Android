package com.attendance.bk.adapter

import android.view.View
import android.widget.TextView
import com.attendance.bk.R
import com.attendance.bk.bean.BTItem
import com.attendance.bk.bean.Icon
import com.attendance.bk.view.BkImageView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * Created by Chen xuJie on 2019/4/19.
 */
class BillTypeAdapter : BaseMultiItemQuickAdapter<BTItem, BaseViewHolder>(null) {


    private var mSelectIcon: Icon? = null


    init {
        addItemType(BTItem.TYPE_GROUP_NAME, R.layout.view_bt_list_item_group)
        addItemType(BTItem.TYPE_GROUP_ICON, R.layout.view_bt_list_item)
    }

    override fun convert(holder: BaseViewHolder, item: BTItem) {
        if (item.type == BTItem.TYPE_GROUP_NAME) {
            holder.setText(R.id.bt_group_name, item.groupName)
        } else {
            holder.getView<TextView>(R.id.bt_name).visibility = View.GONE
            val btIcon = holder.getView<BkImageView>(R.id.bt_icon)
            if (mSelectIcon?.clickIcon == item.groupIcon.clickIcon &&
                mSelectIcon?.normalIcon == item.groupIcon.normalIcon
            ) {
                btIcon.removeFill()
                btIcon.setImageName(item.groupIcon.clickIcon)
            } else {
                btIcon.setImageState(
                    BkImageView.State().name(item.groupIcon.normalIcon).fillColor("#e3e3e3")
                )
            }
        }
    }


    fun setSelIcon(clickIcon: Icon) {
        mSelectIcon = clickIcon
        notifyDataSetChanged()
    }
}
