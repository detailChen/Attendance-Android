//package com.boss.bk.page.account
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Pair
//import android.view.View
//import android.widget.EditText
//import android.widget.TextView
//import com.attendance.bk.page.BaseActivity
//import com.attendance.bk.page.account.AccountActivity
//import com.blankj.utilcode.util.KeyboardUtils
//import com.blankj.utilcode.util.LogUtils
//import com.blankj.utilcode.util.NetworkUtils
//import com.boss.bk.BkApp
//import com.boss.bk.R
//import com.boss.bk.adapter.AccountTradeListAdapter
//import com.boss.bk.bean.db.AccountListItemData
//import com.boss.bk.bean.net.TradeData
//import com.boss.bk.bus.AccountEvent
//import com.boss.bk.bus.TradeEvent
//import com.boss.bk.bus.TransferEvent
//import com.boss.bk.db.table.Account
//import com.boss.bk.db.table.Trade
//import com.boss.bk.dialog.ConfirmDeleteDialog
//import com.boss.bk.page.BaseActivity
//import com.boss.bk.utils.*
//import io.reactivex.Single
//import kotlinx.android.synthetic.main.activity_normal_account_detail.*
//import java.util.*
//import kotlin.collections.ArrayList
//import kotlin.math.abs
//
///**
// * 普通账户详情页面
// * Created by CXJ
// * Created date 2019/1/16/016
// */
//class AccountDetailActivity : BaseActivity(), View.OnClickListener {
//
//
//    private var mAccount: Account? = null
//
//    private var mAdapter: AccountTradeListAdapter? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_normal_account_detail)
//        handleIntent(intent)
//        initView()
//        initData()
//        addEBus()
//    }
//
//    private fun addEBus() {
//        BkApp.eventBus.toUIObserver().`as`(bindLifecycle()).subscribe {
//            when (it) {
//                is AccountEvent -> {
//                    queryAccount(it.mAccount.accountId)
//                    initView()
//                    initData()
//                }
//                is TradeEvent -> initData()
//                is TransferEvent -> initData()
//            }
//        }
//    }
//
//    private fun handleIntent(intent: Intent) {
//        val accountId = intent.getStringExtra(PARAM_ACCOUNT_ID)
//        queryAccount(accountId)
//    }
//
//    private fun initView() {
//        setSupportToolbar(toolbar)
//        setToolbarColor(R.color.color_account_bg)
//        ToolbarUtil.setMainTitle(mAccount!!.name)
//        ToolbarUtil.setSubTitle("编辑")
//        ToolbarUtil.setOnSubTitleClick(object : ToolbarUtil.OnSubTitleClickListener {
//            override fun onSubTitleClick() {
//                if (BkApp.currUser.userIsVisitor()) {
//                    BkUtil.showVisitorUserLoginDialog(this@AccountDetailActivity)
//                    return
//                }
//                showEditDeleteDialog()
//            }
//        })
//
//        mAdapter = AccountTradeListAdapter(this)
//        account_trade_list.adapter = mAdapter
//
//        account_money.setOnClickListener(this)
//        account_money_edit.setOnClickListener(this)
//    }
//
//    private fun initData() {
//        loadAccountLeftMoney()
//        loadAccountTradeData()
//    }
//
//    private fun loadAccountLeftMoney() {
//        BkDb.instance.accountDao().getAccountLeftMoney(BkApp.currGroupId(), mAccount!!.accountId)
//                .workerThreadChange().`as`(bindLifecycle()).subscribe({
//                    account_money.text = BkUtil.formatMoney(it)
//                }, {
//                    showToast("读取失败")
//                    LogUtils.e("loadAccountLeftMoney failed->", it)
//                })
//    }
//
//    private fun queryAccount(accountId: String) {
//        mAccount = BkDb.instance.accountDao().getAccountById(BkApp.currGroupId(), accountId)//直接主线程查询
//        if (mAccount == null) {
//            showToast("数据异常")
//            finish()
//        }
//    }
//
//    private fun loadAccountTradeData() {
//        val accountDao = BkDb.instance.accountDao()
//        val groupId = BkApp.currGroupId()
//        val accountId = mAccount!!.accountId
//        val mtdResult = if (mAccount!!.type == 0) {
//            accountDao.getNormalAccountMonthTotalData(groupId, accountId, DateUtil.currDate)
//        } else {
//            val billDay = mAccount!!.billDay
//            accountDao.getCreditAccountMonthTotalData(groupId, accountId,
//                    if (billDay < 10) "0${billDay}" else "$billDay", DateUtil.currDate)
//        }
//        mtdResult.map {
//            val datas = ArrayList<AccountListItemData>(it.size)
//            if (it.isEmpty().not()) {
//                val firstMtd = it[0]
//                for ((index, item) in it.withIndex()) {
//                    datas.add(AccountListItemData.newMtd(item))
//                    if (index == 0) {//默认展开第一个月数据
//                        datas.addAll(getAccountMonthDatas(firstMtd.month).blockingGet())
//                    }
//                }
//            }
//            datas
//        }.workerThreadChange().`as`(bindLifecycle()).subscribe({
//            list_empty.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
//            account_trade_list.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
//            account_trade_list_top.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
//            mAdapter?.clearData()
//            mAdapter?.updateData(it)
//        }, {
//            showToast("读取失败")
//            LogUtils.e("loadAccountTradeData failed->", it)
//        })
//    }
//
//    fun getAccountMonthDatas(month: String): Single<List<AccountListItemData>> {
//        return Single.create {
//            val accountDao = BkDb.instance.accountDao()
//            val groupId = BkApp.currGroupId()
//            val accountId = mAccount!!.accountId
//            val monthStartEnd = getMonthStartEnd(DateUtil.parseDate(month))
//            val monthDayTotalData = accountDao.getAccountDayTotalData(groupId, accountId, monthStartEnd.first, monthStartEnd.second).blockingGet()
//            val monthTradeData = accountDao.getAccountTradeData(groupId, accountId, monthStartEnd.first, monthStartEnd.second).blockingGet()
//            val imageDao = BkDb.instance.imageDao()
//            monthTradeData.forEach { tid ->
//                val images = imageDao.getImageByForeignId(tid.tradeId).blockingGet()
//                tid.imageList = images
//            }
//            val datas = ArrayList<AccountListItemData>()
//            monthDayTotalData.forEach { dtd ->
//                datas.add(AccountListItemData.newDtd(dtd, month))
//                monthTradeData.forEach { tid ->
//                    if (dtd.date == tid.date) {
//                        datas.add(AccountListItemData.newTrade(tid, month))
//                    }
//                }
//            }
//            it.onSuccess(datas)
//        }
//    }
//
//    fun getMonthStartEnd(month: Date): Pair<String, String> {
//        val cal = DateUtil.dayZeroTimeCal
//        cal.time = month
//        if (mAccount!!.type == 0) {
//            val monthStart = cal.getActualMinimum(Calendar.DAY_OF_MONTH)
//            cal[Calendar.DAY_OF_MONTH] = monthStart
//            val sStart = DateUtil.formatDate(cal.time)
//            val monthEnd = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
//            cal[Calendar.DAY_OF_MONTH] = monthEnd
//            val sEnd = DateUtil.formatDate(cal.time)
//            return Pair(sStart, sEnd)
//        } else {
//            cal.add(Calendar.MONTH, -1)
//            cal.set(Calendar.DAY_OF_MONTH, mAccount!!.billDay)
//            val sStart = DateUtil.formatDate(cal.time)
//            cal.add(Calendar.MONTH, 1)
//            cal.add(Calendar.DAY_OF_MONTH, -1)
//            val sEnd = DateUtil.formatDate(cal.time)
//            return Pair(sStart, sEnd)
//        }
//    }
//
//    private fun showEditDeleteDialog() {
//        val dialog = BkUtil.newDialog(this, layoutResId = R.layout.dialog_account_edit)
//        dialog.findViewById<View>(R.id.modify_account).setOnClickListener {
//            startActivity(AccountActivity.startModifyIntent(mAccount!!))
//            dialog.dismiss()
//        }
//        dialog.findViewById<View>(R.id.delete_account).setOnClickListener {
//            showDeleteWarnDialog()
//            dialog.dismiss()
//        }
//        dialog.show()
//    }
//
//
//    private fun showDeleteWarnDialog() {
//        ConfirmDeleteDialog(this)
//                .setConfirmButton(listener = View.OnClickListener { deleteAccount() })
//                .show()
//    }
//
//    private fun deleteAccount() {
//        val accounts = BkDb.instance.accountDao().getAllAccount(BkApp.currGroupId()) ?: return
//        if (accounts.size == 1) {
//            showToast("请至少保留一个账户")
//            return
//        }
//
//        if (!NetworkUtils.isConnected()) {
//            showToast("请检查网络连接")
//            return
//        }
//        BkApp.apiService.deleteAccount(mAccount!!).map<com.boss.bk.utils.Optional<Account>> {
//            if (it.isResultOk()) {
//                val account = it.data
//                if (account != null) {
//                    BkDb.instance.accountDao().update(account)
//                    com.boss.bk.utils.Optional.of(account)
//                } else {
//                    com.boss.bk.utils.Optional.empty()
//                }
//            } else {
//                showToast(it.desc)
//                com.boss.bk.utils.Optional.empty()
//            }
//        }.workerThreadChange().`as`(bindLifecycle()).subscribe({
//            if (it.isPresent) {
//                showToast("删除成功")
//                BkApp.eventBus.post(AccountEvent(it.get(), BkDb.TYPE_DELETE))
//                finish()
//            }
//        }, {
//            showToast("删除失败")
//            LogUtils.e("deleteAccount failed->", it)
//        })
//    }
//
//
//    private fun showEditMoneyDialog() {
//        val dialog = BkUtil.newDialog(this, layoutResId = R.layout.dialog_account_money_edit)
//        val money = dialog.findViewById<EditText>(R.id.money)
//        money.requestFocus()
//        BkUtil.limitEtDigitInput(money)
//        money.setText(account_money.text.toString())
//        money.setSelection(money.length())
//
//        dialog.findViewById<TextView>(R.id.cancel).setOnClickListener {
//            dialog.dismiss()
//        }
//        dialog.findViewById<TextView>(R.id.save).setOnClickListener {
//            val sMoney = money.text.toString().trim()
//            if (sMoney.isEmpty()) {
//                showToast("金额不能为空哦")
//                return@setOnClickListener
//            }
//            val dMoney = sMoney.toDouble()
//            val currMoney = account_money.text.toString().toDouble()
//            val diffMoney = dMoney - currMoney
//            if (diffMoney != 0.0) {
//                changeAccountMoney(diffMoney)
//            }
//            dialog.dismiss()
//        }
//
//        dialog.show()
//        BkApp.mainHandler.postDelayed({
//            KeyboardUtils.showSoftInput(money)
//        }, 200)
//    }
//
//    private fun changeAccountMoney(diffMoney: Double) {
//        val trade = Trade(
//                tradeId = UUIDUtil.uuid(),
//                money = abs(diffMoney),
//                tradeType = if (diffMoney > 0) TRADE_TYPE_IN else TRADE_TYPE_OUT,
//                payTypeId = mAccount!!.accountId,
//                date = DateUtil.currDate,
//                groupId = BkApp.currGroupId(),
//                userId = BkApp.currUserId(),
//                billTypeId = TRADE_ACCOUNT_DIFF_MONEY,
//                state = TRADE_STATE_FINISH
//        )
//        BkApp.apiService.addTrade(TradeData(trade)).map<Boolean> {
//            if (it.isResultOk()) {
//                if (it.data != null) {
//                    BkDb.instance.tradeDao().addModifyTrade(it.data.trade!!)
//                    true
//                } else {
//                    false
//                }
//            } else {
//                showToast(it.desc)
//                false
//            }
//        }.workerThreadChange().`as`(bindLifecycle()).subscribe({
//            if (it) {
//                showToast("修改成功")
//                BkApp.eventBus.post(AccountEvent(mAccount, BkDb.TYPE_MODIFY))
//                initData()
//            }
//        }, {
//            LogUtils.e("changeAccountMoney failed->", it)
//            showToast("修改失败")
//        })
//    }
//
//    companion object {
//
//        const val PARAM_ACCOUNT_ID = "PARAM_ACCOUNT_ID"
//
//        fun startIntent(accountId: String): Intent {
//            val intent = Intent(BkApp.appContext, AccountDetailActivity::class.java)
//            intent.putExtra(PARAM_ACCOUNT_ID, accountId)
//            return intent
//        }
//    }
//
//    override fun onClick(v: View) {
//        when (v.id) {
//            R.id.account_money,
//            R.id.account_money_edit -> {
//                if (BkApp.currUser.userIsVisitor()) {
//                    BkUtil.showVisitorUserLoginDialog(this)
//                    return
//                }
//                showEditMoneyDialog()
//            }
//        }
//    }
//
//    fun isCreditTypeAccount() = mAccount!!.type == 1
//
//}