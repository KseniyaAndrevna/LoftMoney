package com.kseniyaa.loftmoney;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class ItemsFragment extends Fragment {

    private static final String KEY_TYPE = "type";
    public static final int ITEM_REQUEST_CODE = 100;

    public static ItemsFragment newInstance(String type) {
        ItemsFragment fragment = new ItemsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ItemsFragment.KEY_TYPE, type);
        fragment.setArguments(bundle);

        return fragment;
    }

    private RecyclerView recycler;
    private FloatingActionButton fab;

    private ItemsAdapter adapter;
    private String type;
    private Api api;
    private SwipeRefreshLayout refresh;
    private static ActionMode actionMode;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        assert args != null;
        type = args.getString(KEY_TYPE);

        api = ((App) getActivity().getApplication()).getApi();

        adapter = new ItemsAdapter();
        adapter.setListener(new AdapterListener());

        loadItems();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {

        return inflater.inflate(R.layout.fragment_items, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        MainActivity mainActivity = (MainActivity) this.getActivity();
        fab = mainActivity.findViewById(R.id.fab);
        recycler = view.findViewById(R.id.recycler);
        refresh = view.findViewById(R.id.refresh);
        refresh.setColorSchemeColors(
                ContextCompat.getColor(requireContext(), R.color.refresh_first),
                ContextCompat.getColor(requireContext(), R.color.refresh_second),
                ContextCompat.getColor(requireContext(), R.color.refresh_third),
                ContextCompat.getColor(requireContext(), R.color.refresh_fourth));
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadItems();
            }
        });

        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy < 0 && !fab.isShown())
                    fab.show();
                else if (dy > 0 && fab.isShown())
                    fab.hide();
            }
        });
    }

    public void loadItems() {
        Call<ItemsData> call = api.getItems(type);

        call.enqueue(new Callback<ItemsData>() {

            @Override
            public void onResponse(Call<ItemsData> call, Response<ItemsData> response) {
                refresh.setRefreshing(false);
                ItemsData data = response.body();
                List<Item> items = data.getData();
                adapter.setItems(items);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ItemsData> call, Throwable t) {
                System.out.println(t);
                refresh.setRefreshing(false);
            }
        });
    }

    public static void closeActionMode() {
        if (actionMode != null) {
            actionMode.finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ITEM_REQUEST_CODE & resultCode == RESULT_OK) {
            Item item = data.getParcelableExtra(AddActivity.KEY_ITEM);
            if (item.getType().equals(type)) {
                adapter.addItem(item);
            }
        }
    }

    private void removeSelectedItems() {
        List<Integer> selected = adapter.getSelectedItems();
        for (int i = 0; i < selected.size(); i++) {
            if (i == 0) {
                adapter.removeItem(selected.get(i));
            } else {
                adapter.removeItem(selected.get(i) - 1);
            }
        }
        actionMode.finish();
    }

    class AdapterListener implements ItemsAdapterListener {

        @Override
        public void OnItemClick(Item item, int position) {
            if (actionMode == null) {
                return;
            }
            toggleItem(position);
        }

        @Override
        public void OnItemLongClick(Item item, int position) {
            if (actionMode != null) {
                return;
            }
            ((AppCompatActivity) getActivity()).startSupportActionMode(new ActionModeCallback());
            fab.hide();
            toggleItem(position);
        }

        private void toggleItem(int position) {
            adapter.toggleItem(position);
        }

    }

    class ActionModeCallback implements ActionMode.Callback {

        private TabLayout tabLayout = MainActivity.getTabLayout();

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            actionMode = mode;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = new MenuInflater(requireContext());
            inflater.inflate(R.menu.menu_action_mode, menu);
            tabLayout.setBackgroundColor(getResources().getColor(R.color.action_mode_back));
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.menu_item_delete) {
                showConfirmationDialog();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            adapter.clearSelections();
            fab.show();
            actionMode = null;
            tabLayout.setBackgroundColor(getResources().getColor(R.color.tab_color_primary));
        }

        private void showConfirmationDialog() {
            ConfirmDeleteDialog dialog = new ConfirmDeleteDialog();
            dialog.show(getFragmentManager(), null);

            dialog.setListener(new ConfirmDeleteDialog.Listener() {
                @Override
                public void onDeleteConfirmed() {
                    removeSelectedItems();
                }
            });
        }
    }
}