package com.kseniyaa.loftmoney;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemViewHolder> {

    private List<Item> items = new ArrayList<>();
    private ItemsAdapterListener listener = null;

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public void addItem(Item item) {
        this.items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    public void setListener(ItemsAdapterListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = items.get(position);
        holder.bind(item, listener, position, selections.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private SparseBooleanArray selections = new SparseBooleanArray();

    public void toggleItem(int position) {
        if (selections.get(position, false)) {
            selections.put(position, false);
        } else {
            selections.put(position, true);
        }
        notifyItemChanged(position);
    }

    public void clearSelections() {
        selections.clear();
        notifyDataSetChanged();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> selected = new ArrayList<>();
        for (int i = 0; i < getItemCount(); i++) {
            if (selections.get(i)) {
                selected.add(i);
            }
        }
        return selected;
    }

    public void removeItem(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView price;

        ItemViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_name);
            price = itemView.findViewById(R.id.tv_price);

        }

        void bind(final Item item, final ItemsAdapterListener listener, final int position, final boolean selected) {
            name.setText(item.getName());
            price.setText(Integer.toString(item.getPrice()));

            if (item.getType().equals(Item.Types.income.toString())) {
                int color = itemView.getResources().getColor(R.color.item_text_price_exp_color);
                price.setTextColor(color);
            }

            itemView.setSelected(selected);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.OnItemClick(item, position);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (listener != null) {
                        listener.OnItemLongClick(item, position);
                    }
                    return true;
                }
            });
        }
    }
}

