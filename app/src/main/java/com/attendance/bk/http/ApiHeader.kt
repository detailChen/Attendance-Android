package com.attendance.bk.http

import com.attendance.bk.BuildConfig
import com.attendance.bk.http.BuildHeadersListener
import com.attendance.bk.utils.SPKey
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.SPStaticUtils
import java.net.URLEncoder
import java.util.*

/**
 * 统一请求头管理
 * Created by CXJ
 * Created date 2019/6/20/020
 */
class ApiHeader : BuildHeadersListener {

    override fun buildHeaders(): Map<String, String> {
        val hashMap = HashMap<String, String>()
        hashMap["appVersion"] = BuildConfig.VERSION_NAME
        hashMap["devType"] = "android"
//        hashMap["imei"] = YYUtil.getIMEI()
        hashMap["token"] = SPStaticUtils.getString(SPKey.SP_KEY_ACCESS_TOKEN)
        hashMap["product"] = URLEncoder.encode(AppUtils.getAppName(), "utf-8")
        //todo
        hashMap["channel"] = URLEncoder.encode("官网", "utf-8")//渠道值，友盟定义
        hashMap["brand"] = DeviceUtils.getManufacturer()
        hashMap["model"] = DeviceUtils.getModel()
        return hashMap
    }

}
