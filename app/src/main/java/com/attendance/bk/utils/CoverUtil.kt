package com.attendance.bk.utils

import com.attendance.bk.BkApp
import com.attendance.bk.R

import java.util.Random

/**
 * Created by Chen xuJie on 2019/5/9.
 */
object CoverUtil {


    val randomBookCover: String
        get() {
            val bookCovers = BkApp.appContext.resources.getStringArray(R.array.def_cover_name)
            val random = Random()
            return bookCovers[random.nextInt(bookCovers.size)]
        }
}
