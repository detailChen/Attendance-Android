package com.attendance.bk.http

/**
 * Created by luyao
 * on 2018/3/13 14:38
 */
data class ApiResult<out T>(
    val code: Int = 0,
    val desc: String = "",
    val data: T? = null,
    val time: String = ""
) {


    fun isResultOk(): Boolean {
        return code == 200
    }

    override fun toString(): String {
        return "code=$code, desc=$desc, data=$data,time=$time"
    }
}