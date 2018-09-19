package com.kseniyaa.loftmoney;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MainPagesAdapter extends FragmentPagerAdapter {

    public static final int PAGE_EXPENSES = 0;
    public static final int PAGE_INCOMES = 1;
    public static final int PAGE_BALANCE = 2;

    private String[] pagesTitles;

    MainPagesAdapter(FragmentManager fm, Context context) {
        super(fm);
        pagesTitles = context.getResources().getStringArray(R.array.main_tabs);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case PAGE_EXPENSES:
                System.out.println(position);
                return ItemsFragment.newInstance(Item.Types.expense.toString());

            case PAGE_INCOMES:
                return ItemsFragment.newInstance(Item.Types.income.toString());

            case PAGE_BALANCE:
                return BalanceFragment.newInstance();

            default:
                return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return pagesTitles[position];
    }

    @Override
    public int getCount() {
        return pagesTitles.length;
    }
}
