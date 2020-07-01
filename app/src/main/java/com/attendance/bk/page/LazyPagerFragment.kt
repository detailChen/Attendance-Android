package com.attendance.bk.page

import android.os.Bundle
import com.attendance.bk.page.BaseFragment

/**
 * 懒加载fragment,需要使用FragmentPagerAdapter才有效
 * Created by CXJ
 * Created date 2019/5/21/021
 */
abstract class LazyPagerFragment : BaseFragment() {


    protected var mIsViewInitiated = false
    protected var mIsVisibleToUser = false
    protected var mIsDataInitiated = false

    /**
     * 子类重写是否定位到当前页面时就加载数据.默认只有第一次到当前页面时加载一次数据
     */
    protected val isForceFetchData: Boolean
        get() = false

    //在onCreateView方法之后执行
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mIsViewInitiated = true
        prepareFetchData()
    }

    //要结合FragmentPagerAdapter使用才调用此方法
    //setUserVisibleHint(false)->setUserVisibleHint(true)->onActivityCreated()
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        mIsVisibleToUser = isVisibleToUser
        prepareFetchData()
    }

    protected abstract fun lazyData()

    private fun prepareFetchData() {
        if (mIsViewInitiated && mIsVisibleToUser && (!mIsDataInitiated || isForceFetchData)) {
            lazyData()
            mIsDataInitiated = true
        }
    }
}
