package com.bk.datepicker.date;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.bk.datepicker.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by CXJ
 * Created date 2019/6/12/012
 */
public class YearMonthPicker extends LinearLayout implements YearPicker.OnYearSelectedListener, MonthPicker.OnMonthSelectedListener {


    private YearPicker mYearPicker;
    private MonthPicker mMonthPicker;

    private OnMonthSelectedListener mListener;


    public YearMonthPicker(Context context) {
        this(context, null);
    }

    public YearMonthPicker(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public YearMonthPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_month, this);
        initChild();
        mYearPicker.setBackgroundDrawable(getBackground());
        mMonthPicker.setBackgroundDrawable(getBackground());
    }

    private void initChild() {
        mYearPicker = findViewById(R.id.yearPicker_layout_date);
        mYearPicker.setOnYearSelectedListener(this);
        mMonthPicker = findViewById(R.id.monthPicker_layout_date);
        mMonthPicker.setOnMonthSelectedListener(this);
    }


    /**
     * Sets on month selected listener.
     *
     * @param listener the on month selected listener
     */
    public void setOnMonthSelectedListener(OnMonthSelectedListener listener) {
        mListener = listener;
    }

    @Override
    public void onYearSelected(int year) {
        mMonthPicker.setYear(year);
        onMonthSelected();
    }

    @Override
    public void onMonthSelected(int month) {
        onMonthSelected();
    }


    private void onMonthSelected() {
        if (mListener != null) {
            mListener.onMonthSelected(getYear(), getMonth());
        }
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
     * The interface On date selected listener.
     */
    public interface OnMonthSelectedListener {
        /**
         * On date selected.
         *
         * @param year  the year
         * @param month the month
         */
        void onMonthSelected(int year, int month);
    }


    /**
     * Sets date.
     *
     * @param year  the year
     * @param month the month
     */
    public void setYearMonth(int year, int month) {
        setYearMonth(year, month, true);
    }

    /**
     * Sets date.
     *
     * @param year         the year
     * @param month        the month
     * @param smoothScroll the smooth scroll
     */
    public void setYearMonth(int year, int month, boolean smoothScroll) {
        mYearPicker.setSelectedYear(year, smoothScroll);
        mMonthPicker.setSelectedMonth(month, smoothScroll);
    }

    /**
     * Gets date.
     *
     * @return the date
     */
    public String getYearMonth() {
        DateFormat format = new SimpleDateFormat("yyyy-MM", Locale.CHINA);
        int year, month;
        year = getYear();
        month = getMonth();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1);
        return format.format(calendar.getTime());
    }

}
