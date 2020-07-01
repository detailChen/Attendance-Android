package com.attendance.bk.page.billtype

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.attendance.bk.BkApp
import com.attendance.bk.R
import com.attendance.bk.adapter.BillTypeAdapter
import com.attendance.bk.bean.BTItem
import com.attendance.bk.bean.BillTypeData
import com.attendance.bk.bean.Icon
import com.attendance.bk.bus.BillTypeEvent
import com.attendance.bk.db.BILL_TYPE_OUT
import com.attendance.bk.db.BkDb
import com.attendance.bk.db.table.BillType
import com.attendance.bk.page.BaseActivity
import com.attendance.bk.utils.Optional
import com.attendance.bk.utils.ToolbarUtil
import com.attendance.bk.utils.UUIDUtil
import com.attendance.bk.utils.workerThreadChange
import com.attendance.bk.view.GridSpacingItemDecoration
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.LogUtils
import com.fasterxml.jackson.core.type.TypeReference
import io.reactivex.Single
import kotlinx.android.synthetic.main.activity_add_bill_type.*
import kotlinx.android.synthetic.main.toolbar.*
import java.io.InputStream

/**
 * Created by Chen xuJie on 2019/12/13.
 */
class AddBillTypeActivity : BaseActivity() {

    private var isModify = false
    private var mBillType: BillType? = null
    private var mBookId: String? = null
    private var mbtType: Int = BILL_TYPE_OUT
    private val mAdapter: BillTypeAdapter by lazy { BillTypeAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_bill_type)
        handleIntent()
        initView()
        initData()
    }


    private fun handleIntent() {
        isModify = intent.getBooleanExtra(PARAM_IS_MODIFY, false)
        if (isModify) {
            mBillType = intent.getParcelableExtra(PARAM_BILL_TYPE)
        } else {
            mBookId = intent.getStringExtra(PARAM_BOOK_ID)
            mbtType = intent.getIntExtra(PARAM_BT_TYPE, BILL_TYPE_OUT)
        }
    }


    private fun initView() {
        setSupportToolbar(toolbar)
        ToolbarUtil.setMainTitle(if (isModify) "修改类别" else "添加类别")
        ToolbarUtil.setSubTitle("保存")
        ToolbarUtil.setOnSubTitleClick(object : ToolbarUtil.OnSubTitleClickListener {
            override fun onSubTitleClick() {
                checkAndSave()
            }
        })

        bt_icon_list.layoutManager = GridLayoutManager(this, 5)
        bt_icon_list.setHasFixedSize(true)
        bt_icon_list.adapter = mAdapter
        bt_icon_list.addItemDecoration(
            GridSpacingItemDecoration(
                5,
                ConvertUtils.dp2px(16.0f),
                true
            )
        )
        mAdapter.setOnItemClickListener { adapter, view, position ->
            val btIcon = mAdapter.getItem(position) ?: return@setOnItemClickListener
            mAdapter.setSelIcon(btIcon.groupIcon!!)
            mBillType?.clickIcon = btIcon.groupIcon.clickIcon
            mBillType?.normalIcon = btIcon.groupIcon.normalIcon
            bt_icon.setImageName(mBillType!!.clickIcon)
        }
    }

    private fun initData() {
        if (isModify) {
            initModify()
        } else {
            initDefData()
        }
        loadBtIcon()
    }

    private fun initModify() {
        bt_icon.setImageName(mBillType!!.clickIcon)
        bt_name.setText(mBillType?.name)
        bt_name.setSelection(bt_name.length())
        bt_name.requestFocus()
    }

    private fun initDefData() {
        mBillType = BillType(
            billId = UUIDUtil.uuid(),
            bookId = mBookId!!,
            type = mbtType,
            userId = BkApp.currUserId()
        )
    }

    private fun loadBtIcon() {
        Single.create<List<BTItem>> {
            var btIs: InputStream? = null
            try {
                btIs = BkApp.appContext.resources.openRawResource(R.raw.bt_icon_all)
                val btIconList = BkApp.bkJackson.readValue<List<BillTypeData>>(
                    btIs,
                    object : TypeReference<List<BillTypeData>>() {
                    })
                val btItemList = ArrayList<BTItem>()
                btIconList.forEach { btd ->
                    val groupName = btd.name
                    val icons = btd.icons
                    btItemList.add(BTItem(groupName = groupName, type = BTItem.TYPE_GROUP_NAME))
                    icons.forEach { icon ->
                        btItemList.add(BTItem(groupIcon = icon, type = BTItem.TYPE_GROUP_ICON))
                    }
                }
                it.onSuccess(btItemList)
            } finally {
                btIs?.close()
            }
        }.workerThreadChange().`as`(bindLifecycle()).subscribe({
            mAdapter.setNewData(it)
            if (isModify) {
                val icon = Icon(mBillType!!.clickIcon, mBillType!!.normalIcon)
                mAdapter.setSelIcon(icon)
            } else {
                var firstGroupIcon: Icon? = null
                for (item in it) {
                    if (item.type == BTItem.TYPE_GROUP_ICON) {
                        firstGroupIcon = item.groupIcon
                        break
                    }
                }
                mAdapter.setSelIcon(firstGroupIcon!!)
                mBillType?.clickIcon = firstGroupIcon.clickIcon
                mBillType?.normalIcon = firstGroupIcon.normalIcon
                bt_icon.setImageName(firstGroupIcon.clickIcon)

            }
        }, {
            LogUtils.e("loadBtIcon failed->", it)
        })
    }


    private fun checkAndSave() {
        val btName = bt_name.text.toString().trim()
        if (btName.isEmpty()) {
            showToast("请输入名称")
            return
        }
        mBillType?.name = btName


        BkDb.instance.billTypeDao().checkBtDuplicateName(mBillType!!)
            .map<Optional<BillType>> { hasSameBtName ->
                if (hasSameBtName) {
                    showToast("已存在同名的类别")
                    Optional.empty<BillType>()
                } else {
                    val singleResult = if (isModify) {
                        BkApp.apiService.modifyBillType(mBillType!!)
                    } else {
                        BkApp.apiService.addBillType(mBillType!!)
                    }
                    val result = singleResult.blockingGet()
                    if (result.isResultOk()) {
                        if (result.data == null) {
                            Optional.empty<BillType>()
                        } else {
                            BkDb.instance.billTypeDao().addModifyBillType(result.data, isModify)
                            Optional.of(result.data)
                        }
                    } else {
                        showToast(result.desc)
                        Optional.empty<BillType>()
                    }
                }
            }.workerThreadChange().`as`(bindLifecycle()).subscribe({
                if (it.isPresent) {
                    BkApp.eventBus.post(
                        BillTypeEvent(
                            it.get(),
                            if (isModify) BkDb.TYPE_MODIFY else BkDb.TYPE_ADD
                        )
                    )
                    showToast(if (isModify) "修改成功" else "添加成功")
                    finish()
                }
            }, {
                LogUtils.e("addModifyBillType failed->", it)
            })
    }


    companion object {

        const val PARAM_BILL_TYPE = "PARAM_BILL_TYPE"
        const val PARAM_IS_MODIFY = "PARAM_IS_MODIFY"
        const val PARAM_BOOK_ID = "PARAM_BOOK_ID"
        const val PARAM_BT_TYPE = "PARAM_BT_TYPE"


        fun addBillType(bookId: String, btType: Int): Intent {
            return Intent(BkApp.appContext, AddBillTypeActivity::class.java).also {
                it.putExtra(PARAM_BOOK_ID, bookId)
                it.putExtra(PARAM_IS_MODIFY, false)
                it.putExtra(PARAM_BT_TYPE, btType)
            }
        }

        fun modifyBillType(billType: BillType): Intent {
            return Intent(BkApp.appContext, AddBillTypeActivity::class.java).also {
                it.putExtra(PARAM_BILL_TYPE, billType)
                it.putExtra(PARAM_IS_MODIFY, true)
            }
        }
    }
}