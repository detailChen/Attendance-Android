package com.attendance.bk.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.attendance.bk.BkApp
import com.attendance.bk.R
import com.attendance.bk.db.BkDb
import com.attendance.bk.db.table.Account
import com.attendance.bk.page.account.AccountActivity
import com.attendance.bk.utils.DrawableUtil
import com.attendance.bk.utils.workerThreadChange
import com.attendance.bk.view.RvDivider
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * Created by Chen xuJie on 2020/2/19.
 */
class AccountSelDialog : DialogFragment() {

    private var mListener: OnAccountSelListener? = null

    private var mAdapter: AccountListAdapter? = null

    private var mSelAccountId: String? = null

    interface OnAccountSelListener {
        fun onAccountSel(account: Account)
    }


    fun setOnAccountSelListener(listener: OnAccountSelListener) {
        this.mListener = listener
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_account_sel, container)

        val accountList = view.findViewById<RecyclerView>(R.id.account_list)
        accountList.setHasFixedSize(true)
        accountList.layoutManager = LinearLayoutManager(context)
        val divider = RvDivider()
        divider.setMargin(ConvertUtils.dp2px(16f), 0, 0, 0)
        accountList.addItemDecoration(divider)
        if (mAdapter == null) {
            mAdapter = AccountListAdapter(R.layout.view_account_sel_list_item)
        }
        accountList.adapter = mAdapter
        mAdapter?.setOnItemClickListener { adapter, v, position ->
            val account = mAdapter?.getItem(position) ?: return@setOnItemClickListener
            if (mListener != null) {
                mListener?.onAccountSel(account)
                dismiss()
            }
        }
        var selAccountId: String? = null
        if (TextUtils.isEmpty(mSelAccountId)) {
            val bundle = arguments
            if (bundle != null) {
                selAccountId = bundle.getString(SEL_ACCOUNT_ID)
            }
        } else {
            selAccountId = mSelAccountId
        }
        loadAccountTypeData(selAccountId)
        view.findViewById<View>(R.id.cancel).setOnClickListener { dismiss() }
        view.findViewById<View>(R.id.add_account).setOnClickListener {
            startActivity(AccountActivity.startAddIntent())
            dismiss()
        }

        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(activity!!, R.style.DatePickerBottomDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // 设置Content前设定

        dialog.setContentView(R.layout.dialog_account_sel)
        dialog.setCanceledOnTouchOutside(true) // 外部点击取消

        val window = dialog.window
        if (window != null) {
            window.attributes.windowAnimations = R.style.DatePickerDialogAnim
            val lp = window.attributes
            lp.gravity = Gravity.BOTTOM // 紧贴底部
            lp.width = WindowManager.LayoutParams.MATCH_PARENT // 宽度持平
            lp.dimAmount = 0.35f
            window.attributes = lp
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }

        return dialog
    }


    fun setSelAccount(selAccountId: String?) {
        mSelAccountId = selAccountId
    }

//    private val accountTypeList = ArrayList<AccountType>()

    @SuppressLint("AutoDispose", "CheckResult")
    fun loadAccountTypeData(selAccountId: String?) {
        BkDb.instance.accountDao().getAllAccount(BkApp.currUserId()).workerThreadChange()
            .subscribe({
                mAdapter?.setNewData(it)
                mAdapter?.setSelPos(selAccountId)
            }, {
                ToastUtils.showShort("读取失败");
                LogUtils.e("getAllAccount failed->", it);
            });
    }


    private class AccountListAdapter(layoutResId: Int) :
        BaseQuickAdapter<Account, BaseViewHolder>(layoutResId) {

        private var mSelPos = -1

        fun setSelPos(accountId: String?) {
            for (i in 0 until data.size) {
                val item = data[i]
                if (TextUtils.equals(item.accountId, accountId)) {
                    mSelPos = i
                    break
                }
            }
            notifyDataSetChanged()
        }


        override fun convert(helper: BaseViewHolder, account: Account) {
            val icon = helper.getView<ImageView>(R.id.account_icon)
            icon.setImageDrawable(DrawableUtil.getDrawableByName(account.accountTypeIcon))
            helper.setText(R.id.account_name, account.name)
            helper.getView<View>(R.id.account_sel).visibility =
                if (mSelPos == helper.adapterPosition) View.VISIBLE else View.GONE
        }
    }

    companion object {

        const val TAG = "AccountSelDialog"
        const val SEL_ACCOUNT_ID = "SEL_ACCOUNT_ID"
    }
}