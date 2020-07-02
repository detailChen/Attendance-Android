package com.attendance.bk.adapter;

import android.text.TextUtils;


import com.attendance.bk.R;
import com.attendance.bk.bean.BankInfo;
import com.attendance.bk.view.BkImageView;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Ren ZeQiang
 * @since 2018/9/17.
 */
public class BankListAdapter extends BaseMultiItemQuickAdapter<BankInfo, BaseViewHolder> {
    private static final int VIEW_TYPE_HEADER = 0x0010;
    //银行拼音首字母
    private List<String> mPinyinList = new ArrayList<>();
    //拼音 + 银行
    private List<BankInfo> resultList = new ArrayList<>();



    public BankListAdapter() {
        super(null);

        addItemType(BankInfo.TYPE_PINYIN, R.layout.item_charater);
        addItemType(BankInfo.TYPE_BANK, R.layout.item_bank);
    }

    @Override
    public int getItemCount() {
        return resultList.size() + getHeaderLayoutCount();
    }

    public void handleBank(List<BankInfo> bankInfoList) {
        mPinyinList.add("常用");
        resultList.add(new BankInfo("常用", BankInfo.TYPE_PINYIN));
        for (int i = 0; i < bankInfoList.size(); i++) {
            BankInfo bankInfo = bankInfoList.get(i);
            if (TextUtils.equals(bankInfo.isCommonUse(), "1")) {
                bankInfo.setType(BankInfo.TYPE_BANK);
                resultList.add(bankInfo);
            }
        }
        for (int i = 0; i < bankInfoList.size(); i++) {
            BankInfo bankInfo = bankInfoList.get(i);
            String firstChar = bankInfo.getFirstChar();
            if (!mPinyinList.contains(firstChar)) {
                mPinyinList.add(firstChar);
                resultList.add(new BankInfo(firstChar, BankInfo.TYPE_PINYIN));
            }
            bankInfo.setType(BankInfo.TYPE_BANK);
            resultList.add(bankInfo);

        }

        addData(resultList);

    }


    @Override
    protected void convert(BaseViewHolder helper, BankInfo item) {
        if (item.getType() == BankInfo.TYPE_PINYIN) {
            helper.setText(R.id.tv_character, item.getName());
        } else if (item.getType() == BankInfo.TYPE_BANK) {
            helper.setText(R.id.tv_bank_name, item.getName());
            BkImageView mBankIcon = helper.getView(R.id.iv_bank_icon);
            mBankIcon.setImageName(item.getIcon());
        }
    }



    public int getScrollPosition(String character) {
        if (mPinyinList.contains(character)) {
            for (int i = 0; i < resultList.size(); i++) {
                if (resultList.get(i).getName().contains(character)) {
                    return i + getHeaderLayoutCount();
                }
            }
        }
        return -1;  // -1表示不会滑动
    }

    private class ContactComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            int c1 = (o1.charAt(0) + "").toUpperCase().hashCode();
            int c2 = (o2.charAt(0) + "").toUpperCase().hashCode();

            boolean c1Flag = (c1 < "A".hashCode() || c1 > "Z".hashCode()); // 不是字母
            boolean c2Flag = (c2 < "A".hashCode() || c2 > "Z".hashCode()); // 不是字母

            if (c1Flag && !c2Flag) {
                return -1;
            } else if (!c1Flag && c2Flag) {
                return 1;
            }

            return c1 - c2;
        }
    }
}
