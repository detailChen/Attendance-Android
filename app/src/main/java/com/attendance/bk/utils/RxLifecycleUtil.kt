package com.attendance.bk.utils

import com.uber.autodispose.AutoDispose
import com.uber.autodispose.AutoDisposeConverter
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider

import androidx.lifecycle.LifecycleOwner


/**
 * Created by CXJ
 * Created date 2019/2/20/020
 */
object RxLifecycleUtil {

    fun <T> bindLifecycle(lifecycleOwner: LifecycleOwner): AutoDisposeConverter<T> {
        return AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(lifecycleOwner))
    }
}
