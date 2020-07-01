package com.attendance.bk.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.attendance.bk.R

import com.bk.datepicker.date.YearMonthPicker

/**
 * 时间选择器，弹出框
 * Created by ycuwq on 2018/1/6.
 */
class MonthPickerDialog : DialogFragment() {

    private var mMonthPicker: YearMonthPicker? = null
    private var mSelectedYear = -1
    private var mSelectedMonth = -1
    private var mListener: OnMonthSelectedListener? = null
    private var mIsShowAnimation = true

    fun setOnMonthSelectedListener(listener: OnMonthSelectedListener) {
        mListener = listener
    }

    fun showAnimation(show: Boolean) {
        mIsShowAnimation = show
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_month_picker, container)
        mMonthPicker = view.findViewById(R.id.month_picker)

        view.findViewById<View>(R.id.close).setOnClickListener { v -> dismiss() }
        view.findViewById<View>(R.id.complete_sel).setOnClickListener { v ->
            if (mListener != null) {
                mListener!!.onMonthSelect(mMonthPicker!!.year, mMonthPicker!!.month - 1)
            }
            dismiss()
        }

        if (mSelectedYear > 0) {
            setSelectedMonth()
        }

        return view
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(activity!!, R.style.DatePickerBottomDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // 设置Content前设定

        dialog.setContentView(R.layout.dialog_month_picker)
        dialog.setCanceledOnTouchOutside(true) // 外部点击取消

        val window = dialog.window
        if (window != null) {
            if (mIsShowAnimation) {
                window.attributes.windowAnimations = R.style.DatePickerDialogAnim
            }
            val lp = window.attributes
            lp.gravity = Gravity.BOTTOM // 紧贴底部
            lp.width = WindowManager.LayoutParams.MATCH_PARENT // 宽度持平
            lp.dimAmount = 0.35f
            window.attributes = lp
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }

        return dialog
    }

    fun setSelectedMonth(year: Int, month: Int) {
        mSelectedYear = year
        mSelectedMonth = month + 1
        setSelectedMonth()
    }

    private fun setSelectedMonth() {
        if (mMonthPicker != null) {
            mMonthPicker!!.setYearMonth(mSelectedYear, mSelectedMonth, false)
        }
    }

    interface OnMonthSelectedListener {
        fun onMonthSelect(year: Int, month: Int)
    }

    companion object {

        const val TAG = "MonthPickerDialog"
    }


}
