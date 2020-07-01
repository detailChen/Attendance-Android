package com.attendance.bk.page

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.attendance.bk.R
import com.attendance.bk.utils.RxLifecycleUtil
import com.attendance.bk.view.BkImageView
import com.blankj.utilcode.util.RomUtils.isMeizu
import com.blankj.utilcode.util.RomUtils.isXiaomi
import com.blankj.utilcode.util.ToastUtils
import com.attendance.bk.utils.ToolbarUtil
import com.idescout.sql.SqlScoutServer
import com.jaeger.library.StatusBarUtil
import com.uber.autodispose.AutoDisposeConverter


@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    protected lateinit var mActivity: Activity

    /**
     * progressDialog.
     */
    private var mProgressDialog: Dialog? = null

    val context: Context
        get() = this


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = this
        window.setBackgroundDrawableResource(R.drawable.bg_activity)
    }


    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    open fun setSupportToolbar(toolbar: RelativeLayout) {
        ToolbarUtil.setSupportToolbar(this, toolbar)
        if (canUseStatus() && lightToolbar()) {
            setToolbarColor()
        }
    }

    /**
     * 判断是否可以使用透明状态栏
     *
     * @return true 使用透明状态栏
     */
    private fun canUseStatus(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return true
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return false
        }
        if (isXiaomi() || isMeizu()) {
            return true
        }
        return false
    }

    protected fun setTranslucentForImageView(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.statusBarColor = Color.TRANSPARENT
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
    }


    protected fun <T> bindLifecycle(): AutoDisposeConverter<T> {
        return if (this is LifecycleOwner) {
            RxLifecycleUtil.bindLifecycle(this)
        } else {
            throw ClassCastException("please ")
        }
    }


    override fun onLowMemory() {
        super.onLowMemory()
        BkImageView.onLowMemory()
    }


    open fun setToolbarColor(@ColorRes colorRes: Int = R.color.text_third) {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, colorRes), 0)
        //        StatusBarUtil.setLightMode(this);
    }

    open fun lightToolbar(): Boolean {
        return true
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_MENU && event.isLongPress) {
            true
        } else super.onKeyDown(keyCode, event)
    }


    fun showToast(toast: String) {
        ToastUtils.showShort(toast)
    }

    fun showToast(@StringRes toastRes: Int) {
        ToastUtils.showShort(toastRes)
    }


    /**
     * show dialog.
     */
    fun showDialog(dialogText: String = "") {
        runOnUiThread {
            if (null == mProgressDialog) {
                mProgressDialog = Dialog(this@BaseActivity,
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
     * 设置对话框是否可以取消
     *
     * @param cancelable 是否可以取消
     */
    fun setProgressDialogCancelable(cancelable: Boolean) {
        runOnUiThread {
            if (null != mProgressDialog) {
                mProgressDialog!!.setCancelable(cancelable)
            }
        }
    }

    fun setProgressDialogCancelOutSide(cancelOutSide: Boolean) {
        runOnUiThread {
            if (null != mProgressDialog) {
                mProgressDialog!!.setCanceledOnTouchOutside(cancelOutSide)
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

    override fun onPointerCaptureChanged(hasCapture: Boolean) {

    }
}
