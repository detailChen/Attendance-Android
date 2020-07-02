package com.attendance.bk.http

import com.attendance.bk.bean.LoginResult
import com.attendance.bk.bean.net.TradeData
import com.attendance.bk.db.table.Account
import com.attendance.bk.db.table.BillType
import com.attendance.bk.db.table.Trade
import com.attendance.bk.db.table.UserExtra
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
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



    /**
     * 添加交易
     */
    @POST("/trade/addTrade")
    fun addTrade(@Body tradeData: TradeData): Single<ApiResult<TradeData>>

    /**
     * 修改交易
     */
    @POST("/trade/updateTrade")
    fun updateTrade(@Body tradeData: TradeData): Single<ApiResult<TradeData>>

    @POST("/trade/settlementTrade")
    fun settlementTrade(@Body tradeData: TradeData): Single<ApiResult<TradeData>>

    /**
     * 删除交易流水
     */
    @POST("/trade/deleteTrade")
    fun deleteTrade(@Body trade: Trade): Single<ApiResult<Trade>>

    @POST("/billType/addBillType")
    fun addBillType(@Body billType: BillType): Single<ApiResult<BillType>>

    @POST("/billType/modifyBillType")
    fun modifyBillType(@Body billType: BillType): Single<ApiResult<BillType>>

    @POST("/billType/deleteBillType")
    fun deleteBillType(@Body billType: BillType): Single<ApiResult<BillType>>


    @POST("userExtra/updateUserExtra")
    fun updateUserExtra(@Body userExtra: UserExtra): Single<ApiResult<UserExtra>>

    @POST("/account/addAccount")
    fun addAccount(@Body account: Account): Single<ApiResult<Account>>

    @POST("/account/modifyAccount")
    fun modifyAccount(@Body account: Account): Single<ApiResult<Account>>

    @POST("/account/deleteAccount")
    fun deleteAccount(@Body account: Account): Single<ApiResult<Account>>
}