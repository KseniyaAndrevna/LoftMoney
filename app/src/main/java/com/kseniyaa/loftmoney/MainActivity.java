package com.kseniyaa.loftmoney;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.util.LinkedHashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private MainPagesAdapter adapter;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private ActionMode actionMode;

    private SharedPreferences sharedPreferences;
    private String auth_token;
    private Api api;
    public int income;
    public int expense;
    public int balance;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        toolbar = findViewById(R.id.toolbar);
        fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int page = viewPager.getCurrentItem();

                String type = null;

                switch (page) {
                    case MainPagesAdapter.PAGE_INCOMES:
                        type = Item.Types.income.toString();
                        break;
                    case MainPagesAdapter.PAGE_EXPENSES:
                        type = Item.Types.expense.toString();
                        break;
                }

                if (type != null) {
                    Intent intent = new Intent(MainActivity.this, AddActivity.class);
                    intent.putExtra(AddActivity.KEY_TYPE, type);
                    startActivityForResult(intent, ItemsFragment.ITEM_REQUEST_CODE);
                }
            }
        });
        adapter = new MainPagesAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new PageListener());

        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        tabLayout.setupWithViewPager(viewPager);
    }

    class PageListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (actionMode != null) {
                actionMode.finish();
            }
        }

        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case MainPagesAdapter.PAGE_INCOMES:
                case MainPagesAdapter.PAGE_EXPENSES:
                    fab.show();
                    break;
                case MainPagesAdapter.PAGE_BALANCE:
                    fab.hide();
                    getBalance();
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(data);

        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onSupportActionModeFinished(@NonNull ActionMode mode) {
        super.onSupportActionModeFinished(mode);
        tabLayout.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.tab_color_primary));
        fab.show();
    }

    @Override
    public void onSupportActionModeStarted(@NonNull ActionMode mode) {
        super.onSupportActionModeStarted(mode);
        tabLayout.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.action_mode_back));
        fab.hide();
        actionMode = mode;
    }

    public void getBalance() {
        api = AuthActivity.api;
        auth_token = Utils.getTokenValue(sharedPreferences, this);
        Call<LinkedHashMap<String, String>> call = api.getBalance(auth_token);
        call.enqueue(new Callback<LinkedHashMap<String, String>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<LinkedHashMap<String, String>> call, Response<LinkedHashMap<String, String>> response) {
                assert response.body() != null;
                LinkedHashMap balanceData = response.body();
                assert balanceData != null;
                String stringExpense = (String) balanceData.get("total_expenses");
                String stringIncome = (String) balanceData.get("total_income");

                income = Integer.parseInt(stringIncome);
                expense = Integer.parseInt(stringExpense);

                TextView tv_income = findViewById(R.id.tv_income_value);
                TextView tv_expense = findViewById(R.id.tv_expense_value);
                TextView tv_balance = findViewById(R.id.tv_balance_value);

                tv_income.setText(String.valueOf(income) + getString(R.string.rubl));
                tv_expense.setText(String.valueOf(expense) + getString(R.string.rubl));
                tv_balance.setText(String.valueOf(income - expense) + getString(R.string.rubl));
            }

            @Override
            public void onFailure(Call<LinkedHashMap<String, String>> call, Throwable t) {
                System.out.println(t);
            }
        });
    }
}
