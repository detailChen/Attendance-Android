package com.bk.datepicker.date;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.bk.datepicker.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 日期选择器
 * Created by ycuwq on 2018/1/1.
 */
@SuppressWarnings("unused")
public class DatePicker extends LinearLayout implements YearPicker.OnYearSelectedListener,
        MonthPicker.OnMonthSelectedListener, DayPicker.OnDaySelectedListener {

    private YearPicker mYearPicker;
    private MonthPicker mMonthPicker;
    private DayPicker mDayPicker;

    private Long mMaxDate;
    private Long mMinDate;
    private OnDateSelectedListener mOnDateSelectedListener;

    /**
     * Instantiates a new Date picker.
     *
     * @param context the context
     */
    public DatePicker(Context context) {
        this(context, null);
    }

    /**
     * Instantiates a new Date picker.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public DatePicker(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Instantiates a new Date picker.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     */
    public DatePicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_date, this);
        initChild();
        mYearPicker.setBackgroundDrawable(getBackground());
        mMonthPicker.setBackgroundDrawable(getBackground());
        mDayPicker.setBackgroundDrawable(getBackground());
    }



    private void initChild() {
        mYearPicker = findViewById(R.id.yearPicker_layout_date);
        mYearPicker.setOnYearSelectedListener(this);
        mMonthPicker = findViewById(R.id.monthPicker_layout_date);
        mMonthPicker.setOnMonthSelectedListener(this);
        mDayPicker = findViewById(R.id.dayPicker_layout_date);
        mDayPicker.setOnDaySelectedListener(this);
    }

    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
        if (mYearPicker != null && mMonthPicker != null && mDayPicker != null) {
            mYearPicker.setBackgroundColor(color);
            mMonthPicker.setBackgroundColor(color);
            mDayPicker.setBackgroundColor(color);
        }
    }

    @Override
    public void setBackgroundResource(int resid) {
        super.setBackgroundResource(resid);
        if (mYearPicker != null && mMonthPicker != null && mDayPicker != null) {
            mYearPicker.setBackgroundResource(resid);
            mMonthPicker.setBackgroundResource(resid);
            mDayPicker.setBackgroundResource(resid);
        }
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        super.setBackgroundDrawable(background);
        if (mYearPicker != null && mMonthPicker != null && mDayPicker != null) {
            mYearPicker.setBackgroundDrawable(background);
            mMonthPicker.setBackgroundDrawable(background);
            mDayPicker.setBackgroundDrawable(background);
        }
    }

    private void onDateSelected() {
        if (mOnDateSelectedListener != null) {
            mOnDateSelectedListener.onDateSelected(getYear(), getMonth(), getDay());
        }
    }


    @Override
    public void onMonthSelected(int month) {
        mDayPicker.setMonth(getYear(), month);
        onDateSelected();
    }

    @Override
    public void onDaySelected(int day) {
        onDateSelected();
    }


    @Override
    public void onYearSelected(int year) {
        int month = getMonth();
        mMonthPicker.setYear(year);
        mDayPicker.setMonth(year, month);
        onDateSelected();
    }

    /**
     * Sets date.
     *
     * @param year  the year
     * @param month the month
     * @param day   the day
     */
    public void setDate(int year, int month, int day) {
        setDate(year, month, day, true);
    }

    /**
     * Sets date.
     *
     * @param year         the year
     * @param month        the month
     * @param day          the day
     * @param smoothScroll the smooth scroll
     */
    public void setDate(int year, int month, int day, boolean smoothScroll) {
        mYearPicker.setSelectedYear(year, smoothScroll);
        mMonthPicker.setSelectedMonth(month, smoothScroll);
        mDayPicker.setSelectedDay(day, smoothScroll);
    }

    public void setMaxDate(long date) {
        setCyclic(false);
        mMaxDate = date;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        mYearPicker.setEndYear(calendar.get(Calendar.YEAR));
        mMonthPicker.setMaxDate(date);
        mDayPicker.setMaxDate(date);
        mMonthPicker.setYear(mYearPicker.getSelectedYear());
        mDayPicker.setMonth(mYearPicker.getSelectedYear(), mMonthPicker.getSelectedMonth());
    }

    public void setMinDate(long date) {
        setCyclic(false);
        mMinDate = date;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        mYearPicker.setStartYear(calendar.get(Calendar.YEAR));
        mMonthPicker.setMinDate(date);
        mDayPicker.setMinDate(date);
        mMonthPicker.setYear(mYearPicker.getSelectedYear());
        mDayPicker.setMonth(mYearPicker.getSelectedYear(), mMonthPicker.getSelectedMonth());
    }

    /**
     * Gets date.
     *
     * @return the date
     */
    public String getDate() {
        DateFormat format = SimpleDateFormat.getDateInstance();
        return getDate(format);
    }

    /**
     * Gets date.
     *
     * @param dateFormat the date format
     * @return the date
     */
    public String getDate(@NonNull DateFormat dateFormat) {
        int year, month, day;
        year = getYear();
        month = getMonth();
        day = getDay();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);

        return dateFormat.format(calendar.getTime());
    }

    /**
     * Gets year.
     *
     * @return the year
     */
    public int getYear() {
        return mYearPicker.getSelectedYear();
    }

    /**
     * Gets month.
     *
     * @return the month
     */
    public int getMonth() {
        return mMonthPicker.getSelectedMonth();
    }

    /**
     * Gets day.
     *
     * @return the day
     */
    public int getDay() {
        return mDayPicker.getSelectedDay();
    }

    /**
     * Gets year picker.
     *
     * @return the year picker
     */
    public YearPicker getYearPicker() {
        return mYearPicker;
    }

    /**
     * Gets month picker.
     *
     * @return the month picker
     */
    public MonthPicker getMonthPicker() {
        return mMonthPicker;
    }

    /**
     * Gets day picker.
     *
     * @return the day picker
     */
    public DayPicker getDayPicker() {
        return mDayPicker;
    }


    /**
     * 设置是否循环滚动。
     * set wheel cyclic
     *
     * @param cyclic 上下边界是否相邻
     */
    public void setCyclic(boolean cyclic) {
        mDayPicker.setCyclic(cyclic);
        mMonthPicker.setCyclic(cyclic);
        mYearPicker.setCyclic(cyclic);
    }

    /**
     * Sets on date selected listener.
     *
     * @param onDateSelectedListener the on date selected listener
     */
    public void setOnDateSelectedListener(OnDateSelectedListener onDateSelectedListener) {
        mOnDateSelectedListener = onDateSelectedListener;
    }

    /**
     * The interface On date selected listener.
     */
    public interface OnDateSelectedListener {
        /**
         * On date selected.
         *
         * @param year  the year
         * @param month the month
         * @param day   the day
         */
        void onDateSelected(int year, int month, int day);
    }
}
