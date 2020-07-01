package com.attendance.bk.bean.net

import com.attendance.bk.db.table.Trade
import com.boss.bk.db.table.Image
import com.boss.bk.db.table.Trade


/**
 * 添加或修改交易时的数据类
 * Created by Chen xuJie on 2019/9/22.
 */
class TradeData {

    //该条流水
    var trade: Trade? = null

//    //该条流水图片
//    var imageList: List<Image>? = null


    constructor(trade: Trade) {
        this.trade = trade
    }

//    constructor(trade: Trade, imageList: List<Image>?) {
//        this.trade = trade
//        this.imageList = imageList
//    }

}