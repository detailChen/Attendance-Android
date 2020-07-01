package com.attendance.bk.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.attendance.bk.R
import com.bk.datepicker.date.DatePicker

/**
 * 时间选择器，弹出框
 * Created by ycuwq on 2018/1/6.
 */
class DatePickerDialog : DialogFragment() {


    private var mDatePicker: DatePicker? = null
    private var mSelectedYear = -1
    private var mSelectedMonth = -1
    private var mSelectedDay = -1
    private var mListener: OnDateSelectedListener? = null
    private var mIsShowAnimation = true

    fun setOnDateSelectedListener(listener: OnDateSelectedListener) {
        mListener = listener
    }

    fun showAnimation(show: Boolean) {
        mIsShowAnimation = show
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_date_picker, container)
        mDatePicker = view.findViewById(R.id.date_picker)

        view.findViewById<View>(R.id.close).setOnClickListener { dismiss() }
        view.findViewById<View>(R.id.complete_sel).setOnClickListener {
            mListener?.onDateSelect(mDatePicker!!.year, mDatePicker!!.month - 1, mDatePicker!!.day)
            dismiss()
        }

        if (mSelectedYear > 0) {
            setSelectedDate()
        }

        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(activity!!, R.style.DatePickerBottomDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // 设置Content前设定

        dialog.setContentView(R.layout.dialog_date_picker)
        dialog.setCanceledOnTouchOutside(true) // 外部点击取消

        val window = dialog.window

        if (mIsShowAnimation) {
            window?.attributes?.windowAnimations = R.style.DatePickerDialogAnim
        }
        val lp = window?.attributes
        lp?.gravity = Gravity.BOTTOM // 紧贴底部
        lp?.width = WindowManager.LayoutParams.MATCH_PARENT // 宽度持平
        lp?.dimAmount = 0.35f
        window?.attributes = lp
        window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)


        return dialog
    }

    fun setSelectedDate(year: Int, month: Int, day: Int) {
        mSelectedYear = year
        mSelectedMonth = month + 1
        mSelectedDay = day
        setSelectedDate()
    }

    private fun setSelectedDate() {
        mDatePicker?.setDate(mSelectedYear, mSelectedMonth, mSelectedDay, false)
    }

    interface OnDateSelectedListener {
        fun onDateSelect(year: Int, month: Int, day: Int)
    }

    companion object {

        const val TAG = "DatePickerDialog"
    }


}
