package com.kseniyaa.loftmoney;


import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedHashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BalanceFragment extends Fragment {

    private TextView tv_income;
    private TextView tv_balance;
    private TextView tv_expense;
    private SharedPreferences sharedPreferences;
    private String auth_token;
    private Api api;
    public int income;
    private int expense;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_balance, container, false);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tv_income = view.findViewById(R.id.tv_income_value);
        tv_balance = view.findViewById(R.id.tv_balance_value);
        tv_expense = view.findViewById(R.id.tv_expense_value);

        getBalance();
        tv_expense.setText(String.valueOf(expense));
        tv_balance.setText(String.valueOf(income - expense));
        tv_income.setText(String.valueOf(income));
    }

    public void getBalance() {
        api = AuthActivity.api;
        auth_token = Utils.getTokenValue(sharedPreferences, getContext());
        Call<LinkedHashMap<String, String>> call = api.getBalance(auth_token);
        call.enqueue(new Callback<LinkedHashMap<String, String>>() {
            @Override
            public void onResponse(Call<LinkedHashMap<String, String>> call, Response<LinkedHashMap<String, String>> response) {
                assert response.body() != null;
                LinkedHashMap balanceData = response.body();
                assert balanceData != null;
                String stringExpense = (String) balanceData.get("total_expenses");
                String stringIncome = (String) balanceData.get("total_income");

                income = Integer.parseInt(stringIncome);
                expense = Integer.parseInt(stringExpense);
            }

            @Override
            public void onFailure(Call<LinkedHashMap<String, String>> call, Throwable t) {
                System.out.println(t);
            }
        });
    }
}
