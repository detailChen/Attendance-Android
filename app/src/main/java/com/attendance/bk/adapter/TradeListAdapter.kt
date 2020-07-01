package com.attendance.bk.adapter

import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.attendance.bk.R
import com.attendance.bk.bean.TradeItemData
import com.attendance.bk.bean.TradeListData
import com.attendance.bk.utils.BkUtil
import com.attendance.bk.utils.DrawableUtil
import com.attendance.bk.view.BkImageView
import com.boss.bk.db.TRADE_TYPE_IN
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import java.util.*

/**
 * Created by CXJ
 * Created date 2019/4/24/024
 */
class TradeListAdapter : BaseMultiItemQuickAdapter<TradeListData<TradeItemData>, BaseViewHolder>(null) {

    private val dayMoneyMap = HashMap<String, Double>()

    init {
        addItemType(TradeListData.TYPE_TOP, R.layout.view_trade_list_item_date)
        addItemType(TradeListData.TYPE_CENTER, R.layout.view_trade_list_item_data)
        addItemType(TradeListData.TYPE_BOTTOM, R.layout.view_trade_list_item_data)
    }

    fun updateData(tidList: List<TradeItemData>?) {
        if (tidList == null || tidList.isEmpty()) {
            return
        }

        val tidMap = LinkedHashMap<String, MutableList<TradeItemData>>()

        for (tid in tidList) {
            val date = tid.date
            var dateTidList: MutableList<TradeItemData>? = tidMap[date]
            if (dateTidList == null) {
                dateTidList = ArrayList()
                tidMap[date] = dateTidList
            }
            dateTidList.add(tid)

            var dayMoney = dayMoneyMap[date]
            if (dayMoney == null) {
                dayMoneyMap[date] = if (tid.tradeType == TRADE_TYPE_IN) tid.money else -tid.money
            } else {
                if (tid.tradeType == TRADE_TYPE_IN) {
                    dayMoney += tid.money
                } else {
                    dayMoney -= tid.money
                }
                dayMoneyMap[date] = dayMoney
            }
        }

        val dataList = ArrayList<TradeListData<TradeItemData>>()
        for ((date, tid) in tidMap) {
            dataList.add(TradeListData(null, date, TradeListData.TYPE_TOP))
            for (i in tid.indices) {
                if (i == tid.size - 1) {
                    dataList.add(TradeListData(tid[i], null, TradeListData.TYPE_BOTTOM))
                } else {
                    dataList.add(TradeListData(tid[i], null, TradeListData.TYPE_CENTER))
                }
            }
        }
        addData(dataList)
    }

    fun clearData() {
        mData.clear()
        dayMoneyMap.clear()
    }


    override fun convert(helper: BaseViewHolder, item: TradeListData<TradeItemData>) {
        if (item.itemType == TradeListData.TYPE_TOP) {
            helper.setText(R.id.date, item.date)
            val dayTotalMoney = dayMoneyMap[item.date]
            if (dayTotalMoney == null) {
                helper.setText(R.id.day_money, BkUtil.formatMoneyWithTS(0.00))
            } else {
                helper.setText(R.id.day_money, BkUtil.formatMoneyWithTS(dayTotalMoney))
            }
            helper.getView<View>(R.id.top_line).visibility = View.GONE
            helper.getView<View>(R.id.bottom_line).visibility = View.VISIBLE
        } else {
            val tid = item.data!!
            val icon = helper.getView<BkImageView>(R.id.icon)
//            icon.setImageState(BkImageView.State().imageColor(tid.color).name(tid.icon))
            icon.setImageDrawable(DrawableUtil.getDrawableByName(tid.icon))
            helper.setText(R.id.name, tid.name)
            val textMoney = helper.getView<TextView>(R.id.money)
            textMoney.text = BkUtil.formatMoney(if (tid.tradeType == TRADE_TYPE_IN) tid.money else -tid.money)
            //            textMoney.setTextColor(ContextCompat.getColor(mContext, tid.getTradeType() == Trade.TYPE_IN ? R.color.color_in : R.color.color_out));
            helper.setText(R.id.memo, tid.memo)
            helper.getView<View>(R.id.memo).visibility = if (TextUtils.isEmpty(tid.memo)) View.GONE else View.VISIBLE
//            val imageLayout = helper.getView<ImageLayout>(R.id.image_layout)
//            imageLayout.updateImages(tid.imageList)
//            imageLayout.visibility = if (tid.imageList == null || tid.imageList!!.isEmpty()) View.GONE else View.VISIBLE

        }

        when (item.itemType) {
            TradeListData.TYPE_TOP -> {
                helper.itemView.setBackgroundResource(R.drawable.bg_trade_list_item_top)
            }
            TradeListData.TYPE_BOTTOM -> {
                helper.itemView.setBackgroundResource(R.drawable.bg_trade_list_item_bottom)

            }
            else -> {
                helper.itemView.setBackgroundResource(R.drawable.bg_trade_list_item_center)
            }
        }
    }

}
