package com.attendance.bk.dialog

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
import com.attendance.bk.bean.AccountType
import com.attendance.bk.utils.DrawableUtil
import com.attendance.bk.view.RvDivider
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.fasterxml.jackson.core.type.TypeReference
import java.io.IOException

/**
 * Created by Chen xuJie on 2019/4/19.
 */
class AccountTypeSelDialog : DialogFragment() {


    private var mListener: OnAccountTypeSelListener? = null

    private var mAdapter: AccountTypeListAdapter? = null

    private var mSelAccountTypeId: String? = null

    interface OnAccountTypeSelListener {
        fun onAccountTypeSel(accountType: AccountType)
    }


    fun setOnAccountTypeSelListener(listener: OnAccountTypeSelListener) {
        this.mListener = listener
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_accounttype_sel, container)

        val accountTypeList = view.findViewById<RecyclerView>(R.id.account_type_list)
        accountTypeList.setHasFixedSize(true)
        accountTypeList.layoutManager = LinearLayoutManager(context)
        val divider = RvDivider()
        divider.setMargin(ConvertUtils.dp2px(16f), 0, 0, 0)
        accountTypeList.addItemDecoration(divider)
        if (mAdapter == null) {
            mAdapter = AccountTypeListAdapter(R.layout.view_accounttype_sel_list_item)
        }
        accountTypeList.adapter = mAdapter
        mAdapter?.setOnItemClickListener { adapter, v, position ->
            val accountType = mAdapter?.getItem(position) ?: return@setOnItemClickListener
            if (mListener != null) {
                mListener?.onAccountTypeSel(accountType)
                dismiss()
            }
        }
        var selAccountTypeId: String? = null
        if (TextUtils.isEmpty(mSelAccountTypeId)) {
            val bundle = arguments
            if (bundle != null) {
                selAccountTypeId = bundle.getString(SEL_ACCOUNT_TYPE_ID)
            }
        } else {
            selAccountTypeId = mSelAccountTypeId
        }
        loadAccountTypeData(selAccountTypeId)
        view.findViewById<View>(R.id.cancel).setOnClickListener { dismiss() }

        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(activity!!, R.style.DatePickerBottomDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // 设置Content前设定

        dialog.setContentView(R.layout.dialog_accounttype_sel)
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


    fun setSelAccountType(selAccountTypeId: String?) {
        mSelAccountTypeId = selAccountTypeId
    }


    private fun loadAccountTypeData(selAccountTypeId: String?) {
        try {
            val resInputStream = BkApp.appContext.resources.openRawResource(R.raw.account_type)
            val accountTypeList = BkApp.bkJackson.readValue<List<AccountType>>(resInputStream,
                    object : TypeReference<List<AccountType>>() {})
            mAdapter?.setNewData(accountTypeList)
            mAdapter?.setSelPos(selAccountTypeId)
        } catch (e: IOException) {
            ToastUtils.showShort("读取失败")
            LogUtils.e("loadPayTypeData failed->", e)
        }
    }


    private class AccountTypeListAdapter(layoutResId: Int) : BaseQuickAdapter<AccountType, BaseViewHolder>(layoutResId) {

        private var mSelPos = -1

        fun setSelPos(accountTypeId: String?) {
            for (i in 0 until data.size) {
                val item = data[i]
                if (TextUtils.equals(item.accountTypeId, accountTypeId)) {
                    mSelPos = i
                    break
                }
            }
            notifyDataSetChanged()
        }


        override fun convert(helper: BaseViewHolder, accountType: AccountType) {
            val icon = helper.getView<ImageView>(R.id.account_type_icon)
            icon.setImageDrawable(DrawableUtil.getDrawableByName(accountType.accountTypeIcon))
            helper.setText(R.id.account_type_name, accountType.accountTypeName)
            helper.getView<View>(R.id.account_type_sel).visibility = if (mSelPos == helper.adapterPosition) View.VISIBLE else View.GONE
        }
    }

    companion object {

        const val TAG = "AccountTypeSelDialog"
        const val SEL_ACCOUNT_TYPE_ID = "SEL_ACCOUNT_TYPE_ID"
    }
}
