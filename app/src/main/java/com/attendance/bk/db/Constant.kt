package com.attendance.bk.db

/**
 * Created by Chen xuJie on 2019/7/4.
 */

/**
 * 图片上传状态
 */
const val IMAGE_STATE_UPLOAD_NO = 0 //图片未上传
const val IMAGE_STATE_UPLOAD_YES = 1//图片已经上传

/**
 * 项目状态
 */
const val PROJECT_STATE_UN_FINISH = 0//项目未完成
const val PROJECT_STATE_FINISH = 1//项目已完成
/**
 * 回收站數據類型
 */
const val RECYCLE_BIN_TYPE_TRADE = 0//交易回收类型
const val RECYCLE_BIN_TYPE_BOOK = 1//账本回收类型
const val RECYCLE_BIN_TYPE_BOOK_SET = 2//账套回收类型
const val RECYCLE_BIN_TYPE_PROJECT = 3//项目回收类型

/**
 * 交易收支类型
 */
const val TRADE_TYPE_IN = 0//收入
const val TRADE_TYPE_OUT = 1//支出

/**
 * 交易流水类型
 */
const val TRADE_TYPE_NORMAL = 0//普通记一笔流水
const val TRADE_TYPE_TRANSFER = 1//转账流水
const val TRADE_TYPE_LOAN = 2//借贷流水

/**
 * 应收应付预收预付等交易相关的收款还款等交易流水billId
 */
const val TRADE_RECEIVABLE_RECEIVE = "1"//应收款收款billId
const val TRADE_RECEIVED_PAYMENT = "2"//预收款发货款billId
const val TRADE_PAYABLE_REFUND = "3"//应付款还款billId
const val TRADE_PREPAYMENT_PAYMENT = "4"//预付款收货款billId
const val TRADE_TRANSFER_MONEY = "5"//转账本金billId
const val TRADE_TRANSFER_FEE = "6"//转账手续费billId
const val TRADE_LOAN_MONEY = "7"//借贷本金billId
const val TRADE_LOAN_BACK_MONEY = "8"//借贷收款或者还款billId
const val TRADE_LOAN_INTEREST_MONEY = "9"//借贷产生的利息交易billId
const val TRADE_ACCOUNT_DIFF_MONEY = "10"//账户修改余额产生的变更流水


/**
 * 收支类型
 */
const val BILL_TYPE_OUT = 0//支出
const val BILL_TYPE_IN = 1//收入

/**
 * 交易状态
 */
const val TRADE_STATE_UN_FINISH = 0//交易未完成（应收款，预收款，应付款，预付款未结清的情况）
const val TRADE_STATE_FINISH = 1////交易已完成（应收款，预收款，应付款，预付款已结清的情况和其他交易的状态）
const val TRADE_STATE_BAD_FINISH = 2//交易已完成,但是坏账了（应收款，预收款，应付款，预付款已结清的情况和其他交易的状态）


/**
 * 借贷的状态
 */
const val LOAN_STATE_UN_FINISH = 0//借贷等待收款还款未完成
const val LOAN_STATE_FINISH = 1////借贷关系已完成
const val LOAN_STATE_BAD_FINISH = 2//交易已完成,但是坏账了