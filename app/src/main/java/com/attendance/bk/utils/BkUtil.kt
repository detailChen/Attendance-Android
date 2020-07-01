package com.attendance.bk.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.attendance.bk.BkApp
import com.blankj.utilcode.util.*
import com.attendance.bk.listener.TextWatcherAdapter
import com.attendance.bk.login.LoginByWX
import io.reactivex.Completable
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Chen xuJie on 2018/12/19/019
 */
object BkUtil {


    private val MONEY_FORMAT_1 = DecimalFormat("0.00")
    private val MONEY_FORMAT_2 = DecimalFormat("0.000")

    /**
     * 四舍五入保留指定位数小数
     *
     * @param d
     * @return
     */
    fun keepDecimalPlaces(count: Int, d: Double): Double {
        val b = BigDecimal(d)
        return b.setScale(count, BigDecimal.ROUND_HALF_UP).toDouble()
    }

    //默认保留两位小数
    fun keepDecimalPlaces(d: Double): Double {
        val b = BigDecimal(d)
        return b.setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
    }

    fun keepDecimalPlaces(count: Int, value: BigDecimal): BigDecimal {
        return value.setScale(count, BigDecimal.ROUND_HALF_UP)
    }

    @JvmOverloads
    fun parseMoney(money: String, onError: Double = 0.0): Double {
        var m = money
        m = m.replace(",", ".")
        return try {
            java.lang.Double.parseDouble(m)
        } catch (e: Exception) {
            onError
        }

    }

    /**
     * 格式化金额数字
     *
     * @param money 金额
     * @param sign  是否带正负号
     * @return 格式化后的金额
     */
    fun formatMoney(money: Double, sign: Boolean = false): String {
        var m = money
        m = keepDecimalPlaces(m)
        if (m == 0.0) {
            return "0.00"
        }
        val fm = MONEY_FORMAT_1.format(m).replace("-", "")
        return (if (m > 0) if (sign) "+" else "" else "-") + fm
    }

    /**
     * 带千位符的格式化金额数字
     *
     * @param money 金额
     * @param sign  是否带正负号
     * @return 格式化后的金额
     */
    fun formatMoneyWithTS(money: Double, sign: Boolean = false): String {
        var m = money
        m = keepDecimalPlaces(m)
        if (m == 0.0) {
            return "0.00"
        }
        var fMoney = MONEY_FORMAT_1.format(if (m >= 0) m else -m)
        val dotIdx = fMoney.lastIndexOf(".")

        if (dotIdx >= 3 && useThousandsSeparator()) { //取出整数部分加分隔符
            var sIntMoney = fMoney.substring(0, dotIdx)
            sIntMoney = sIntMoney.replace("(?<=\\d)(?=(?:\\d{3})+$)".toRegex(), ",")
            val sFloatMoney = fMoney.substring(dotIdx)
            fMoney = sIntMoney + sFloatMoney
        }

        return (if (m > 0) if (sign) "+" else "" else "-") + fMoney
    }


    /**
     * 从包含千位符的数字中获取正确的数字
     *
     * @param money
     * @return
     */
    fun getRightMoney(money: String): Double {
        return if (!money.contains(",")) {
            java.lang.Double.valueOf(money)
        } else {
            val moneyArray = money.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val sb = StringBuilder()
            for (s in moneyArray) {
                sb.append(s)
            }
            java.lang.Double.valueOf(sb.toString())
        }
    }

    fun formatMoney(money: Double, decimalDigits: Int): String {
        return MONEY_FORMAT_2.format(keepDecimalPlaces(decimalDigits, money))
    }

    /**
     * 是否使用千位分隔符
     *
     * @return true 使用  false 不使用
     */
    fun useThousandsSeparator(): Boolean {
        return SPUtils.getInstance().getBoolean(SPKey.SP_KEY_THOUSANDS_SEPARATOR, false)
    }

