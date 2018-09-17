package com.kseniyaa.loftmoney;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
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

public class ItemsFragment extends Fragment {

    private static final String KEY_TYPE = "type";
    public static final int ITEM_REQUEST_CODE = 100;
    private SharedPreferences sharedPreferences;
    private String auth_token;
    private RecyclerView recycler;
    private FloatingActionButton fab;
    private ItemsAdapter adapter;
    private String type;
    private Api api;
    private SwipeRefreshLayout refresh;
    public ActionMode actionMode;

    public static ItemsFragment newInstance(String type) {
        ItemsFragment fragment = new ItemsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ItemsFragment.KEY_TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        assert args != null;
        type = args.getString(KEY_TYPE);

        api = ((App) getActivity().getApplication()).getApi();

        adapter = new ItemsAdapter();
        adapter.setListener(new AdapterListener());

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

        loadItems();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadItems();
    }

    private void removeSelectedItems() {
        List<Integer> selected = adapter.getSelectedItems();
        for (int i = 0; i < selected.size(); i++) {
            removeItem(selected.get(i));
        }
        actionMode.finish();
    }

    class AdapterListener implements ItemsAdapterListener {

        @Override
        public void OnItemClick(Item item, int position) {
            if (actionMode == null) {
                return;
            }
            toggleItem(item.getId(), position);
            actionMode.setTitle(getString(R.string.action_mode_title) + adapter.getSelectedItems().size());
        }

        @Override
        public void OnItemLongClick(Item item, int position) {
            if (actionMode != null) {
                return;
            }
            ((AppCompatActivity) getActivity()).startSupportActionMode(new ActionModeCallback());
            toggleItem(item.getId(), position);
            actionMode.setTitle(getString(R.string.action_mode_title) + 1);
        }

        private void toggleItem(int id, int position) {
            adapter.toggleItem(id, position);
        }
    }

    class ActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            actionMode = mode;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = new MenuInflater(requireContext());
            inflater.inflate(R.menu.menu_action_mode, menu);
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
            actionMode = null;
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

    public void removeItem(final int id) {
        Call<Item> call = api.deleteItem(id, auth_token);
        call.enqueue(new Callback<Item>() {
            @Override
            public void onResponse(Call<Item> call, Response<Item> response) {
                loadItems();
            }

            @Override
            public void onFailure(Call<Item> call, Throwable t) {
            }
        });
    }

    public void loadItems() {
        auth_token = Utils.getTokenValue(sharedPreferences, getContext());
        Call<List<Item>> call = api.getItems(type, auth_token);

        call.enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                refresh.setRefreshing(false);
                List<Item> items = response.body();
                adapter.setItems(items);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                System.out.println(t);
                refresh.setRefreshing(false);
            }
        });
    }
}