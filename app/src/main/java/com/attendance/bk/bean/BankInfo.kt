package com.attendance.bk.bean


import com.chad.library.adapter.base.entity.MultiItemEntity
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author Ren ZeQiang
 * @since 2018/9/17.
 */
class BankInfo : MultiItemEntity {

    //名称
    @JsonProperty("bank_name")
    var name: String? = null
    @JsonProperty("bank_icon")
    var icon: String? = null
    @JsonProperty("first_char")
    var firstChar: String? = null
    //类型
    @JsonIgnore
    var type: Int = 0
    //是不是常用
    @JsonProperty("is_common_use")
    var isCommonUse: String? = null


    constructor()

    constructor(name: String, type: Int) {
        this.name = name
        this.type = type
    }

    constructor(name: String, icon: String) {
        this.name = name
        this.icon = icon
    }

    constructor(name: String, type: Int, isCommonUse: String, icon: String) {
        this.name = name
        this.type = type
        this.isCommonUse = isCommonUse
        this.icon = icon
    }

    override fun getItemType(): Int {
        return type
    }

    companion object {

        const val TYPE_PINYIN = 0
        const val TYPE_BANK = 1
    }
}
