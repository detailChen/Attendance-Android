package com.attendance.bk.utils

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.BarUtils
import com.attendance.bk.page.BaseActivity
import com.attendance.bk.BkApp
import com.attendance.bk.R

/**
 * Created by CXJ
 * Created date 2019/1/9/009
 */
object ToolbarUtil {

    @SuppressLint("StaticFieldLeak")
    private lateinit var mToolbar: Toolbar


    interface OnSubTitleClickListener {
        fun onSubTitleClick()
    }

    fun setSupportToolbar(activity: BaseActivity, toolbar: Toolbar) {
        mToolbar = toolbar
        activity.setSupportActionBar(toolbar)
        toolbar.contentInsetStartWithNavigation = 0
        toolbar.setTitleTextAppearance(activity, R.style.Toolbar_TitleText)
        val actionBar = activity.supportActionBar ?: return
        actionBar.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar!!.title = ""
        setBackArrowColor(ContextCompat.getColor(activity, R.color.white))
        //        mToolbar.setNavigationIcon(R.drawable.ic_back);
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


    fun setBackArrowColor(color: Int) {
        val backArrow = mToolbar.navigationIcon
        backArrow!!.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        mToolbar.navigationIcon = backArrow
    }

    fun setStatusBarTopPadding() {
        mToolbar.setPadding(0, BarUtils.getStatusBarHeight(), 0, 0)
    }

}
