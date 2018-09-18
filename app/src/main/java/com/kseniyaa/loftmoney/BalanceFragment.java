package com.kseniyaa.loftmoney;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.LinkedHashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BalanceFragment extends Fragment {

    private Api api;
    private int income;
    private int expense;
    private TextView tv_income;
    private TextView tv_expense;
    private TextView tv_balance;
    private DiagramView diagramView;

    public static BalanceFragment newInstance() {
        Bundle bundle = new Bundle();
        BalanceFragment fragment = new BalanceFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = ((App) getActivity().getApplication()).getApi();
    }

    @Override
    public void onResume() {
        super.onResume();
        getBalance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_balance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv_income = view.findViewById(R.id.tv_income_value);
        tv_expense = view.findViewById(R.id.tv_expense_value);
        tv_balance = view.findViewById(R.id.tv_balance_value);
        diagramView = view.findViewById(R.id.diagram);
    }

    public void getBalance() {
        Call<LinkedHashMap<String, String>> call = api.getBalance(Utils.getTokenValue( getContext()));
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

                tv_income.setText(valueFormat(income) + getString(R.string.rubl));
                tv_expense.setText(valueFormat(expense) + getString(R.string.rubl));
                tv_balance.setText(valueFormat(income - expense) + getString(R.string.rubl));

                diagramView.setValues(income, expense);
            }

            @Override
            public void onFailure(Call<LinkedHashMap<String, String>> call, Throwable t) {
                System.out.println(t);
            }
        });
    }

    private String valueFormat(int value) {
        String newValue;
        NumberFormat format = NumberFormat.getNumberInstance();
        newValue = format.format(value);
        return newValue;
    }
}
