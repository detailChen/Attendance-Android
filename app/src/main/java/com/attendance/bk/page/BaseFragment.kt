package com.attendance.bk.page

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.uber.autodispose.AutoDisposeConverter

import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.attendance.bk.R
import com.attendance.bk.utils.RxLifecycleUtil
import com.blankj.utilcode.util.Utils.runOnUiThread

/**
 * BaseFragment
 *
 * @author Chen Xujie
 * @since 2018-12-15
 */
abstract class BaseFragment : Fragment() {

    protected lateinit var layoutView: View
    protected var mContext: Context? = null


    protected abstract fun getLayoutRes(): Int

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = getContext()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgumentData()
        initInitialData()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutView = inflater.inflate(getLayoutRes(), container, false)
        initView(layoutView)
        initData()
        addEBus()
        return layoutView
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun getArgumentData() {
        val bundle = arguments
        if (bundle != null) {
            getBundleData(bundle)
        }
    }

    protected open fun getBundleData(bundle: Bundle) {}

    /**
     * 不耗时的初始化
     */
    protected open fun initInitialData() {}

    protected open fun initView(rootView: View) {}

    protected open fun initData() {}


    protected open fun addEBus() {}


    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }


    protected fun <T> bindLifecycle(): AutoDisposeConverter<T> {
        return RxLifecycleUtil.bindLifecycle(this as LifecycleOwner)
    }

    override fun onDetach() {
        super.onDetach()
    }

    /**
     * progressDialog.
     */
    private var mProgressDialog: Dialog? = null
    /**
     * show dialog.
     */
    fun showDialog(dialogText: String = "") {
        runOnUiThread {
            if (null == mProgressDialog) {
                mProgressDialog = Dialog(requireActivity(),
                    R.style.progressDialog
                )
                mProgressDialog?.setCancelable(true)
                mProgressDialog?.setContentView(R.layout.progress_dialog_content)
                mProgressDialog?.setCancelable(false)
                mProgressDialog?.setCanceledOnTouchOutside(false)
            }
            mProgressDialog?.findViewById<TextView>(R.id.dialog_text)?.text = dialogText
            if (mProgressDialog!!.isShowing.not()){
                mProgressDialog?.show()
            }
        }
    }
    /**
     * dismiss dialog
     */
    fun dismissDialog() {
        if (null != mProgressDialog && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
        }
    }

}
