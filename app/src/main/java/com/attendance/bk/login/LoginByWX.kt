package com.attendance.bk.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import com.attendance.bk.BkApp
import com.attendance.bk.BuildConfig
import com.attendance.bk.R
import com.attendance.bk.bean.WXLoginResult
import com.attendance.bk.bus.RequestWXInfoSuccessEvent
import com.attendance.bk.bus.SYNC_FAIL
import com.attendance.bk.bus.SYNC_OK
import com.attendance.bk.bus.SyncDataEvent
import com.attendance.bk.db.BkDb
import com.attendance.bk.db.GenerateDefUserData
import com.attendance.bk.page.BaseActivity
import com.attendance.bk.page.PrivacyProtocolActivity
import com.attendance.bk.page.main.MainActivity
import com.attendance.bk.utils.SPKey
import com.attendance.bk.utils.workerThreadChange
import com.blankj.utilcode.util.*
import com.boss.bk.sync.DataSyncHelper
import com.jaeger.library.StatusBarUtil
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import kotlinx.android.synthetic.main.activity_login_by_wx.*


/**
 *微信登陆页面
 * Created by CXJ
 * Created date 2019/7/15/015
 *
 */
class LoginByWX : BaseActivity() {


    private var hasAgree = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_by_wx)
        initView()
        addEBus()
    }


    private fun addEBus() {
        BkApp.eventBus.toUIObserver().`as`(bindLifecycle()).subscribe {
            when (it) {
                is RequestWXInfoSuccessEvent -> doWxLogin(it.wxLoginResult)
                is SyncDataEvent -> {
                    when (it.syncState) {
                        SYNC_OK -> {
                            dismissDialog()
                            ActivityUtils.finishAllActivities()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                        SYNC_FAIL -> {
                            dismissDialog()
                            showToast("数据同步失败，请重新登录")
                        }
                    }
                }
            }
        }
    }

    private fun initView() {
        StatusBarUtil.setColor(this, Color.WHITE, 0)
        StatusBarUtil.setLightMode(this)

        tv_login_wx.setOnClickListener {
            loginByWx()
        }
        if (SPStaticUtils.getBoolean(SPKey.SP_KEY_HAS_LOGIN_BY_WX, false)) {
            tv_login_yk.visibility = View.GONE
        } else {
            tv_login_yk.visibility = View.VISIBLE
            tv_login_yk.setOnClickListener {
                loginByYk()
            }
        }
        checkbox.setImageResource(R.drawable.ic_xuanzhong)
        checkbox.setOnClickListener {
            hasAgree = if (hasAgree) {
                checkbox.setImageResource(R.drawable.ic_xuanzhong_not)
                false
            } else {
                checkbox.setImageResource(R.drawable.ic_xuanzhong)
                true
            }
        }

        val text = findViewById<TextView>(R.id.user_protocol)
        SpanUtils.with(text)
                .append("我已阅读并同意以下")
                .append("用户协议").setForegroundColor(ColorUtils.getColor(R.color.text_third))
                .setClickSpan(clickProtocol)
                .append("和")
                .append("隐私政策").setForegroundColor(ColorUtils.getColor(R.color.text_third))
                .setClickSpan(clickPrivacy)
                .create()
    }


    private val clickProtocol = object : ClickableSpan() {
        override fun onClick(widget: View) {
            startActivity(PrivacyProtocolActivity.startIntent(PrivacyProtocolActivity.SHOW_USER_PROTOCOL))
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.color = ColorUtils.getColor(R.color.text_third)
            ds.isUnderlineText = true
        }
    }
    private val clickPrivacy = object : ClickableSpan() {
        override fun onClick(widget: View) {
            startActivity(PrivacyProtocolActivity.startIntent(PrivacyProtocolActivity.SHOW_PRIVACY_AGREEMENT))
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.color = ColorUtils.getColor(R.color.text_third)
            ds.isUnderlineText = true
        }
    }

    private fun loginByYk() {
        val visitorUser = BkDb.instance.userDao().getVisitorUser()
        if (visitorUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            return
        }
        GenerateDefUserData.generateDefUserData().workerThreadChange().`as`(bindLifecycle())
                .subscribe({ success ->
                    if (success) {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                }, {
                    showToast("数据初始化失败")
                    LogUtils.e("generateDefUserData failed->", it)
                })
    }


    private fun loginByWx() {
        val wxapi = WXAPIFactory.createWXAPI(applicationContext, BuildConfig.wxAppId)
        wxapi.registerApp(BuildConfig.wxAppId)
        if (!wxapi.isWXAppInstalled) {
            showToast("未安装微信客户端！")
            return
        }

        val req = SendAuth.Req()
        req.scope = "snsapi_userinfo"
        req.state = "bbk_wx_login"
        wxapi.sendReq(req)
    }


    private fun doWxLogin(wxLoginResult: WXLoginResult) {
        BkApp.apiService.loginByWx(
                        openId = wxLoginResult.openId,
                        unionId = wxLoginResult.unionId,
                        nickName = wxLoginResult.nickname,
                        icon = wxLoginResult.icon,
                        gender = wxLoginResult.gender,
                        location = wxLoginResult.location)
                .workerThreadChange()
                .`as`(bindLifecycle())
                .subscribe({
                    if (it.isResultOk() && it.data != null) {
                        //保存當前用戶id
                        SPStaticUtils.put(SPKey.SP_KEY_USER_ID, it.data.userId)
                        //保存token
                        SPStaticUtils.put(SPKey.SP_KEY_ACCESS_TOKEN, it.data.token)
                        //同步数据
                        doSyncData(it.data.userId)
                        SPStaticUtils.put(SPKey.SP_KEY_HAS_LOGIN_BY_WX, true)
                    } else {
                        showToast(it.desc)
                    }
                }, {
                    LogUtils.e("doWxLogin failed->", it)
                })
    }

    private fun doSyncData(userId: String) {
        showDialog("数据同步中，请稍后...")
        DataSyncHelper.doSyncData(userId)
    }


}