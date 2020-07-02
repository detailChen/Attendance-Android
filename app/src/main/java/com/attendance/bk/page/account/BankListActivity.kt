package com.attendance.bk.page.account

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.attendance.bk.BkApp
import com.attendance.bk.R
import com.attendance.bk.adapter.BankListAdapter
import com.attendance.bk.bean.AccountType
import com.attendance.bk.bean.BankInfo
import com.attendance.bk.page.BaseActivity
import com.attendance.bk.utils.ToolbarUtil.setMainTitle
import com.attendance.bk.utils.workerThreadChange
import com.attendance.bk.view.RvDivider
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.fasterxml.jackson.core.type.TypeReference
import io.reactivex.Single
import io.reactivex.SingleEmitter
import kotlinx.android.synthetic.main.toolbar.*

/**
 * 选择发卡页面
 *
 * @author Ren ZeQiang
 * @since 2018/9/17.
 */
class BankListActivity : BaseActivity() {

    private var mAccountType: AccountType? = null
    private var mAdapter: BankListAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_list)
        handleIntent(intent)
        initView()
        initData()
        addEBusListener()
    }

    private fun handleIntent(intent: Intent) {
        mAccountType = intent.getParcelableExtra(PARAM_ACCOUNT_TYPE)
    }

    private fun initView() {
        setSupportToolbar(toolbar)
        setMainTitle("选择发卡行")
        val mBankRv = findViewById<RecyclerView>(R.id.recycler_view)
        layoutManager = LinearLayoutManager(this)
        mBankRv.layoutManager = layoutManager
        val divider = RvDivider()
        divider.skipLastDivider()
        divider.skipFirstDivider()
        mBankRv.addItemDecoration(divider)
        mAdapter = BankListAdapter()
        mBankRv.adapter = mAdapter
        mAdapter!!.setOnItemClickListener { _, _, position: Int ->
            val bankInfo = mAdapter!!.getItem(position) ?: return@setOnItemClickListener
            if (bankInfo.type == BankInfo.TYPE_BANK) {
                mAccountType!!.accountTypeIcon = bankInfo.icon!!
                mAccountType!!.accountTypeName = bankInfo.name!!
                Intent().let {
                    it.putExtra(PARAM_ACCOUNT_TYPE, mAccountType)
                    setResult(RESULT_OK, it)
                    finish()
                }
            }
        }
    }


    private fun initData() {
        Single.create { emitter: SingleEmitter<List<BankInfo?>?> ->
            val `is` = resources.openRawResource(R.raw.bank)
            val bankInfoList: List<BankInfo?> = BkApp.bkJackson.readValue(`is`, object : TypeReference<List<BankInfo>>() {})
            emitter.onSuccess(bankInfoList)
        }.workerThreadChange().`as`(bindLifecycle())
                .subscribe({
                    mAdapter!!.handleBank(it)
                }, {
                    ToastUtils.showShort("读取失败")
                    LogUtils.e("read bank data failed->", it)
                })
    }

    private fun addEBusListener() {}

    companion object {

        const val PARAM_ACCOUNT_TYPE = "PARAM_ACCOUNT_TYPE"

        fun startIntent(accountType: AccountType): Intent {
            val intent = Intent(BkApp.appContext, BankListActivity::class.java)
            intent.putExtra(PARAM_ACCOUNT_TYPE, accountType)
            return intent
        }
    }
}