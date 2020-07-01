package com.attendance.bk

import android.app.Activity
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ToastUtils

/**
 * Created by Chen xuJie on 2020/2/28.
 */

fun Activity.showToast(toast:String){
    ToastUtils.showShort(toast)
}

fun Fragment.showToast(toast:String){
    ToastUtils.showShort(toast)
}
