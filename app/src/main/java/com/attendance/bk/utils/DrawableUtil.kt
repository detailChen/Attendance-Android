package com.attendance.bk.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.attendance.bk.BkApp
import com.blankj.utilcode.util.ConvertUtils
import java.util.*

/**
 * Created by CXJ
 * Created date 2019/1/9/009
 */
object DrawableUtil {

    const val CORNER_TYPE_LEFT = 0x10//左边有圆角
    const val CORNER_TYPE_RIGHT = 0x11//右边有圆角
    const val CORNER_TYPE_UP = 0x12//上边有圆角
    const val CORNER_TYPE_DOWN = 0x13//下边有圆角

    /**
     * 用于缓存图片名对应的Id，加快速度，value为负表示图片为VectorDrawable
     */
    private val mDrawableNameId = HashMap<String, Int>()


    /**
     * 有背景色的圆角矩形
     *
     * @param context
     * @param color
     * @param cornerSize
     * @return
     */
    fun fillCornerDrawable(context: Context, @ColorRes color: Int, cornerSize: Int): GradientDrawable {
        val gd = GradientDrawable()
        gd.setColor(ContextCompat.getColor(context, color))
        val corner = ConvertUtils.dp2px(cornerSize.toFloat()).toFloat()
        gd.cornerRadius = corner
        return gd
    }

    /**
     * 无背景色有描边的圆角矩形
     *
     * @param context
     * @param color
     * @param cornerSize
     * @param strokeWidth
     * @return
     */
    fun strokeCornerDrawable(context: Context, @ColorRes color: Int, cornerSize: Int, strokeWidth: Float): GradientDrawable {
        val gd = GradientDrawable()
        gd.setStroke(ConvertUtils.dp2px(strokeWidth), ContextCompat.getColor(context, color))
        gd.cornerRadius = ConvertUtils.dp2px(cornerSize.toFloat()).toFloat()
        return gd
    }


    /**
     * 有背景色有描边的圆角矩形
     *
     * @param context
     * @param bgColor
     * @param strokeColor
     * @param cornerSize
     * @return
     */
    fun fillStrokeCornerDrawable(context: Context, @ColorRes bgColor: Int, @ColorRes strokeColor: Int, cornerSize: Int): GradientDrawable {
        val bg = GradientDrawable()
        bg.setColor(ContextCompat.getColor(context, bgColor))
        val corner = ConvertUtils.dp2px(cornerSize.toFloat()).toFloat()
        bg.setStroke(ConvertUtils.dp2px(1f), ContextCompat.getColor(context, strokeColor))
        bg.cornerRadius = corner
        return bg
    }

    /**
     * 有背景色的某一边有圆角的矩形
     *
     * @param context
     * @param color
     * @param cornerSize
     * @param cornerType
     * @return
     */
    fun fillCornerRectDrawable(context: Context, @ColorRes color: Int, cornerSize: Int, cornerType: Int): GradientDrawable {
        val bg = GradientDrawable()
        bg.setColor(ContextCompat.getColor(context, color))
        val corner = ConvertUtils.dp2px(cornerSize.toFloat()).toFloat()
        var radii: FloatArray? = null
        when (cornerType) {
            CORNER_TYPE_LEFT -> radii = floatArrayOf(corner, corner, 0f, 0f, 0f, 0f, corner, corner)
            CORNER_TYPE_RIGHT -> radii = floatArrayOf(0f, 0f, corner, corner, corner, corner, 0f, 0f)
            CORNER_TYPE_UP -> radii = floatArrayOf(corner, corner, corner, corner, 0f, 0f, 0f, 0f)
            CORNER_TYPE_DOWN -> radii = floatArrayOf(0f, 0f, 0f, 0f, corner, corner, corner, corner)
        }
        bg.cornerRadii = radii
        return bg
    }


    /**
     * 有描边的某一边有圆角的矩形
     *
     * @param context
     * @param color
     * @param cornerSize
     * @param strokeWidth
     * @param cornerType
     * @return
     */
    fun strokeCornerRectDrawable(context: Context, @ColorRes color: Int,
                                 cornerSize: Int, strokeWidth: Int, cornerType: Int): GradientDrawable {
        val bg = GradientDrawable()
        bg.setStroke(ConvertUtils.dp2px(strokeWidth.toFloat()), ContextCompat.getColor(context, color))
        val corner = ConvertUtils.dp2px(cornerSize.toFloat()).toFloat()
        var radii: FloatArray? = null
        when (cornerType) {
            CORNER_TYPE_LEFT -> radii = floatArrayOf(corner, corner, 0f, 0f, 0f, 0f, corner, corner)
            CORNER_TYPE_RIGHT -> radii = floatArrayOf(0f, 0f, corner, corner, corner, corner, 0f, 0f)
            CORNER_TYPE_UP -> radii = floatArrayOf(corner, corner, corner, corner, 0f, 0f, 0f, 0f)
            CORNER_TYPE_DOWN -> radii = floatArrayOf(0f, 0f, 0f, 0f, corner, corner, corner, corner)
        }
        bg.cornerRadii = radii
        return bg
    }

    fun getDrawableByName(name: String?): Drawable? {
        if (name == null || name == "") return null
        var resId = mDrawableNameId[name]
        val res = BkApp.appContext.resources
        if (resId == null) {
            resId = res.getIdentifier(name, "drawable", BkApp.appContext.packageName)
            if (resId == 0) {
                return null
            }
            mDrawableNameId[name] = resId
        }
        return res.getDrawable(resId, null)
    }

    fun getGradientDrawable(startColor: String, endColor: String, corner: Int): GradientDrawable {
        val gd = GradientDrawable()
        gd.gradientType = GradientDrawable.LINEAR_GRADIENT
        gd.orientation = GradientDrawable.Orientation.LEFT_RIGHT
        gd.cornerRadius = ConvertUtils.dp2px(corner.toFloat()).toFloat()
        gd.colors = intArrayOf(Color.parseColor(startColor), Color.parseColor(endColor))
        return gd
    }

}
