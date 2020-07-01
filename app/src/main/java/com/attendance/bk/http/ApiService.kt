package com.attendance.bk.http

import com.attendance.bk.bean.LoginResult
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


/**
 *
 * Created by CXJ
 * Created date 2019/12/3/003
 *
 */
interface ApiService {


    /**
     * 微信登陆接口
     */
    @FormUrlEncoded
    @POST("/login/wx")
    fun loginByWx(
        @Field("openId") openId: String,
        @Field("unionId") unionId: String,
        @Field("nickName") nickName: String,
        @Field("icon") icon: String,
        @Field("gender") gender: String,
        @Field("location") location: String
    ): Single<ApiResult<LoginResult>>

    /**
     * 同步接口
     *
     * @param userId         用户ID
     * @param lastVersion   上次同步成功时间
     * @return 同步请求
     */
    @FormUrlEncoded
    @POST("/sync/syncData")
    fun syncData(
        @Field("userId") userId: String,
        @Field("lastVersion") lastVersion: Long
    ): Call<ResponseBody>
}