package com.attendance.bk.page.billtype

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.attendance.bk.BkApp
import com.attendance.bk.R
import com.attendance.bk.adapter.BillTypeManageAdapter
import com.attendance.bk.bus.BillTypeEvent
import com.attendance.bk.db.BkDb
import com.attendance.bk.page.BaseActivity
import com.attendance.bk.utils.ToolbarUtil
import com.attendance.bk.utils.workerThreadChange
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback
import kotlinx.android.synthetic.main.activity_billtype_manage.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * Created by Chen xuJie on 2019/12/13.
 */
class ManageBillTypeActivity : BaseActivity() {


    private var mBookId: String? = null
    private var mBtType: Int = 0
    private var mBillTypeAdapter: BillTypeManageAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_billtype_manage)
        handleIntent()
        initView()
        loadData()
        addEBus()
    }

    private fun addEBus() {
        BkApp.eventBus.toUIObserver().`as`(bindLifecycle()).subscribe {
            if (it is BillTypeEvent) {
                loadData()
            }
        }
    }


    private fun handleIntent() {
        mBookId = intent.getStringExtra(PARAM_BOOK_ID)
        mBtType = intent.getIntExtra(PARAM_BT_TYPE, 0)
    }

    private fun initView() {
        setSupportToolbar(toolbar)
        ToolbarUtil.setMainTitle("类别管理")
        ToolbarUtil.setSubTitle("添加")
        ToolbarUtil.setOnSubTitleClick(object : ToolbarUtil.OnSubTitleClickListener {
            override fun onSubTitleClick() {
                startActivity(AddBillTypeActivity.addBillType(mBookId!!, mBtType))
            }
        })
        bill_type_list.layoutManager = LinearLayoutManager(this)
        bill_type_list.setHasFixedSize(true)
        mBillTypeAdapter = BillTypeManageAdapter(R.layout.view_bill_type_manage_list_item)
        bill_type_list.adapter = mBillTypeAdapter

        val itemDragAndSwipeCallback = ItemDragAndSwipeCallback(mBillTypeAdapter)
        val itemTouchHelper = ItemTouchHelper(itemDragAndSwipeCallback)
        itemTouchHelper.attachToRecyclerView(bill_type_list)
        // 开启拖拽
        mBillTypeAdapter?.enableDragItem(itemTouchHelper)
        //禁止滑动删除
        mBillTypeAdapter?.disableSwipeItem()

        mBillTypeAdapter?.setOnItemClickListener { adapter, view, position ->
            val billType = mBillTypeAdapter?.getItem(position) ?: return@setOnItemClickListener
            startActivity(AddBillTypeActivity.modifyBillType(billType))
        }
    }

    private fun loadData() {
        BkDb.instance.billTypeDao().getBookBtList(BkApp.currUserId(), mBookId!!, mBtType)
            .workerThreadChange()
            .`as`(bindLifecycle())
            .subscribe({ btList ->
                mBillTypeAdapter?.setNewData(btList)
            }, { e ->
                ToastUtils.showShort("读取失败")
                LogUtils.e("getBookBtList failed->", e)
            })
    }

    override fun onStop() {
        super.onStop()
        mBillTypeAdapter?.closeLayout()
    }

    companion object {

        const val PARAM_BOOK_ID = "PARAM_BOOK_ID"
        const val PARAM_BT_TYPE = "PARAM_BT_TYPE"

        fun startIntent(bookId: String, btType: Int): Intent {
            return Intent(BkApp.appContext, ManageBillTypeActivity::class.java).also {
                it.putExtra(PARAM_BOOK_ID, bookId)
                it.putExtra(PARAM_BT_TYPE, btType)
            }
        }
    }
}