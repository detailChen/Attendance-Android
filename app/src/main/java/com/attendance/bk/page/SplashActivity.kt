package com.attendance.bk.page


import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.attendance.bk.R
import com.attendance.bk.db.BkDb
import com.attendance.bk.login.LoginByWX
import com.attendance.bk.page.main.MainActivity
import com.attendance.bk.utils.SPKey
import com.blankj.utilcode.util.SPStaticUtils


/**
 * 开屏页面
 */
class SplashActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val accessToken = SPStaticUtils.getString(SPKey.SP_KEY_ACCESS_TOKEN, "")
        if (TextUtils.isEmpty(accessToken)) {//第一次登陆或者处于游客模式
            val visitorUser = BkDb.instance.userDao().getVisitorUser()
            if (visitorUser != null) {
                gotoMain()
            } else {
                gotoLogin()
            }
        } else {
            gotoMain()
        }
    }


    fun gotoMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

//    private fun showSplashAd() {
//        FragmentUtils.add(supportFragmentManager, AdGDTFragment(), R.id.container)
//    }

    private fun gotoLogin() {
        startActivity(Intent(this, LoginByWX::class.java))
        finish()
    }


}
