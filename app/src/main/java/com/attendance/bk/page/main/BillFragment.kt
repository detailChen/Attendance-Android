package com.attendance.bk.page.main

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.attendance.bk.BkApp
import com.attendance.bk.R
import com.attendance.bk.adapter.TradeListAdapter
import com.attendance.bk.bean.TradeListData
import com.attendance.bk.db.BkDb
import com.attendance.bk.db.table.Book
import com.attendance.bk.dialog.MonthPickerDialog
import com.attendance.bk.page.LazyPagerFragment
import com.attendance.bk.page.TakeAccountActivity
import com.attendance.bk.showToast
import com.attendance.bk.utils.DateUtil
import com.attendance.bk.utils.workerThreadChange
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import kotlinx.android.synthetic.main.fragment_bill.view.*
import java.util.*

/**
 * Created by Chen xuJie on 2020/4/3.
 */
class BillFragment : LazyPagerFragment(), View.OnClickListener {

    private var mBook: Book? = null
    private var mCurrMonth: Date? = null//当前选中的月份,日期为1,如果是某一年全部的则日期为yyyy-12-31
    private var mAdapter: TradeListAdapter? = null

    private var mMonthPickerDialog: MonthPickerDialog? = null
    private var mDateItemDivider: RecyclerView.ItemDecoration? = null


    private var mMonth: TextView? = null
    private var mBookName: TextView? = null
    private var mMonthOutMoney: TextView? = null
    private var mMonthInMoney: TextView? = null
    private lateinit var mTradeList: RecyclerView


    override fun lazyData() {
        loadCurrBook()
        loadMonthMoney()
        loadTradeData()
    }


    @SuppressLint("AutoDispose", "CheckResult")
    private fun loadCurrBook() {
        BkDb.instance.bookDao().queryForBookId(BkApp.currUser.getUserExtra().currBookId).subscribe({
            mBook = it
            mBookName?.text = it.name
        }, {
            showToast("读取失败")
            LogUtils.e("queryForBookId failed->", it)
        })
    }

    private fun loadMonthMoney() {

    }


    override fun getLayoutRes() = R.layout.fragment_bill


