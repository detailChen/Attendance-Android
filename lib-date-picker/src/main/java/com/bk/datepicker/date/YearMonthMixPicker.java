package com.bk.datepicker.date;

import android.content.Context;
import android.util.AttributeSet;

import com.bk.datepicker.WheelPicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * Created by Chen xuJie on 2019/6/15.
 */
public class YearMonthMixPicker extends WheelPicker<YearMonthMixPicker.YearMonth> {


    private List<YearMonth> mData = new ArrayList<>();

    private YearMonth mSelectedYearMonth;
    private OnYearMonthSelectedListener mOnYearMonthSelectedListener;


    public void setOnYearSelectedListener(OnYearMonthSelectedListener onYearMonthSelectedListener) {
        mOnYearMonthSelectedListener = onYearMonthSelectedListener;
    }

    public interface OnYearMonthSelectedListener {
        void onYearMonthSelected(YearMonth year);
    }

    public YearMonthMixPicker(Context context) {
        super(context);
    }

    public YearMonthMixPicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public YearMonthMixPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setItemMaximumWidthText("0000.00");
        setOnWheelChangeListener(new WheelPicker.OnWheelChangeListener<YearMonth>() {
            @Override
            public void onWheelSelected(YearMonth item, int position) {
                mSelectedYearMonth = item;
                if (mOnYearMonthSelectedListener != null) {
                    mOnYearMonthSelectedListener.onYearMonthSelected(item);
                }
            }
        });
    }

    public void setMinMonth(int year, int month) {
        Calendar c = Calendar.getInstance();
        int cy = c.get(Calendar.YEAR);
        int cm = c.get(Calendar.MONTH);

        int minM = Math.max(0, Math.min(month, cy <= year ? cm : 11));
        int minY = Math.min(year, cy);

        mData.clear();
        mData.add(new YearMonth("合计", -1, -1));
        mData.add(new YearMonth(c.get(Calendar.YEAR) + "年", -1, c.get(Calendar.YEAR)));

        while (true) {
            int y = c.get(Calendar.YEAR);
            int m = c.get(Calendar.MONTH);

            mData.add(new YearMonth((y) + "-" + (m < 9 ? "0" : "") + (m + 1), m, y));

            if (y < minY || (y == minY && m <= minM)) {
                break;
            }

            if (m == 0) {
                mData.add(new YearMonth((y - 1) + "年", -1, y - 1));
            }

            c.add(Calendar.MONTH, -1);
        }
        Collections.reverse(mData);
        setDataList(mData);
    }


    public void setSelectedYearMonth(int year, int month, boolean smoothScroll) {
        int selPos = -1;
        if (year == -1 && month == -1) {
            selPos = mData.size() - 1;
        } else {
            for (int i = 0; i < mData.size(); i++) {
                YearMonth yearMonth = mData.get(i);
                int y = yearMonth.year;
                int m = yearMonth.month;
                if (year == y && month == m) {
                    selPos = i;
                    break;
                }
            }
        }
        setCurrentPosition(selPos, smoothScroll);
    }

    public int[] getSelectedYearMonth() {
        YearMonth yearMonth = mData.get(getCurrentPosition());
        return new int[]{yearMonth.year, yearMonth.month};
    }


    public static class YearMonth {
        public final int year;
        public final int month; // 从0开始！
        public final String title;

        public YearMonth(String title, int month, int year) {
            this.title = title;
            this.month = month;
            this.year = year;
        }

        @Override
        public String toString() {
            return title;
        }
    }


}
