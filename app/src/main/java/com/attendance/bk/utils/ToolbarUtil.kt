package com.attendance.bk.utils

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.attendance.bk.BkApp
import com.attendance.bk.R
import com.attendance.bk.page.BaseActivity
import com.blankj.utilcode.util.BarUtils


/**
 * Created by CXJ
 * Created date 2019/1/9/009
 */
object ToolbarUtil {

    @SuppressLint("StaticFieldLeak")
    private lateinit var mToolbar: RelativeLayout


    interface OnSubTitleClickListener {
        fun onSubTitleClick()
    }

    fun setSupportToolbar(activity: BaseActivity, toolbar: RelativeLayout) {
        mToolbar = toolbar
        val back = toolbar.findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            activity.onBackPressed()
        }

    }

    fun setMainTitle(title: String) {
        val mainTitle = mToolbar.findViewById<TextView>(R.id.main_title)
        mainTitle.text = title
    }

    fun setMainTitle(titleRes: Int) {
        val mainTitle = mToolbar.findViewById<TextView>(R.id.main_title)
        mainTitle.text = BkApp.appContext.getText(titleRes)
    }


    fun setSubTitle(title: String) {
        val subTitle = mToolbar.findViewById<TextView>(R.id.sub_title)
        subTitle.visibility = View.VISIBLE
        subTitle.text = title
    }

    fun setSubTitleVisible(visible: Boolean) {
        val subTitle = mToolbar.findViewById<TextView>(R.id.sub_title)
        subTitle.visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun setOnSubTitleClick(listener: OnSubTitleClickListener) {
        val subTitle = mToolbar.findViewById<TextView>(R.id.sub_title)
        subTitle.setOnClickListener { listener.onSubTitleClick() }
    }



    fun setStatusBarTopPadding() {
        mToolbar.setPadding(0, BarUtils.getStatusBarHeight(), 0, 0)
    }

}
