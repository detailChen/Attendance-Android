package com.attendance.bk.page.account

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.attendance.bk.BkApp
import com.attendance.bk.R
import com.attendance.bk.bean.AccountType
import com.attendance.bk.bus.AccountEvent
import com.attendance.bk.db.BkDb
import com.attendance.bk.db.table.Account
import com.attendance.bk.dialog.AccountTypeSelDialog
import com.attendance.bk.dialog.DayPickerDialog
import com.attendance.bk.page.BaseActivity
import com.attendance.bk.utils.BkUtil.limitEtTextInput
import com.attendance.bk.utils.Optional
import com.attendance.bk.utils.ToolbarUtil
import com.attendance.bk.utils.ToolbarUtil.setMainTitle
import com.attendance.bk.utils.ToolbarUtil.setOnSubTitleClick
import com.attendance.bk.utils.ToolbarUtil.setSubTitle
import com.attendance.bk.utils.UUIDUtil
import com.attendance.bk.utils.workerThreadChange
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.NetworkUtils
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * 普通类型账户
 * Created by CXJ
 * Created date 2019/1/10/010
 */
class AccountActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mAccount: Account
    private var isModify = false

    private var mAccountTypeSelDialog: AccountTypeSelDialog? = null
    private var mDayPickerDialog: DayPickerDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        handleIntent(intent)
        initView()
        if (isModify) {
            initModify()
        } else {
            initData()
        }
    }

    private fun handleIntent(intent: Intent) {
        isModify = intent.getBooleanExtra(PARAM_IS_MODIFY, false)
        if (isModify) {
            mAccount = intent.getParcelableExtra(PARAM_ACCOUNT)
        }
    }

    private fun initView() {
        setSupportToolbar(toolbar)
        setMainTitle(if (!isModify) "添加账户" else "编辑账户")
        setSubTitle("保存")
        setOnSubTitleClick(object : ToolbarUtil.OnSubTitleClickListener {
            override fun onSubTitleClick() {
                checkAndSave()
            }
        })
        limitEtTextInput(name, 7)
        limitEtTextInput(memo, 15)
        findViewById<View>(R.id.account_type_layout).setOnClickListener(this)
        findViewById<View>(R.id.bill_day_layout).setOnClickListener(this)

    }

    @SuppressLint("SetTextI18n")
    private fun initData() {
        initAccount()
        val billDay = mAccount.billDay
        bill_day.text = "每月${billDay}日"
    }

    private fun initAccount() {
        mAccount = Account(
            accountId = UUIDUtil.uuid(),
            userId = BkApp.currUserId(),
            billDay = 1
        )
    }


    private fun showAccountTypeDialog() {
        if (mAccountTypeSelDialog == null) {
            mAccountTypeSelDialog = AccountTypeSelDialog()
            val bundle = Bundle()
            bundle.putString(AccountTypeSelDialog.SEL_ACCOUNT_TYPE_ID, mAccount.accountTypeId)
            mAccountTypeSelDialog?.arguments = bundle
            mAccountTypeSelDialog?.setOnAccountTypeSelListener(object :
                AccountTypeSelDialog.OnAccountTypeSelListener {
                override fun onAccountTypeSel(accountType: AccountType) {
                    val accountTypeId = accountType.accountTypeId
                    if (accountTypeId == "2" || accountTypeId == "5") {//储蓄卡
                        startActivityForResult(
                            BankListActivity.startIntent(accountType),
                            REQ_ACCOUNT_TYPE
                        )
                    } else {
                        setAccountTypeData(accountType)
                    }
                }
            })
        } else {
            mAccountTypeSelDialog?.setSelAccountType(mAccount.accountTypeId)
        }
        mAccountTypeSelDialog?.show(supportFragmentManager, AccountTypeSelDialog.TAG)
    }


    @SuppressLint("SetTextI18n")
    private fun setAccountTypeData(accountType: AccountType) {
        mAccount.accountTypeId = accountType.accountTypeId
        mAccount.accountTypeName = accountType.accountTypeName
        mAccount.accountTypeIcon = accountType.accountTypeIcon
        mAccount.type = accountType.type
        account_type_name.text = when (accountType.accountTypeId) {
            "2" -> "${accountType.accountTypeName}-储蓄卡"
            "5" -> "${accountType.accountTypeName}-信用卡"
            else -> accountType.accountTypeName
        }
        bill_day_layout.visibility = if (mAccount.type == 0) View.GONE else View.VISIBLE

//        account_type_icon.setImageName(accountType.accountTypeName)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQ_ACCOUNT_TYPE && resultCode == Activity.RESULT_OK) {
            val accountType =
                data?.getParcelableExtra<AccountType>(BankListActivity.PARAM_ACCOUNT_TYPE)
            accountType?.run {
                setAccountTypeData(this)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    @SuppressLint("SetTextI18n")
    private fun initModify() {
        account_type_name.text = when (mAccount.accountTypeId) {
            "2" -> "${mAccount.accountTypeName}-储蓄卡"
            "5" -> "${mAccount.accountTypeName}-信用卡"
            else -> mAccount.accountTypeName
        }//        account_type_icon.setImageName(mAccount.accountTypeName)
        name.setText(mAccount.name)
        name.requestFocus()
        name.setSelection(name.length())
        memo.setText(mAccount.memo)
        bill_day_layout.visibility = if (mAccount.type == 0) View.GONE else View.VISIBLE
        bill_day.text = "每月${mAccount.billDay}日"
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.account_type_layout -> showAccountTypeDialog()
            R.id.bill_day_layout -> showDaySelectDialog()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showDaySelectDialog() {
        if (mDayPickerDialog == null) {
            mDayPickerDialog = DayPickerDialog()
            mDayPickerDialog?.setTitle("选择账单日")
            mDayPickerDialog?.setSelectedDay(mAccount.billDay)
            mDayPickerDialog?.setOnDaySelectedListener(object :
                DayPickerDialog.OnDaySelectedListener {
                override fun onDaySelect(day: Int) {
                    mAccount.billDay = day
                    bill_day.text = "每月${day}日"
                }
            })
        } else {
            mDayPickerDialog?.setSelectedDay(mAccount.billDay)
        }

        mDayPickerDialog?.show(supportFragmentManager, DayPickerDialog.TAG)
    }


    private fun checkAndSave() {
        if (mAccount.accountTypeId.isEmpty()) {
            showToast("请选择账户类型")
            return
        }
        val sName = name.text.toString().trim()
        if (sName.isEmpty()) {
            showToast("请输入账户名称")
            return
        }

        if (mAccount.type == 0) {//如果是资产账户，账单日设为每个月1号
            mAccount.billDay = 1
        }

        mAccount.name = sName
        mAccount.memo = memo.text.toString().trim()
        if (BkApp.currUser.userIsVisitor()) {
            addVisitorUserAccount()
        } else {
            addModifyAccount()
        }
    }

    private fun addVisitorUserAccount() {
        BkDb.instance.accountDao().addVisitorUserAccount(mAccount).workerThreadChange()
            .`as`(bindLifecycle())
            .subscribe({
                if (it is Account) {
                    showToast("添加成功")
                    BkApp.eventBus.post(AccountEvent(it, BkDb.TYPE_ADD))
                    finish()
                } else if (it is Int && it == -1) {
                    showToast("已存在同名账户")
                }
            }, {
                showToast("添加失败")
                LogUtils.e("addVisitorUserAccount failed->", it)
            })
    }

    @SuppressLint("CheckResult")
    private fun addModifyAccount() {
        if (!NetworkUtils.isConnected()) {
            showToast("请检查网络连接")
            return
        }
        val sameNameAccountId =
            BkDb.instance.accountDao().querySameNameAccountId(mAccount.userId, mAccount.name)
        if (TextUtils.isEmpty(sameNameAccountId).not()) {
            showToast("已存在同名账户")
            return
        }
        val singleSource = if (isModify) {
            BkApp.apiService.modifyAccount(mAccount)
        } else {
            BkApp.apiService.addAccount(mAccount)
        }
        singleSource.map<Optional<Account>> {
            if (it.isResultOk()) {
                if (it.data != null) {
                    BkDb.instance.accountDao().addModifyAccount(it.data, isModify)
                    Optional.of(it.data)
                } else {
                    Optional.empty<Account>()
                }
            } else {
                showToast(it.desc)
                Optional.empty()
            }
        }.workerThreadChange().`as`(bindLifecycle()).subscribe({
            if (it.isPresent) {
                showToast(if (isModify) "修改成功" else "添加成功")
                BkApp.eventBus.post(
                    AccountEvent(
                        it.get(),
                        if (isModify) BkDb.TYPE_MODIFY else BkDb.TYPE_ADD
                    )
                )
                finish()
            }
//            dismissDialog()
        }, {
            showToast(if (isModify) "修改失败" else "添加失败")
            LogUtils.e("addModifyAccount failed->", it)
//            dismissDialog()
        })
    }

    companion object {

        const val PARAM_ACCOUNT = "PARAM_ACCOUNT"
        const val PARAM_IS_MODIFY = "PARAM_IS_MODIFY"

        const val REQ_ACCOUNT_TYPE = 0


        @JvmStatic
        fun startAddIntent(): Intent {
            val intent = Intent(BkApp.appContext, AccountActivity::class.java)
            intent.putExtra(PARAM_IS_MODIFY, false)
            return intent
        }

        @JvmStatic
        fun startModifyIntent(account: Account): Intent {
            val intent = Intent(BkApp.appContext, AccountActivity::class.java)
            intent.putExtra(PARAM_ACCOUNT, account)
            intent.putExtra(PARAM_IS_MODIFY, true)
            return intent
        }
    }
}