package com.attendance.bk.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.attendance.bk.R
import com.bk.datepicker.date.DayPicker

/**
 * Created by Chen xuJie on 2020/5/2.
 */
class DayPickerDialog : DialogFragment() {


    private var mDayPicker: DayPicker? = null
    private var mTitle: TextView? = null
    private var mSelectedDay = -1
    private var mListener: OnDaySelectedListener? = null
    private var mIsShowAnimation = true

    fun setOnDaySelectedListener(listener: OnDaySelectedListener) {
        mListener = listener
    }

    fun showAnimation(show: Boolean) {
        mIsShowAnimation = show
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_day_picker, container)
        mDayPicker = view.findViewById(R.id.day_picker)
        mDayPicker?.setMaxDay(28)

        mTitle = view.findViewById(R.id.title)

        view.findViewById<View>(R.id.close).setOnClickListener { dismiss() }
        view.findViewById<View>(R.id.complete_sel).setOnClickListener {
            mListener?.onDaySelect(mDayPicker!!.selectedDay)
            dismiss()
        }

        if (mSelectedDay > 0) {
            setSelectedDate()
        }

        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(activity!!, R.style.DatePickerBottomDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // 设置Content前设定

        dialog.setContentView(R.layout.dialog_day_picker)
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

    fun setSelectedDay(day: Int) {
        mSelectedDay = day
        setSelectedDate()
    }

    fun setTitle(title: String) {
        mTitle?.text = title
    }

    private fun setSelectedDate() {
        mDayPicker?.setSelectedDay(mSelectedDay, false)
    }

    interface OnDaySelectedListener {
        fun onDaySelect(day: Int)
    }

    companion object {

        const val TAG = "DayPickerDialog"
    }


}