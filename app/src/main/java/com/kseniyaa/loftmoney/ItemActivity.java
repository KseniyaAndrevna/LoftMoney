package com.kseniyaa.loftmoney;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ItemActivity extends AppCompatActivity {

    List<Item> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        addItem();

        ItemsAdapter adapter = new ItemsAdapter();
        adapter.setItems(items);

        RecyclerView recycler = findViewById(R.id.recycler);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        recycler.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
    }

    public void addItem() {
        items.add(new Item("Молоко", "70"));
        items.add(new Item("Зубная щётка", "70"));
        items.add(new Item("Сковородка с антипригарным покрытием", "4500"));
        items.add(new Item("Стол кухонный", "2000"));
        items.add(new Item("Велосипед", "5000"));
        items.add(new Item("Кружка", "100"));
        items.add(new Item("Палатка", "3000"));
        items.add(new Item("Рюкзак", "2999"));
    }
}
