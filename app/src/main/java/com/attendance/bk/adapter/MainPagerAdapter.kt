package com.attendance.bk.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.attendance.bk.page.main.AttendanceFragment
import com.attendance.bk.page.main.BillFragment
import com.attendance.bk.page.main.FormFragment
import com.attendance.bk.page.main.MineFragment
import java.util.*

/**
 * Created by Chen xuJie on 2019/3/10.
 */
class MainPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val mFragmentList = ArrayList<Fragment>()

    init {
        mFragmentList.add(AttendanceFragment())
        mFragmentList.add(BillFragment())
        mFragmentList.add(FormFragment())
        mFragmentList.add(MineFragment())
    }

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }
}
