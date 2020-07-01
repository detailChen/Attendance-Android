package com.attendance.bk.page

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.attendance.bk.BkApp
import com.attendance.bk.R
import com.attendance.bk.adapter.TextAdapter
import com.attendance.bk.adapter.TextAdapter.Companion.TYPE_CONTENT
import com.attendance.bk.adapter.TextAdapter.Companion.TYPE_SUB_TITLE_1
import com.attendance.bk.adapter.TextAdapter.Companion.TYPE_SUB_TITLE_2
import com.attendance.bk.adapter.TextAdapter.Companion.TYPE_TITLE
import com.attendance.bk.bean.TextData
import com.attendance.bk.utils.ToolbarUtil
import kotlinx.android.synthetic.main.activity_pricavy_protocol.*
import kotlinx.android.synthetic.main.toolbar.*


/**
 *
 * Created by CXJ
 * Created date 2019/9/29/029
 *
 */
class PrivacyProtocolActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pricavy_protocol)
        initViewData()
    }


    private fun initViewData() {
        val privacyProtocolType = intent.getIntExtra(PARAM_PRIVACY_PRIVACY, 0)
        setSupportToolbar(toolbar)
        ToolbarUtil.setMainTitle (if(privacyProtocolType == SHOW_USER_PROTOCOL) "用户协议" else "隐私政策")


        text_list.layoutManager = LinearLayoutManager(this)
        val textAdapter = TextAdapter(this)
        text_list.adapter = textAdapter

        val dataList = ArrayList<TextData>()
        if (privacyProtocolType == SHOW_PRIVACY_AGREEMENT) {
            dataList.add(TextData(TYPE_TITLE, getContent(R.string.privacy_agreement_title)))
            dataList.add(TextData(TYPE_CONTENT, getContent(R.string.privacy_agreement_summary)))
            dataList.add(TextData(TYPE_SUB_TITLE_1, getContent(R.string.privacy_agreement_subtitle_1)))
            dataList.add(TextData(TYPE_SUB_TITLE_2, getContent(R.string.privacy_agreement_subtitle_1_1)))
            dataList.add(TextData(TYPE_CONTENT, getContent(R.string.privacy_agreement_subtitle_1_1_content)))
            dataList.add(TextData(TYPE_SUB_TITLE_2, getContent(R.string.privacy_agreement_subtitle_1_2)))
            dataList.add(TextData(TYPE_CONTENT, getContent(R.string.privacy_agreement_subtitle_1_2_content)))
            dataList.add(TextData(TYPE_SUB_TITLE_1, getContent(R.string.privacy_agreement_subtitle_2)))
            dataList.add(TextData(TYPE_CONTENT, getContent(R.string.privacy_agreement_subtitle_2_content)))
            dataList.add(TextData(TYPE_SUB_TITLE_1, getContent(R.string.privacy_agreement_subtitle_3)))
            dataList.add(TextData(TYPE_SUB_TITLE_2, getContent(R.string.privacy_agreement_subtitle_3_1)))
            dataList.add(TextData(TYPE_CONTENT, getContent(R.string.privacy_agreement_subtitle_3_1_content)))
            dataList.add(TextData(TYPE_SUB_TITLE_2, getContent(R.string.privacy_agreement_subtitle_3_2)))
            dataList.add(TextData(TYPE_CONTENT, getContent(R.string.privacy_agreement_subtitle_3_2_content)))
            dataList.add(TextData(TYPE_SUB_TITLE_1, getContent(R.string.privacy_agreement_subtitle_4)))
            dataList.add(TextData(TYPE_CONTENT, getContent(R.string.privacy_agreement_subtitle_4_content)))
            dataList.add(TextData(TYPE_SUB_TITLE_1, getContent(R.string.privacy_agreement_subtitle_5)))
            dataList.add(TextData(TYPE_CONTENT, getContent(R.string.privacy_agreement_subtitle_5_content)))
            dataList.add(TextData(TYPE_SUB_TITLE_1, getContent(R.string.privacy_agreement_subtitle_6)))
            dataList.add(TextData(TYPE_SUB_TITLE_2, getContent(R.string.privacy_agreement_subtitle_6_1)))
            dataList.add(TextData(TYPE_CONTENT, getContent(R.string.privacy_agreement_subtitle_6_1_content)))
            dataList.add(TextData(TYPE_SUB_TITLE_2, getContent(R.string.privacy_agreement_subtitle_6_2)))
            dataList.add(TextData(TYPE_CONTENT, getContent(R.string.privacy_agreement_subtitle_6_2_content)))
            dataList.add(TextData(TYPE_SUB_TITLE_2, getContent(R.string.privacy_agreement_subtitle_6_3)))
            dataList.add(TextData(TYPE_CONTENT, getContent(R.string.privacy_agreement_subtitle_6_3_content)))
            dataList.add(TextData(TYPE_SUB_TITLE_1, getContent(R.string.privacy_agreement_subtitle_7)))
            dataList.add(TextData(TYPE_CONTENT, getContent(R.string.privacy_agreement_subtitle_7_content)))
            dataList.add(TextData(TYPE_SUB_TITLE_1, getContent(R.string.privacy_agreement_subtitle_8)))
            dataList.add(TextData(TYPE_CONTENT, getContent(R.string.privacy_agreement_subtitle_8_content)))
            dataList.add(TextData(TYPE_SUB_TITLE_1, getContent(R.string.privacy_agreement_subtitle_9)))
            dataList.add(TextData(TYPE_CONTENT, getContent(R.string.privacy_agreement_subtitle_9_content)))
        } else {
            dataList.add(TextData(TYPE_TITLE,getContent(R.string.user_protocol_title)))
            dataList.add(TextData(TYPE_CONTENT,getContent(R.string.user_protocol_summary)))
            dataList.add(TextData(TYPE_SUB_TITLE_1,getContent(R.string.user_protocol_sub_title_1)))
            dataList.add(TextData(TYPE_CONTENT,getContent(R.string.user_protocol_sub_title_1_1_content)))
            dataList.add(TextData(TYPE_CONTENT,getContent(R.string.user_protocol_sub_title_1_2_content)))
            dataList.add(TextData(TYPE_CONTENT,getContent(R.string.user_protocol_sub_title_1_3_content)))
            dataList.add(TextData(TYPE_CONTENT,getContent(R.string.user_protocol_sub_title_1_4_content)))
            dataList.add(TextData(TYPE_SUB_TITLE_1,getContent(R.string.user_protocol_sub_title_2)))
            dataList.add(TextData(TYPE_CONTENT,getContent(R.string.user_protocol_sub_title_2_1_content)))
            dataList.add(TextData(TYPE_CONTENT,getContent(R.string.user_protocol_sub_title_2_2_content)))
            dataList.add(TextData(TYPE_SUB_TITLE_1,getContent(R.string.user_protocol_sub_title_3)))
            dataList.add(TextData(TYPE_CONTENT,getContent(R.string.user_protocol_sub_title_3_1_content)))
            dataList.add(TextData(TYPE_CONTENT,getContent(R.string.user_protocol_sub_title_3_2_content)))
            dataList.add(TextData(TYPE_SUB_TITLE_1,getContent(R.string.user_protocol_sub_title_4)))
            dataList.add(TextData(TYPE_CONTENT,getContent(R.string.user_protocol_sub_title_4_1_content)))
            dataList.add(TextData(TYPE_CONTENT,getContent(R.string.user_protocol_sub_title_4_2_content)))
            dataList.add(TextData(TYPE_CONTENT,getContent(R.string.user_protocol_sub_title_4_3_content)))
            dataList.add(TextData(TYPE_SUB_TITLE_1,getContent(R.string.user_protocol_sub_title_5)))
            dataList.add(TextData(TYPE_CONTENT,getContent(R.string.user_protocol_sub_title_5_1_content)))
            dataList.add(TextData(TYPE_CONTENT,getContent(R.string.user_protocol_sub_title_5_2_content)))
            dataList.add(TextData(TYPE_CONTENT,getContent(R.string.user_protocol_sub_title_5_3_content)))
            dataList.add(TextData(TYPE_SUB_TITLE_1,getContent(R.string.user_protocol_sub_title_6)))
            dataList.add(TextData(TYPE_CONTENT,getContent(R.string.user_protocol_sub_title_6_1_content)))
            dataList.add(TextData(TYPE_CONTENT,getContent(R.string.user_protocol_sub_title_6_2_content)))
            dataList.add(TextData(TYPE_CONTENT,getContent(R.string.user_protocol_sub_title_6_3_content)))
            dataList.add(TextData(TYPE_SUB_TITLE_1,getContent(R.string.user_protocol_sub_title_7)))
            dataList.add(TextData(TYPE_CONTENT,getContent(R.string.user_protocol_sub_title_7_1_content)))
            dataList.add(TextData(TYPE_CONTENT,getContent(R.string.user_protocol_sub_title_7_2_content)))
            dataList.add(TextData(TYPE_CONTENT,getContent(R.string.user_protocol_sub_title_7_3_content)))
            dataList.add(TextData(TYPE_SUB_TITLE_1,getContent(R.string.user_protocol_sub_title_8)))
            dataList.add(TextData(TYPE_CONTENT,getContent(R.string.user_protocol_sub_title_8_1_content)))
            dataList.add(TextData(TYPE_SUB_TITLE_1,getContent(R.string.user_protocol_sub_title_9)))
            dataList.add(TextData(TYPE_CONTENT,getContent(R.string.user_protocol_sub_title_9_1_content)))
            dataList.add(TextData(TYPE_CONTENT,getContent(R.string.user_protocol_sub_title_9_2_content)))
            dataList.add(TextData(TYPE_CONTENT,getContent(R.string.user_protocol_sub_title_9_3_content)))
            dataList.add(TextData(TYPE_CONTENT,getContent(R.string.user_protocol_sub_title_9_4_content)))
            dataList.add(TextData(TYPE_CONTENT,getContent(R.string.user_protocol_sub_title_9_5_content)))
            dataList.add(TextData(TYPE_CONTENT,getContent(R.string.user_protocol_sub_title_9_6_content)))
        }


        textAdapter.updateData(dataList)
    }


    private fun getContent(idRes: Int): String {
        return resources.getText(idRes).toString()
    }

    companion object {

        const val PARAM_PRIVACY_PRIVACY = "PARAM_PRIVACY_PRIVACY"

        const val SHOW_PRIVACY_AGREEMENT = 0
        const val SHOW_USER_PROTOCOL = 1

        fun startIntent(showType : Int):Intent{
            return Intent(BkApp.appContext,PrivacyProtocolActivity::class.java).also {
                it.putExtra(PARAM_PRIVACY_PRIVACY,showType)
            }
        }
    }
}