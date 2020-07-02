package com.attendance.bk.page.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.attendance.bk.BkApp
import com.attendance.bk.R
import com.attendance.bk.adapter.BookListAdapter
import com.attendance.bk.adapter.MainPagerAdapter
import com.attendance.bk.db.BkDb
import com.attendance.bk.listener.SimplePageChangeListener
import com.attendance.bk.page.BaseActivity
import com.attendance.bk.page.LazyPagerFragment
import com.attendance.bk.utils.BkUtil
import com.attendance.bk.utils.workerThreadChange
import com.attendance.bk.view.GridSpacingItemDecoration
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SizeUtils
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.activity_main_content.*
import kotlinx.android.synthetic.main.activity_take_account.*

class MainActivity : BaseActivity(), View.OnClickListener {


    private var mBookAdapter: BookListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()

    }

    private fun initView() {
        val adapter = MainPagerAdapter(supportFragmentManager)
        main_pager.adapter = adapter
        main_pager.addOnPageChangeListener(object : SimplePageChangeListener() {
            override fun onPageSelected(position: Int) {
                onPageSel(position)
                if (position == 0) {
                    setTranslucentForImageView(this@MainActivity)
                    drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNDEFINED)
                } else {
                    if (position == 1) {
                        setToolbarColor(R.color.text_third)
                    } else {
                        setToolbarColor()
                    }
                    drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
                val currFragment = adapter.getItem(position) as LazyPagerFragment
                currFragment.userVisibleHint = true
            }
        })
        main_pager.offscreenPageLimit = 3
        onPageSel(0)
        val bookFragment = adapter.getItem(0) as LazyPagerFragment
        bookFragment.userVisibleHint = true

        drawer_layout.addDrawerListener(object : DrawerLayout.DrawerListener {

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

            }

            override fun onDrawerOpened(drawerView: View) {

            }

            override fun onDrawerClosed(drawerView: View) {

            }

            override fun onDrawerStateChanged(newState: Int) {

            }
        })

        toolbar.setPadding(0, BarUtils.getStatusBarHeight(), 0, 0)


        tab_attendance_layout.setOnClickListener(this)
        tab_bill_layout.setOnClickListener(this)
        tab_form_layout.setOnClickListener(this)
        tab_mine_layout.setOnClickListener(this)
//        book_layout.setOnClickListener(this)
        add_book.setOnClickListener(this)
    }

    private fun initBookListView() {
        val bookList = findViewById<RecyclerView>(R.id.book_list)
        val spanCount = 3
        bookList.layoutManager = GridLayoutManager(this, spanCount)
        val space = SizeUtils.dp2px(16f)
        bookList.addItemDecoration(GridSpacingItemDecoration(spanCount, space, false))
        mBookAdapter = BookListAdapter(R.layout.view_book_list_item)
        bookList.adapter = mBookAdapter

        val itemDragAndSwipeCallback = ItemDragAndSwipeCallback(mBookAdapter)
        val itemTouchHelper = ItemTouchHelper(itemDragAndSwipeCallback)
        itemTouchHelper.attachToRecyclerView(bookList)

        // 开启拖拽
        mBookAdapter?.enableDragItem(itemTouchHelper)
        //禁止滑动删除
        mBookAdapter?.disableSwipeItem()
        mBookAdapter?.setOnItemClickListener { _, _, position ->
            val book = mBookAdapter?.getItem(position) ?: return@setOnItemClickListener
            //如果没有切换账本，则不用刷新数据
            val userExtra = BkApp.currUser.getUserExtra()
            val currSelBookId = userExtra.currBookId
//            if (userExtra.currType == UserExtra.CURR_TYPE_BOOK && TextUtils.equals(currSelBookId, book.bookId)) {
//                drawer_layout.closeDrawers()
//                return@setOnItemClickListener
//            } else {
//                updateCurrSelBook(book)
//            }
        }

        mBookAdapter?.setOnItemChildClickListener { _, _, position ->
            if (BkApp.currUser.userIsVisitor()) {
                BkUtil.showVisitorUserLoginDialog(this)
                return@setOnItemChildClickListener
            }
            val book = mBookAdapter?.getItem(position) ?: return@setOnItemChildClickListener
//            showBookEditDialog(book)
        }
    }




    private fun onPageSel(pos: Int) {
        val tabIconAttendance = findViewById<ImageView>(R.id.tab_icon_attendance)
        val tabIconBill = findViewById<ImageView>(R.id.tab_icon_bill)
        val tabIconForm = findViewById<ImageView>(R.id.tab_icon_form)
        val tabIconMine = findViewById<ImageView>(R.id.tab_icon_mine)
        val tabTextAttendance = findViewById<TextView>(R.id.tab_text_attendance)
        val tabTextBill = findViewById<TextView>(R.id.tab_text_bill)
        val tabTextForm = findViewById<TextView>(R.id.tab_text_form)
        val tabTextMine = findViewById<TextView>(R.id.tab_text_mine)

        tabIconAttendance.setImageResource(if (pos == 0) R.drawable.main_tab_attendance_sel else R.drawable.main_tab_attendance_nor)
        tabIconBill.setImageResource(if (pos == 1) R.drawable.main_tab_analysis_sel else R.drawable.main_tab_analysis_sel)
        tabIconForm.setImageResource(if (pos == 2) R.drawable.main_tab_analysis_sel else R.drawable.main_tab_analysis_nor)
        tabIconMine.setImageResource(if (pos == 3) R.drawable.main_tab_mine_sel else R.drawable.main_tab_mine_nor)

        val ctx = this
        tabTextAttendance.setTextColor(
            ContextCompat.getColor(
                ctx,
                if (pos == 0) R.color.color_home_tab_active else R.color.color_home_tab_normal
            )
        )
        tabTextBill.setTextColor(
            ContextCompat.getColor(
                ctx,
                if (pos == 1) R.color.color_home_tab_active else R.color.color_home_tab_normal
            )
        )
        tabTextForm.setTextColor(
            ContextCompat.getColor(
                ctx,
                if (pos == 2) R.color.color_home_tab_active else R.color.color_home_tab_normal
            )
        )
        tabTextMine.setTextColor(
            ContextCompat.getColor(
                ctx,
                if (pos == 3) R.color.color_home_tab_active else R.color.color_home_tab_normal
            )
        )

    }


    @SuppressLint("AutoDispose", "CheckResult")
    private fun loadBookData() {
//        val bookSetId = BkApp.currUser.getUserExtra().currBookId
        BkDb.instance.bookDao().queryAllBook(BkApp.currUserId())
            .workerThreadChange().subscribe({ bookDataList ->
//                mBookAdapter.updateData(bookDataList, false)
            }, { throwable ->
                showToast("读取账本失败")
                LogUtils.e("loadBookData failed->", throwable)
            })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tab_attendance_layout -> main_pager.setCurrentItem(0, false)
            R.id.tab_bill_layout -> main_pager.setCurrentItem(1, false)
            R.id.tab_form_layout -> main_pager.setCurrentItem(2, false)
            R.id.tab_mine_layout -> main_pager.setCurrentItem(3, false)
            R.id.add_book -> checkAndAddBook()
        }
    }

    private fun checkAndAddBook() {

    }
}
