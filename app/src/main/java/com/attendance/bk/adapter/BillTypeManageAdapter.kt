package com.attendance.bk.adapter

import android.annotation.SuppressLint
import android.widget.TextView
import com.attendance.bk.BkApp
import com.attendance.bk.R
import com.attendance.bk.bus.BillTypeEvent
import com.attendance.bk.db.BkDb
import com.attendance.bk.db.table.BillType
import com.attendance.bk.page.billtype.AddBillTypeActivity
import com.attendance.bk.utils.DrawableUtil
import com.attendance.bk.utils.Optional
import com.attendance.bk.utils.workerThreadChange
import com.attendance.bk.view.swipeRevealLayout.SwipeRevealLayout
import com.attendance.bk.view.swipeRevealLayout.ViewBinderHelper
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * Created by Chen xuJie on 2019/12/13.
 */
class BillTypeManageAdapter(layoutResId: Int) :
    BaseItemDraggableAdapter<BillType, BaseViewHolder>(layoutResId, null) {


    private val binderHelper = ViewBinderHelper()

    override fun convert(helper: BaseViewHolder, item: BillType) {
        //绑定
        binderHelper.bind(helper.getView(R.id.swipe_layout), item.billId)
        helper.setImageDrawable(R.id.bt_icon, DrawableUtil.getDrawableByName(item.clickIcon))
        helper.setText(R.id.bt_name, item.name)
        helper.getView<TextView>(R.id.tv_delete).setOnClickListener {
            deleteBillType(item, helper.adapterPosition)
        }

        val swipeRevealLayout = helper.itemView as SwipeRevealLayout
        swipeRevealLayout.setOnSingleTapListener {
            if (swipeRevealLayout.isClosed) {
                mContext.startActivity(AddBillTypeActivity.modifyBillType(item))
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun deleteBillType(billType: BillType, pos: Int) {
        BkApp.apiService.deleteBillType(billType).map<Optional<BillType>> {
            if (it.isResultOk()) {
                if (it.data == null) {
                    Optional.empty<BillType>()
                } else {
                    BkDb.instance.billTypeDao().deleteBillType(it.data)
                    Optional.of(it.data)
                }
            } else {
                ToastUtils.showShort(it.desc)
                Optional.empty<BillType>()
            }
        }.workerThreadChange().subscribe({
            if (it.isPresent) {
                data.remove(billType)
                notifyItemRemoved(pos)
                BkApp.eventBus.post(BillTypeEvent(billType, BkDb.TYPE_DELETE))
            }
        }, {
            LogUtils.e("deleteBillType failed->", it)
        })
    }

    fun closeLayout() {
        data.forEach {
            binderHelper.closeLayout(it.billId)
        }
    }
}