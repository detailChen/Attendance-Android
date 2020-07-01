package com.attendance.bk.wxapi

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.attendance.bk.BkApp
import com.attendance.bk.BuildConfig
import com.attendance.bk.bean.WXLoginResult
import com.attendance.bk.bus.RequestWXInfoSuccessEvent
import com.attendance.bk.showToast
import com.attendance.bk.utils.workerThreadChange
import com.blankj.utilcode.util.LogUtils
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL


/**
 * 微信回调
 */
class WXEntryActivity : Activity(), IWXAPIEventHandler {

    private var api: IWXAPI? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        api = WXAPIFactory.createWXAPI(applicationContext, BuildConfig.wxAppId)
        api?.registerApp(BuildConfig.wxAppId)
        api?.handleIntent(intent, this)
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        api?.handleIntent(intent, this)
    }


    override fun onDestroy() {
        super.onDestroy()
        api?.detach()
    }


    override fun onReq(req: BaseReq) {
    }


    override fun onResp(resp: BaseResp) {
        when (resp.type) {
            1 -> {//登录
                val wxLoginRes = resp as SendAuth.Resp
                if (wxLoginRes.errCode == BaseResp.ErrCode.ERR_OK) {
                    requestWxTokenAndUserMsg(wxLoginRes.code)
                } else if (wxLoginRes.errCode == BaseResp.ErrCode.ERR_USER_CANCEL
                        || wxLoginRes.errCode == BaseResp.ErrCode.ERR_AUTH_DENIED) {
                    showToast("微信登陆取消")
                    finish()
                } else {
                    showToast("微信登陆出错")
                    finish()
                }
            }
//            2 -> {//分享
//                var shareResIntent: Intent? = null
//                when (resp.errCode) {
//                    BaseResp.ErrCode.ERR_USER_CANCEL -> shareResIntent = WXShareHelper.generateCancelRes(WXShareHelper.SHARE_WEIXIN)
//                    BaseResp.ErrCode.ERR_OK -> WXShareHelper.generateSuccessRes(WXShareHelper.SHARE_WEIXIN)
//                    BaseResp.ErrCode.ERR_SENT_FAILED,
//                    BaseResp.ErrCode.ERR_BAN -> {
//                        shareResIntent = WXShareHelper.generateFailedRes(WXShareHelper.SHARE_WEIXIN, resp.errStr, Throwable("WX share failed"))
//                    }
//                }
//                setResult(RESULT_OK, shareResIntent)
//                WXLoginHelper.setupWxResData(shareResIntent)
//                finish()
//            }
        }
    }


    @SuppressLint("AutoDispose", "CheckResult")
    private fun requestWxTokenAndUserMsg(code: String) {
        Observable.create<WXLoginResult> {
            val authURL = StringBuilder().apply {
                append("https://api.weixin.qq.com/sns/oauth2/access_token?")
                append("appid=")
                append(BuildConfig.wxAppId)
                append("&secret=")
                append(BuildConfig.wxAppSecret)
                append("&code=")
                append(code)
                append("&grant_type=authorization_code")
            }
            // 请求授权，获取token，openId，unionId
            var url = URL(authURL.toString())
            val okHttpClient = OkHttpClient()
            var response = okHttpClient.newCall(Request.Builder().url(url).build()).execute()
            val result = StringBuilder()
            if (response.isSuccessful) {
                val reader = BufferedReader(InputStreamReader(response.body()?.byteStream()))
                var line: String? = reader.readLine()
                while (line != null) {
                    result.append(line)
                    line = reader.readLine()
                }
                reader.close()
            } else {
                throw  IOException("get wx openId,unionId,accessToken failed!")
            }

            // 获取个人信息：
            val tokenMsg = JSONObject(result.toString())
            val openId = tokenMsg.optString("openid")
            val unionId = tokenMsg.optString("unionid")
            val accessToken = tokenMsg.optString("access_token")

            url = URL("https://api.weixin.qq.com/sns/userinfo?access_token=$accessToken&openid=$openId")
            response = okHttpClient.newCall(Request.Builder().url(url).build()).execute()
            if (response.isSuccessful) {
                result.delete(0, result.length)
                val reader = BufferedReader(InputStreamReader(response.body()?.byteStream()))
                var line: String? = reader.readLine()
                while (line != null) {
                    result.append(line)
                    line = reader.readLine()
                }
                reader.close()
            } else {
                throw  IOException("get wx user info failed!")
            }
            val uMsg = JSONObject(result.toString())
            val resObj = WXLoginResult(
                    openId = openId,
                    unionId = unionId,
                    nickname = uMsg.optString("nickname"),
                    icon = uMsg.optString("headimgurl"),
                    gender = uMsg.optString("sex"),
                    location = "${uMsg.optString("country")}-${uMsg.optString("province")}-${uMsg.optString("city")}"
            )
            it.onNext(resObj)
            it.onComplete()
        }.workerThreadChange().subscribe({
            BkApp.eventBus.post(RequestWXInfoSuccessEvent(it))
            finish()
        }, {
            showToast("读取微信数据失败")
            LogUtils.e("requestWxTokenAndUserMsg failed->", it)
            finish()
        })
    }


//    @SuppressLint("AutoDispose", "CheckResult")
//    private fun requestWxTokenAndUserMsg(wxLoginRes: SendAuth.Resp) {
//        RxHttpUtils
//                .createApi("bossbk", SPStaticUtils.getString(SPKey.SP_KEY_DOMAIN), ApiService::class.java)
//                .loginByWx(wxLoginRes.code, wxLoginRes.lang, wxLoginRes.country)
//                .workerThreadChange()
//                .subscribe({ loginData ->
//                    //生成新用户
////                    BkDb.instance.userDao().generateNewUser(loginData.userId, loginData.nickname, loginData.headImgUrl)
//                    //保存token
//                    SPUtils.put(SPKey.SP_KEY_USER_ID, loginData.userId)
//                    SPUtils.put(SPKey.userAccessTokenKey(loginData.userId), loginData.token)
//
//                    val intent = WXLoginHelper.generateSuccessRes()
//                    setResult(RESULT_OK, intent)
//                    WXLoginHelper.setupWxResData(intent)
//                    finish()
//                }, { throwable ->
//                    showToast("获取token失败")
//                    logE("requestWxTokenAndUserMsg failed->$throwable")
//                    finish()
//                })
//    }


}
