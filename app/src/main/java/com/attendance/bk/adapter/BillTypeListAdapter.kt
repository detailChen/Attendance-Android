package com.attendance.bk.adapter

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IntDef
import androidx.recyclerview.widget.RecyclerView
import com.attendance.bk.R
import com.attendance.bk.db.BkDb
import com.attendance.bk.db.table.BillType
import com.attendance.bk.utils.workerThreadChange
import com.attendance.bk.view.BkImageView
import com.blankj.utilcode.util.LogUtils
import java.util.*


/**
 * Created by Chen xuJie on 2019/4/19.
 */
class BillTypeListAdapter(recyclerView: RecyclerView) : DragSortAdapter<BillType, BillTypeListAdapter.BtHolder>(recyclerView) {


    @IntDef(MODE_NORMAL, MODE_EDIT)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Mode

    private val mDeleteBtList = ArrayList<BillType>(1)
    private var mListener: OnItemClickListener<BillType>? = null

    private var mSelPos = -1

    private var mMode = MODE_NORMAL

    private var hasChangedOrder = false

    interface OnItemClickListener<T> {

        fun onItemClick(t: T)

        fun onAddClick()
    }

    fun setOnItemClickListener(listener: OnItemClickListener<BillType>) {
        this.mListener = listener
    }

    fun reSetSelectPos() {
        mSelPos = -1
    }

    /**
     * 获取当前模式
     */
    @Mode
    fun getMode(): Int {
        return mMode
    }

    fun setMode(@Mode mode: Int) {
        if (this.mMode == mode) {
            return
        }
        setMode(mode, -1)
    }

    /**
     * 模式变更！
     */
    fun setMode(@Mode mode: Int, except: Int) {
        this.mMode = mode
        if (mode == MODE_NORMAL) {
            saveBillTypeOrder()
            hasChangedOrder = false
            notifyDataSetChanged()
        } else {
            if (except >= 0 && except < mData.size) {
                notifyItemRangeChanged(0, except)
                notifyItemRangeChanged(except + 1, mData.size - except)
            } else {
                notifyDataSetChanged()
            }
        }
    }


    @SuppressLint("CheckResult")
    private fun saveBillTypeOrder() {
        BkDb.instance.billTypeDao().saveBtOrder(mData).workerThreadChange()
                .subscribe({

                }, {
                    LogUtils.e("saveBillTypeOrder failed->", it)
                })
    }


    fun updateData(dataList: List<BillType>?) {
        if (dataList == null) {
            return
        }
        mData.clear()
        mData.addAll(mDeleteBtList)
        mData.addAll(dataList)
        notifyDataSetChanged()
    }

    /**
     * 此种情况，是为了处理编辑类别被删除的流水时，为了防止被删除的类别找不到类别，
     * 所以把删除的类别临时加上去方便继续编辑
     *
     * @param billType
     */
    fun addDeleteData(billType: BillType) {
        mDeleteBtList.clear()
        mDeleteBtList.add(billType)
    }

    /**
     * 设置选中的类别，添加时，传null，默认选中第一个
     *
     * @param btId
     */
    fun setSelPos(btId: String?) {
        if (TextUtils.isEmpty(btId)) {
            mSelPos = -1
            return
        }
        for (i in mData.indices) {
            if (mData[i].billId == btId) {
                mSelPos = i
                break
            }
        }
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BtHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.view_bt_list_item, parent, false)
        val holder = BtHolder(this, view)
        holder.itemView.setOnClickListener {
            val pos = holder.adapterPosition
            if (mListener != null) {
                if (pos == mData.size) {
                    mListener?.onAddClick()
                } else {
                    mListener?.onItemClick(mData[pos])
                    mSelPos = pos
                    notifyDataSetChanged()
                }
            }
        }

        return holder
    }

    override fun onBindViewHolder(holder: BtHolder, position: Int) {
        if (getItemViewType(position) == 0) {
            val bt = mData[position]
            holder.btName.text = bt.name
            if (mSelPos == position) {
                holder.btIcon.setImageState(BkImageView.State().name(bt.icon).fillColor("#e3e3e3"))
            } else {
                holder.btIcon.removeFill()
                holder.btIcon.setImageName(bt.icon)
            }
//            holder.btDelete.visibility = if (mMode == MODE_EDIT) View.VISIBLE else View.INVISIBLE
        } else {
            holder.btName.text = "管理"
            holder.btIcon.setImageName("bt_shezhi")
        }
    }


    override fun getItemCount(): Int {
        return mData.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == mData.size) 1 else 0
    }


    class BtHolder(val mAdapter: BillTypeListAdapter, itemView: View) : DragSortAdapter.DragSortHolder(mAdapter, itemView) {

        var btIcon: BkImageView = itemView.findViewById(R.id.bt_icon)
        var btName: TextView = itemView.findViewById(R.id.bt_name)
        var btDelete: ImageView = itemView.findViewById(R.id.bill_type_delete)

        override fun onItemSelected() {
            if (mAdapter.getMode() != MODE_EDIT) {
                mAdapter.setMode(MODE_EDIT, adapterPosition)
            }
        }
    }

    companion object {

        const val MODE_NORMAL = 0
        const val MODE_EDIT = 1
    }
}
