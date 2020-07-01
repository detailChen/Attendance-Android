//package com.attendance.bk.utils
//
//import android.text.TextUtils
//import com.attendance.bk.BkApp
//import com.attendance.bk.R
//import com.blankj.utilcode.util.LogUtils
//import com.blankj.utilcode.util.ToastUtils
//import com.boss.bk.BkApp
//import com.boss.bk.R
//import com.boss.bk.bean.db.PayType
//import com.fasterxml.jackson.core.type.TypeReference
//import java.io.IOException
//import java.util.*
//
///**
// * 支付类别工具类
// * Created by CXJ
// * Created date 2019/4/28/028
// */
//object PayTypeUtil {
//
//
//    var payTypeList: List<PayType> = ArrayList()
//        private set
//
//    init {
//        try {
//            val resInputStream = BkApp.appContext.resources.openRawResource(R.raw.pay_type)
//            payTypeList = BkApp.bkJackson.readValue(resInputStream, object : TypeReference<List<PayType>>() {
//            })
//        } catch (e: IOException) {
//            ToastUtils.showShort("读取失败")
//            LogUtils.e("loadPayTypeData failed->", e)
//        }
//
//    }
//
//
//    fun getPayTypeById(payTypeId: String?): PayType? {
//        if (TextUtils.isEmpty(payTypeId)){
//            return null
//        }
//        for (payType in payTypeList) {
//            if (TextUtils.equals(payTypeId, payType.payTypeId)) {
//                return payType
//            }
//        }
//        return null
//    }
//
//    fun getPayTypeNameById(payTypeId: String?): String? {
//        for (payType in payTypeList) {
//            if (TextUtils.equals(payTypeId, payType.payTypeId)) {
//                return payType.name
//            }
//        }
//        return null
//    }
//}
