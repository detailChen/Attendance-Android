package com.attendance.bk.utils

import android.annotation.TargetApi
import android.app.KeyguardManager
import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import android.os.CancellationSignal

import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils

/**
 * 指纹识别/解锁辅助类
 *
 * @author Chen Xujie
 * @since 2019-06-06
 */

class FingerprintHelper {

    private var mCancelSignal: CancellationSignal? = null

    @TargetApi(23)
    fun beginListen(ctx: Context, onCheckOk: Runnable, onCheckFailed: Runnable) {
        val context = ctx.applicationContext
        if (isSupportFinger(context) != 1) {
            return
        }

        mCancelSignal = CancellationSignal()

        val callback = object : FingerprintManager.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                mCancelSignal = null
                finishListen(context)
                ToastUtils.showShort(errString)
                onCheckFailed.run()
            }

            override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence) {
                ToastUtils.showShort(helpString)
            }

            override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult) {
                mCancelSignal = null
                finishListen(context)
                onCheckOk.run()
            }

            override fun onAuthenticationFailed() {
                ToastUtils.showShort("不识别的指纹，请重试")
            }
        }

        val fingerprintManager = context.getSystemService(FingerprintManager::class.java) ?: return
        fingerprintManager.authenticate(null, mCancelSignal, 0, callback, null)
    }

    /**
     * 指纹监听回调 onAuthenticationError 或 onAuthenticationSucceeded 之后，则应该结束。但是如果再调用CancellationSignal cancel
     * 会导致onAuthenticationError 再次被调用。因此不调用cancel了。但是引发新问题是AuthenticationCallback对象无法被释放，
     * 导致内存泄露。（一加3T Android7.1测试泄露了）
     * 此处在回调结束后，重新调用指纹监听，采用一个空回调，并立即取消监听，解决回调和内存泄露问题
     */
    @TargetApi(23)
    private fun finishListen(context: Context) {
        val fingerprintManager = context.getSystemService(FingerprintManager::class.java) ?: return
        val mCancelSignal = CancellationSignal()
        fingerprintManager.authenticate(null, mCancelSignal, 0, object : FingerprintManager.AuthenticationCallback() {

        }, null)
        mCancelSignal.cancel()
    }

    @TargetApi(16)
    fun cancelListen() {
        if (mCancelSignal != null && !mCancelSignal!!.isCanceled) {
            try {
                mCancelSignal!!.cancel()
            } catch (e: Exception) {
                // null
            }

        }
    }

    companion object {

        /**
         * 是否支持指纹并且有已设置的指纹。小米等厂商部分设备6.0等低版本也支持标准指纹API，此方法也支持这些设备
         *
         * @param context Context
         * @return 1 有可以使用的指纹。-1不支持指纹API，-2 硬件不支持，2没有设置其它安全解锁，3没有设置指纹
         */
        @TargetApi(23)
        fun isSupportFinger(context: Context): Int {
            if (!hasFingerApi(context)) {
                return -1
            }
            try {
                val keyguardManager = context.getSystemService(KeyguardManager::class.java)
                val fingerprintManager = context.getSystemService(FingerprintManager::class.java)
                        ?: return -1

                if (!fingerprintManager.isHardwareDetected) {
                    return -2
                }

                if (keyguardManager != null && !keyguardManager.isKeyguardSecure) {
                    return 2
                }

                if (!fingerprintManager.hasEnrolledFingerprints()) {
                    return 3
                }
            } catch (e: Throwable) {
                return -1
            }

            return 1
        }

        /**
         * 通过反射检测系统是否支持标准指纹API
         *
         * @param context Context
         * @return true支持，false不支持
         */
        private fun hasFingerApi(context: Context): Boolean {
            val fingerApiSupportSP = "device_finger_api_support"
            // 检查是否存在该值，不必每次都通过反射来检查
            val checkedStr = SPUtils.getInstance().getString(fingerApiSupportSP, "-1")
            if ("0" == checkedStr) {
                return false
            } else if ("1" == checkedStr) {
                return true
            }
            return try {
                // 通过反射判断是否存在标准指纹API相关类
                Class.forName("android.hardware.fingerprint.FingerprintManager")
                Class.forName("android.app.KeyguardManager")
                SPUtils.getInstance().put(fingerApiSupportSP, "1")
                true
            } catch (e: Throwable) {
                SPUtils.getInstance().put(fingerApiSupportSP, "0")
                false
            }

        }
    }


}