    override fun initView(rootView: View) {
        val toolbar = rootView.findViewById<View>(R.id.toolbar)
        toolbar.setPadding(0, BarUtils.getStatusBarHeight(), 0, 0)

        mBookName = rootView.book_name
        mMonthOutMoney = rootView.month_out_money
        mMonthInMoney = rootView.month_in_money
        mMonth = rootView.month

        mTradeList = rootView.trade_data_list
        mTradeList.layoutManager = LinearLayoutManager(activity)
        mAdapter = TradeListAdapter()
        mTradeList.adapter = mAdapter
        mAdapter?.setEmptyView(R.layout.view_list_empty, mTradeList)
        mAdapter?.setOnItemClickListener { _, _, position ->
            val item = mAdapter?.getItem(position) ?: return@setOnItemClickListener
            val data = item.data ?: return@setOnItemClickListener

        }

        rootView.month_layout.setOnClickListener(this)
        rootView.take_account.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.month_layout -> showMonthSelDialog()
            R.id.take_account -> startActivity(TakeAccountActivity.startAddIntent())
        }
    }

    private fun showMonthSelDialog() {
        if (mMonthPickerDialog == null) {
            mMonthPickerDialog = MonthPickerDialog()
            mMonthPickerDialog?.showAnimation(true)
            mMonthPickerDialog?.setOnMonthSelectedListener(object :
                MonthPickerDialog.OnMonthSelectedListener {
                override fun onMonthSelect(year: Int, month: Int) {
                    val cal = DateUtil.dayZeroTimeCal
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, month)
                    val c = DateUtil.dayZeroTimeCal
                    c.time = mCurrMonth
                    if (cal.time == c.time) {//选择的是同一个月份，不用重新加载
                        return
                    }
                    cal.set(Calendar.MONTH, month)
                    cal.set(Calendar.DAY_OF_MONTH, 1)
                    mCurrMonth = cal.time
                    mMonth?.text = DateUtil.formatDate(mCurrMonth!!, "yyyy.MM")//该年的某个月
                    mAdapter?.clearData()
                    loadMonthMoney()
                    loadTradeData()
                }

            })
        } else {
            val cal = DateUtil.dayZeroTimeCal
            cal.time = mCurrMonth
            mMonthPickerDialog!!.setSelectedMonth(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))
        }
        val fm = activity?.supportFragmentManager ?: return
        mMonthPickerDialog?.show(fm, MonthPickerDialog.TAG)
    }

    /**
     * 加载交易流水数据
     */
    @SuppressLint("AutoDispose", "CheckResult")
    private fun loadTradeData() {
        //每次重新加载数据之前，由于数据源发生了变化，所以都要重新设置，不然加载更多会失效
        mAdapter?.setOnLoadMoreListener({ this.loadTradeData() }, mTradeList)

        val data = mAdapter!!.data
        var lastTradeDate: String? = null
        if (data.isNotEmpty()) {
            val tld = data[data.size - 1]
            lastTradeDate = tld.data?.date
        }
        if (lastTradeDate == null) {
            var cal = DateUtil.dayZeroTimeCal
            cal.time = mCurrMonth
            val now = DateUtil.dayZeroTimeCal
            if (cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) && cal.get(Calendar.MONTH) == now.get(
                    Calendar.MONTH
                )
            ) {//加载是当前的月份,截止时间为今天
                cal = DateUtil.dayZeroTimeCal
            } else {
                cal.add(Calendar.MONTH, 1)
                cal.add(Calendar.DAY_OF_MONTH, -1)
            }
            cal.add(Calendar.DAY_OF_MONTH, 1)
            lastTradeDate = DateUtil.formatDate(cal.time)
        }

        BkDb.instance.tradeDao()
            .getYearMonthTradeList(
                BkApp.currUserId(),
                mBook!!.bookId,
                mCurrMonth!!,
                QUERY_DAY_COUNT,
                lastTradeDate
            )
            .workerThreadChange()
            .subscribe({ tidList ->
                mAdapter?.updateData(tidList)
                addRecyclerDivider()
                if (tidList.isEmpty()) {
                    mAdapter?.loadMoreEnd(true)
                } else {
                    mAdapter?.loadMoreComplete()
                }
            }, { throwable ->
                ToastUtils.showShort("读取失败")
                LogUtils.e("loadTradeData failed->", throwable)
            })
    }

    private fun addRecyclerDivider() {
        if (mDateItemDivider != null) {
            mTradeList.removeItemDecoration(mDateItemDivider!!)
        }
        mDateItemDivider = object : RecyclerView.ItemDecoration() {
            var mDivider = ColorDrawable(Color.TRANSPARENT)
            var height = ConvertUtils.dp2px(9f)
            var dataList = mAdapter!!.data

            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                super.getItemOffsets(outRect, view, parent, state)
                val position = (view.layoutParams as RecyclerView.LayoutParams).viewAdapterPosition

                if (position > -1) {
                    if (position == 0) {
                        outRect.set(0, 0, 0, 0)
                    } else {
                        val data = dataList[position - 1]
                        if (data.itemType == TradeListData.TYPE_BOTTOM) {
                            outRect.set(0, height, 0, 0)
                        } else {
                            outRect.set(0, 0, 0, 0)
                        }
                    }
                }
            }

            override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                val left = parent.paddingLeft
                val right = parent.width - parent.paddingRight
                for (i in dataList.indices) {
                    val data = dataList[i]
                    if (data.itemType == TradeListData.TYPE_BOTTOM) {
                        val child = parent.getChildAt(i) ?: break
                        val params = child.layoutParams as RecyclerView.LayoutParams
                        val top = child.bottom + params.bottomMargin
                        val bottom = top + height
                        mDivider.setBounds(left, top, right, bottom)
                        mDivider.draw(c)
                    }
                }
            }
        }
        mTradeList.addItemDecoration(mDateItemDivider!!)
    }





    companion object {
        const val QUERY_DAY_COUNT = 7
    }
}