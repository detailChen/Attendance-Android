package com.attendance.bk

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import com.attendance.bk.db.BkDb
import com.attendance.bk.db.table.User
import com.attendance.bk.http.ApiService
import com.attendance.bk.http.RetrofitClient
import com.attendance.bk.utils.RxBus
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ProcessUtils
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import io.reactivex.plugins.RxJavaPlugins


class BkApp : Application() {


    override fun onCreate() {
        super.onCreate()
        if (!ProcessUtils.isMainProcess()) {
            return
        }
        appContext = applicationContext
        eventBus = RxBus()
        mainHandler = Handler(Looper.getMainLooper())
        initRxJavaPlugins()
        initObjectMapper()
//        initFresco()
//        initOSSService()
//        initUmeng()
    }


    private fun initRxJavaPlugins() {
        RxJavaPlugins.setErrorHandler { LogUtils.e("BkApp", "get rx UncaughtException ->", it) }
    }

    private fun initObjectMapper() {
        bkJackson = ObjectMapper()
        bkJackson
            // 序列化时, 禁止自动缩进 (格式化) 输出的 json (压缩输出)
            .configure(SerializationFeature.INDENT_OUTPUT, false)
            // 序列化时, 对于没有任何 public methods / properties 的类, 序列化不报错
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            // 忽略未知的字段
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            // 所有实例中的 空字段, null 字段, 都要参与序列化
            .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }


//    private fun initFresco() {
//        Fresco.initialize(this)
//    }

//    private fun initOSSService() {
//        val credentialProvider = BkOSSAuthCredentialsProvider(SPKey.STS_SERVER_URL)
//        val conf = ClientConfiguration()
//        conf.connectionTimeout = 15 * 1000 // 连接超时，默认15秒
//        conf.socketTimeout = 15 * 1000 // socket超时，默认15秒
//        conf.maxConcurrentRequest = 5 // 最大并发请求书，默认5个
//        conf.maxErrorRetry = 2 // 失败后最大重试次数，默认2次
//        val ossClient = OSSClient(applicationContext, SPKey.OSS_ENDPOINT, credentialProvider, conf)
//        OSSLog.enableLog()
//        ossService = OssService(ossClient, SPKey.BUCKET_NAME)
//        val ossImageClient = OSSClient(applicationContext, SPKey.OSS_IMG_ENDPOINT, credentialProvider, conf)
//        ossImageService = ImageService(OssService(ossImageClient, SPKey.BUCKET_NAME))
//    }


//    private fun initUmeng(){
//        UMConfigure.init(this, BkUtil.getMetaData(this,"UMENG_APPKEY"), BkUtil.getMetaData(this,"UMENG_CHANNEL"), 1, null)
//    }


    companion object {

        lateinit var appContext: Context
        lateinit var eventBus: RxBus
        lateinit var mainHandler: Handler
        lateinit var bkJackson: ObjectMapper

//        lateinit var ossService: OssService
//        lateinit var ossImageService: ImageService

        var currUser: User = User()
            get() {
                if (TextUtils.isEmpty(field.userId)) {
                    synchronized(BkApp::class.java) {
                        if (TextUtils.isEmpty(field.userId)) {
                            field = BkDb.instance.userDao().getCurrUser()
                        }
                    }
                }
                return field
            }

        fun currUserId(): String = currUser.userId

        val apiService: ApiService by lazy {
            RetrofitClient.getRetrofit().create(ApiService::class.java)
        }
    }


}