    /**
     * 对所有的输入数字的EditText做限制,保留小数位数等，最大输入位数为9
     *
     * @param edit          EditText
     * @param charSequence  editText的内容
     * @param decimalDigits 保留的小数位数
     */
    fun limitEtDigitInput(edit: EditText, charSequence: CharSequence, decimalDigits: Int) {
        var s = charSequence.toString()
        //如果包含小数点，要确保只能输入一位小数点
        if (s.contains(".")) {
            var pointCount = 0
            for (i in 0 until s.length) {
                if (s[i] == '.') {
                    pointCount++
                }
                if (pointCount > 1) {
                    s = s.substring(0, s.indexOf('.') + 1)
                    edit.setText(s)
                    edit.setSelection(edit.length())
                    break
                }
            }
            //保留小数位数
            if (s.length - 1 - s.indexOf(".") > decimalDigits) {
                s = s.substring(0, s.indexOf(".") + decimalDigits + 1)
                edit.setText(s)
                edit.setSelection(edit.length())
                return
            }
        }

        //第一位输的是'.',自动在前面加0，显示'0.'
        if (s.startsWith(".")) {
            s = "0$s"
            edit.setText(s)
            edit.setSelection(2)
        }

        //第一位以0开头，在输入第二位，直接取第二位
        if (s.startsWith("0") && s.trim { it <= ' ' }.length > 1) {
            val c = s[1]
            if (c in '0'..'9') {
                edit.setText(c.toString())
                edit.setSelection(1)
            }
        }

        if (s.length > 9) {
            if (!s.contains(".")) {//不包含小数点，最多9位
                edit.setText(s.substring(0, 9))
                edit.setSelection(edit.length())
            }
        }
        //最多能输入12个字符，包括小数
        if (s.length > 12) {
            edit.setText(s.substring(0, 12))
        }
    }


