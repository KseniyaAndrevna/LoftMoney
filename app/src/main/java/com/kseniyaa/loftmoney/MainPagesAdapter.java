package com.kseniyaa.loftmoney;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MainPagesAdapter extends FragmentPagerAdapter {

    private static final int PAGE_EXPENSES = 0;
    private static final int PAGE_INCOMES = 1;
    private static final int PAGE_BALANCE = 2;

    private static final int PAGES_COUNT = 3;

    private String[] pagesTitles;

    MainPagesAdapter(FragmentManager fm, Context context) {
        super(fm);
        pagesTitles = context.getResources().getStringArray(R.array.main_tabs);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case PAGE_EXPENSES:
                return ItemsFragment.newInstance(ItemsFragment.TYPE_EXPENSES);

            case PAGE_INCOMES:
                return ItemsFragment.newInstance(ItemsFragment.TYPE_INCOMES);

            case PAGE_BALANCE:
                return new BalanceFragment();

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
        return PAGES_COUNT;
    }
}
