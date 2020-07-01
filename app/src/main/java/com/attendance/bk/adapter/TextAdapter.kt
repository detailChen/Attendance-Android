package com.attendance.bk.adapter

import android.content.Context
import android.graphics.Typeface.DEFAULT_BOLD
import android.graphics.Typeface.MONOSPACE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.attendance.bk.R
import com.attendance.bk.bean.TextData
import com.blankj.utilcode.util.ColorUtils


/**
 *
 * Created by CXJ
 * Created date 2019/9/29/029
 *
 */
class TextAdapter(val context: Context) : RecyclerView.Adapter<TextAdapter.TextHolder>() {


    private var textType: Int = -1

    private val mData = ArrayList<TextData>()

    fun updateData(dataList: ArrayList<TextData>) {
        mData.clear()
        mData.addAll(dataList)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.view_text_item, parent, false)
        return TextHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: TextHolder, position: Int) {
        holder.text.text = mData[position].textContent
        when (getItemViewType(position)) {
            TYPE_TITLE -> {
                holder.text.typeface = DEFAULT_BOLD
                holder.text.textSize = 26f
                holder.text.setTextColor(ColorUtils.getColor(R.color.text_primary))
            }
            TYPE_CONTENT -> {
                holder.text.typeface = MONOSPACE
                holder.text.textSize = 13f
                holder.text.setTextColor(ColorUtils.getColor(R.color.text_primary))
            }
            TYPE_SUB_TITLE_1 -> {
                holder.text.typeface = MONOSPACE
                holder.text.textSize =18f
                holder.text.setTextColor(ColorUtils.getColor(R.color.text_primary))
            }
            TYPE_SUB_TITLE_2 -> {
                holder.text.typeface = MONOSPACE
                holder.text.textSize = 15f
                holder.text.setTextColor(ColorUtils.getColor(R.color.text_primary))
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return mData[position].textType
    }

    inner class TextHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView.findViewById(R.id.text)
    }

    companion object {
        const val TYPE_TITLE = 0//文章标题
        const val TYPE_CONTENT = 1//内容
        const val TYPE_SUB_TITLE_1 = 2//一级标题
        const val TYPE_SUB_TITLE_2 = 3//二级标题
    }

}