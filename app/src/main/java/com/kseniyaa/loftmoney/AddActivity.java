package com.kseniyaa.loftmoney;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

public class AddActivity extends AppCompatActivity {

    private EditText nameInput;
    private EditText priceInput;
    private Button addBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        nameInput = findViewById(R.id.et_name);
        priceInput = findViewById(R.id.et_price);
        addBtn = findViewById(R.id.btn_add);

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

        nameInput.addTextChangedListener(watcher);
        priceInput.addTextChangedListener(watcher);

    }
}
