package com.attendance.bk.utils

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 日期格式化辅助类
 *
 * @author Chen Xujie
 * @since 2019-01-02
 */
object DateUtil {

    private val date = ThreadLocal<DateFormat>()
    private val time = ThreadLocal<DateFormat>()

    val dateFormat: DateFormat
        get() {
            var hm = date.get()
            if (hm == null) {
                hm = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
                date.set(hm)
            }
            return hm
        }


    val timeFormat: DateFormat
        get() {
            var hm = time.get()
            if (hm == null) {
                hm = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA)
                time.set(hm)
            }
            return hm
        }

    val dayZeroTimeCal: Calendar
        get() = setDayZeroTime(Calendar.getInstance())

    val updateTime: String
        get() = timeFormat.format(Date())

    val currDate: String
        get() = dateFormat.format(Date())

    val nowDate: Date
        get() = setDayZeroTime(Calendar.getInstance()).time

    fun formatDate(date: Date): String {
        return dateFormat.format(date)
    }

    fun formatDate(date: Date, formatString: String): String {
        return SimpleDateFormat(formatString, Locale.getDefault()).format(date)
    }

    fun parseDate(date: String?): Date {
        if (date == null) {
            throw IllegalArgumentException("date can't be null")
        }
        var d = date
        try {
            if (d.length > "yyyy-MM-dd".length) {
                d = d.substring(0, 9)
            }
            return dateFormat.parse(d)
        } catch (e: ParseException) {
            throw RuntimeException(e)
        }
    }


    fun parseTime(time: String): Date {
        try {
            return timeFormat.parse(time)
        } catch (e: ParseException) {
            throw RuntimeException(e)
        }
    }


    /**
     * 抹除日期小于天单位的数值为0，抹除后小时，分钟，秒钟均为0
     *
     * @param c Calendar
     * @return
     */
    fun setDayZeroTime(c: Calendar): Calendar {
        c.set(Calendar.MILLISECOND, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.HOUR_OF_DAY, 0)
        return c
    }

    /**
     * 获取2个日期时间天数间隔（仅比较天数，无视时分秒）
     *
     * @param d1 日期1
     * @param d2 日期2
     * @return 天数间隔
     */
    fun getDayDiff(d1: Date, d2: Date): Int {
        val cal = Calendar.getInstance()
        cal.time = d1
        setDayZeroTime(cal)
        val t1 = cal.timeInMillis
        cal.time = d2
        setDayZeroTime(cal)
        val t2 = cal.timeInMillis
        return Math.abs((t1 - t2) / (1000 * 3600 * 24)).toInt()
    }
}
