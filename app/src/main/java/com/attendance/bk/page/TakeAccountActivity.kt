package com.attendance.bk.page

import android.content.Intent
import android.os.Bundle
import com.attendance.bk.BkApp
import com.attendance.bk.R
import com.attendance.bk.db.BkDb
import com.attendance.bk.db.table.Trade

/**
 * 记一笔页面
 * Created by Chen xuJie on 2020/4/19.
 */
class TakeAccountActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_account)
        initView()
    }

    private fun handleIntent(intent: Intent) {

    }

    private fun initView() {

    }

    private fun loadBillTypes() {

    }


    companion object {


        const val PARAM_TRADE = "PARAM_TRADE"
        const val PARAM_OP_TYPE = "PARAM_OP_TYPE"
        /**
         * 从账本进来记账
         */
        fun startAddIntent(): Intent {
            return Intent(BkApp.appContext, TakeAccountActivity::class.java).also {
                it.putExtra(PARAM_OP_TYPE, BkDb.TYPE_ADD)
            }
        }

        /**
         * 从账本进来修改
         */
        fun startModifyIntent(trade: Trade): Intent {
            return Intent(BkApp.appContext, TakeAccountActivity::class.java).also {
                it.putExtra(PARAM_TRADE, trade)
                it.putExtra(PARAM_OP_TYPE, BkDb.TYPE_MODIFY)
            }
        }
    }


}