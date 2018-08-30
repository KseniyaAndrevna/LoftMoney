package com.kseniyaa.loftmoney;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ItemsFragment extends Fragment {

    private static final String KEY_TYPE = "type";
    public static final int TYPE_EXPENSES = 1;
    public static final int TYPE_INCOMES = 2;
    public static final int TYPE_UNKNOWN = -1;

    public static ItemsFragment newInstance(int type) {
        ItemsFragment fragment = new ItemsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ItemsFragment.KEY_TYPE, type);
        fragment.setArguments(bundle);

        return fragment;
    }

    private RecyclerView recycler;
    private ItemsAdapter adapter;
    private List<Item> items = new ArrayList<>();
    private int type;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            type = args.getInt(KEY_TYPE, TYPE_UNKNOWN);

            if (type == TYPE_UNKNOWN) {
                throw new IllegalStateException("Unknown type");
            }
        } else {
            throw new IllegalStateException("No fragment type");
        }

        adapter = new ItemsAdapter();
        addItem();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_items, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recycler = view.findViewById(R.id.recycler);

        ItemsAdapter adapter = new ItemsAdapter();
        adapter.setItems(items);

        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
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
