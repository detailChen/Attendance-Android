package com.attendance.bk.adapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.attendance.bk.view.recyclerviewdraghelper.ItemTouchHelperAdapter
import com.attendance.bk.view.recyclerviewdraghelper.ItemTouchHelperViewHolder
import com.attendance.bk.view.recyclerviewdraghelper.SimpleItemTouchHelperCallback
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * RecyclerView 拖动排序Adapter
 *
 *
 * 注意：不同ItemViewType的项目不可交换位置,因此需要保证要交换的位置是连续的，不然拖动看起来很奇怪
 *
 */
abstract class DragSortAdapter<T, VH : DragSortAdapter.DragSortHolder>(recyclerView: RecyclerView) : RecyclerView.Adapter<VH>(),
    ItemTouchHelperAdapter {


    protected val context: Context
    private val itemTouchHelper: ItemTouchHelper = ItemTouchHelper(SimpleItemTouchHelperCallback(this))
    protected val mData: MutableList<T> = LinkedList()

    init {
        itemTouchHelper.attachToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    fun updateData(data: List<T>?, addMore: Boolean) {
        val oldCount = mData.size
        if (!addMore) {
            mData.clear()
        }
        if (data != null) {
            mData.addAll(data)
            if (addMore) {
                notifyItemRangeInserted(oldCount, data.size)
            } else {
                notifyDataSetChanged()
            }
        } else {
            notifyDataSetChanged()
        }
    }

    val allData: List<T>
        get() = mData

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        var from = fromPosition
        var to = toPosition
        val maxPos = mData.size - 1
        from = max(0, min(maxPos, from))
        to = max(0, min(maxPos, to))
        mData.add(to, mData.removeAt(from))
        notifyItemMoved(from, to)
        return true
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onItemDismiss(position: Int) {
        mData.removeAt(position)
        notifyItemRemoved(position)
    }

    open class DragSortHolder(protected var adapter: DragSortAdapter<*, *>, itemView: View) : RecyclerView.ViewHolder(itemView),
        ItemTouchHelperViewHolder {
        override fun onItemSelected() {
        }

        override fun onItemClear() {
            if (adapterPosition >= 0) {
                try {
                    adapter.notifyItemChanged(adapterPosition)
                } catch (e: Exception) { // null
                }
            }
        }

    }
}