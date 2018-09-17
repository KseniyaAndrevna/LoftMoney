package com.kseniyaa.loftmoney;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import java.util.LinkedHashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiagramView extends View {

    public int income;
    private int expense;
    private Paint expensePaint = new Paint();
    private Paint incomePaint = new Paint();
    private SharedPreferences sharedPreferences;
    private String auth_token;
    private Api api;


    public DiagramView(Context context) {
        this(context, null);
    }

    public DiagramView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DiagramView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        incomePaint.setColor(ContextCompat.getColor(context, R.color.income_color));
        expensePaint.setColor(ContextCompat.getColor(context, R.color.expense_color));

        //todo перепутаны цвета в покраске цифр на дох и расх!!
        //todo balance style
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        System.out.println("Begin canvas");
        getBalance();


        if (expense + income == 0) {
            return;
        }

        float expenseAngle = 360.f * expense / (expense + income);
        float incomeAngle = 360.f * income / (expense + income);

        int space = 15;
        int size = Math.min(getWidth(), getHeight()) - space * 2;
        int xMargin = (getWidth() - size) / 2;
        int yMargin = (getHeight() - size) / 2;

        canvas.drawArc(
                xMargin - space, yMargin,
                getWidth() - xMargin - space,
                getHeight() - yMargin,
                180 - expenseAngle / 2, expenseAngle,
                true, expensePaint
        );

        canvas.drawArc(
                xMargin + space, yMargin,
                getWidth() - xMargin + space,
                getHeight() - yMargin,
                360 - incomeAngle / 2, incomeAngle,
                true, incomePaint
        );
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


