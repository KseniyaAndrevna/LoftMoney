package com.kseniyaa.loftmoney;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddActivity extends AppCompatActivity {

    public static final String KEY_TYPE = "type";

    private EditText nameInput;
    private EditText priceInput;
    private Button addBtn;
    private Api api;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        nameInput = findViewById(R.id.et_name);
        priceInput = findViewById(R.id.et_price);
        addBtn = findViewById(R.id.btn_add);
        api = ((App) getApplication()).getApi();

        final String type = getIntent().getExtras().getString(KEY_TYPE);

        nameInput.addTextChangedListener(watcher);
        priceInput.addTextChangedListener(watcher);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameInput.getText().toString();
                String price = priceInput.getText().toString();

                Item item = new Item(name,Integer.parseInt(price),type);
                createItems(item);
            }
        });
    }

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            addBtn.setEnabled(!(TextUtils.isEmpty(nameInput.getText()) || TextUtils.isEmpty(priceInput.getText())));
        }
    };

    public void createItems(Item item) {
        Call<Item> call = api.createItem(item, Utils.getTokenValue(this));
        call.enqueue(new Callback<Item>() {
            @Override
            public void onResponse(Call<Item> call, Response<Item> response) {
                finish();
            }
            @Override
            public void onFailure(Call<Item> call, Throwable t) {
                System.out.println(t);
            }
        });
    }
}