    /**
     * 对EditTex输入金额做限制
     *
     * @param money
     */
    fun limitEtDigitInput(money: EditText) {
        money.addTextChangedListener(object : TextWatcherAdapter() {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                limitEtDigitInput(money, s, 2)
            }
        })
    }

    /**
     * 对EditTex输入金额做限制
     *
     * @param money
     */
    fun limitEtDigitInput(money: EditText, decimalDigits: Int) {
        money.addTextChangedListener(object : TextWatcherAdapter() {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                limitEtDigitInput(money, s, decimalDigits)
            }
        })
    }

    /**
     * 对文字输入做限制，不能超过maxCount个字
     */
    fun limitEtTextInput(editText: EditText, maxCount: Int) {
        editText.addTextChangedListener(object : TextWatcherAdapter() {
            override fun onTextChanged(c: CharSequence, start: Int, before: Int, count: Int) {
                var s = c
                if (s.length > maxCount) {
                    s = s.subSequence(0, maxCount)
                    editText.setText(s)
                    editText.setSelection(editText.length())
                    ToastUtils.showShort(String.format(Locale.CHINA, "不能超过%d个字哦", maxCount))
                }
            }
        })
    }


    fun getResUri(context: Context, resId: Int): Uri {
        return Uri.parse("android.resource://" + context.packageName
                + "/drawable/" + context.resources.getResourceEntryName(resId))
    }


    /**
     * 保存图片到本地相册
     */
    fun saveBitmap(context: Context, view: View, bitmapName: String) {
        Completable.create {
            //创建对应大小的Bitmap
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.RGB_565)
            val canvas = Canvas(bitmap)
            view.draw(canvas)

            //存储
            val file: File
            val fileName: String = if (RomUtils.isXiaomi()) {
                Environment.getExternalStorageDirectory().path + "/DCIM/Camera/" + bitmapName
            } else {
                Environment.getExternalStorageDirectory().path + "/DCIM/" + bitmapName
            }
            file = File(fileName)
            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                fos.flush()
                MediaStore.Images.Media.insertImage(context.contentResolver, file.absolutePath, bitmapName, null)
            } catch (e: IOException) {
                LogUtils.e(e.message)
            } finally {
                bitmap.recycle()
                if (fos != null) {
                    CloseUtils.closeIOQuietly(fos)
                }
            }
            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://$fileName")))

        }.subscribeOn(RxWorkerUtil.workerScheduler()).subscribe()
    }


    fun parseColor(color: String): Int {
        var c = color
        if (TextUtils.isEmpty(color)) {
            return 0
        }
        if (!color.startsWith("#")) {
            c = "#$color"
        }
        return try {
            Color.parseColor(c)
        } catch (e: Exception) {
            0
        }
    }

    /**
     * 设置PopupWidow显示时背景的透明效果
     *
     * @param bgAlpha 透明度
     */
    fun dimBackground(activity: Activity?, bgAlpha: Float) {
        val window = activity?.window ?: return
        val lp = window.attributes
        lp.alpha = bgAlpha
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window.attributes = lp
    }


    /**
     * 将后端返回的流文件保存下来以便调试
     */
    fun saveBytesToFile(bytes: ByteArray, fileName: String): File {
        val destDir = BkApp.appContext.cacheDir
        // 将流写入到zip文件
        val serverZipFile = File(destDir, "${fileName}.zip")
        FileIOUtils.writeFileFromBytesByStream(serverZipFile, bytes)

        // 清理解压文件夹
        val serverFileDir = File(destDir, "unZip${fileName}Dir")
        if (!serverFileDir.isDirectory) {
            serverFileDir.mkdirs()
        } else { // 解压前删除之前的旧文件
            for (f in serverFileDir.listFiles()) {
                f.delete()
            }
        }
        // 解压缩
        ZipUtils.unzipFile(serverZipFile, serverFileDir)

        // 在解压缩目录找到json文件
        var serverJsonFile: File? = null
        val files = serverFileDir.listFiles()
        for (f in files) {
            if (f.isFile && f.name.endsWith(".json")) {
                serverJsonFile = f
                break
            }
        }

        val dateTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA).format(Date())
        val destFile = File(serverFileDir, "${fileName}_$dateTime.json")
        // json文件重命名为目标文件（方便调试）
        serverJsonFile?.renameTo(destFile)
        return destFile
    }

    fun getMetaData(context: Context, metaDataKey: String): String {
        return try {
            val applicationInfo = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            val metaDataValue = applicationInfo.metaData.get(metaDataKey)
            metaDataValue?.toString() ?: ""
        } catch (var4: PackageManager.NameNotFoundException) {
            var4.printStackTrace()
            ""
        }
    }

    //根据图片比例调整view的狂傲
    fun updateImgHeight(imageView: ImageView) {
        val d = DrawableUtil.getDrawableByName("cover_image_01") ?: return

        if (imageView.width > 0) {
            val destH = (d.intrinsicHeight.toFloat() / d.intrinsicWidth * imageView.width).toInt()
            val lp = imageView.layoutParams
            if (lp.height != destH && destH > 0) {
                lp.height = destH
                imageView.layoutParams = lp
            }
        } else {
            imageView.post(Runnable {
                val d1 = imageView.drawable ?: return@Runnable
                val destH =
                        (d1.intrinsicHeight.toFloat() / d1.intrinsicWidth * imageView.width).toInt()
                val lp = imageView.layoutParams
                if (lp.height != destH && destH > 0) {
                    lp.height = destH
                    imageView.layoutParams = lp
                }
            })
        }
    }

    fun showVisitorUserLoginDialog(activity: Activity) {
        AlertDialog.Builder(activity)
            .setTitle("温馨提示")
            .setMessage("游客模式下不支持此操作，请登录")
            .setPositiveButton("登录") { dialog, _ ->
                activity.startActivity(Intent(activity, LoginByWX::class.java))
                dialog.dismiss()
            }
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

}