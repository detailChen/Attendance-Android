package com.attendance.bk.utils


/**
 * 一些配置类
 * Created by Chen xuJie on 2018/12/17/017
 */
object SPKey {

//  const val SP_KEY_DOMAIN = "http://10.168.1.179:80"
    const val SP_KEY_DOMAIN = "http://139.196.84.229:80"

    const val SP_KEY_USER_ID = "SP_KEY_USER_ID"

    const val SP_KEY_ACCESS_TOKEN = "SP_KEY_ACCESS_TOKEN"

    //是否使用千位符分割
    const val SP_KEY_THOUSANDS_SEPARATOR = "SP_KEY_THOUSANDS_SEPARATOR"

    /**
     * 购买vip的订单id
     */
    const val SP_KEY_VIP_ORDER_ID = "SP_KEY_VIP_ORDER_ID"

    //FileProvider的authorities的值
    const val FILE_PROVIDER_AUTHORITIES = "com.boss.bk.fileprovider"


    fun userAccessTokenKey(userId: String): String {
        return "$userId$SP_KEY_ACCESS_TOKEN"
    }

    //访问的endpoint地址
    const val OSS_ENDPOINT = "http://oss-cn-shanghai.aliyuncs.com"
    //访问的图片endpoint地址
    const val OSS_IMG_ENDPOINT = "http://img-cn-shanghai.aliyuncs.com"

    //STS 鉴权服务器地址，使用前请参照文档 https://help.aliyun.com/document_detail/31920.html 介绍配置STS 鉴权服务器地址。
    const val STS_SERVER_URL = "$SP_KEY_DOMAIN/sts/getsts"//STS 地址
    //bucket名称
    const val BUCKET_NAME = "bbk-image-1"

    const val SP_KEY_GDT_APP_ID = "SP_KEY_GDT_APP_ID"
    const val SP_KEY_SPLASH_AD_ID = "SP_KEY_SPLASH_AD_ID"
    const val SP_KEY_QQ_GROUP_KEY = "SP_KEY_QQ_GROUP_KEY"
    const val SP_KEY_QQ_GROUP = "SP_KEY_QQ_GROUP"
    const val SP_KEY_WEIXIN = "SP_KEY_WEIXIN"

    const val SP_KEY_HAS_LOGIN_BY_WX = "SP_KEY_HAS_LOGIN_BY_WX"


}
