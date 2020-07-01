package com.attendance.bk.adapter

import com.attendance.bk.db.table.Book
import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * Created by Chen xuJie on 2020/4/20.
 */
class BookListAdapter(layoutResId: Int) :
    BaseItemDraggableAdapter<Book, BaseViewHolder>(layoutResId, null) {


    override fun convert(p0: BaseViewHolder, p1: Book?) {

    }
}