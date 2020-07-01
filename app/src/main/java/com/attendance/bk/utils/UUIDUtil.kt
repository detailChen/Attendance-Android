package com.attendance.bk.utils

import java.util.*

/**
 * Description:
 * User: cxj
 * Date: 2019-08-05
 * Time: 23:12
 */
object UUIDUtil {

    fun uuid(): String = UUID.randomUUID().toString().replace("-".toRegex(), "")

}
