package com.attendance.bk.sync

import com.attendance.bk.db.TableName
import com.attendance.bk.db.table.*
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by Chen xuJie on 2019/7/3.
 */
data class SyncJsonObject(
    @JsonProperty("code")
    val code: Int = 1,
    @JsonProperty("desc")
    val desc: String? = null,
    @JsonProperty("syncVersion")
    val syncVersion: Long = 0,
    @JsonProperty("syncData")
    val syncData: SyncData? = null


) {
    data class SyncData(

        /**
         * 用户
         */
        @JsonProperty(TableName.TB_NAME_USER)
        var userList: List<User>? = null,


        /**
         * 账本
         */
        @JsonProperty(TableName.TB_NAME_BOOK)
        var bookList: List<Book>? = null,


        /**
         * 交易
         */
        @JsonProperty(TableName.TB_NAME_TRADE)
        var tradeList: List<Trade>? = null,

        /**
         * 类别
         */
        @JsonProperty(TableName.TB_NAME_BILL_TYPE)
        var billTypeList: List<BillType>? = null,


        /**
         * 账户
         */
        @JsonProperty(TableName.TB_NAME_ACCOUNT)
        var accountList: List<Account>? = null

//            /**
//             * 转账
//             */
//            @JsonProperty(TableName.TB_NAME_TRANSFER)
//            var transferList: List<Transfer>? = null,
//
//            /**
//             * 借贷
//             */
//            @JsonProperty(TableName.TB_NAME_LOAN)
//            var loanList: List<Loan>? = null
    )
}


