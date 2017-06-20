package com.github.chuross.rx.internal;


import android.support.v7.util.DiffUtil;

import java.util.List;

public class DiffCallback<T> extends DiffUtil.Callback {

    private List<T> oldItems;
    private List<T> newItems;

    public DiffCallback(List<T> oldItems, List<T> newItems) {
        this.oldItems = oldItems;
        this.newItems = newItems;
    }

    @Override
    public int getOldListSize() {
        return oldItems.size();
    }

    @Override
    public int getNewListSize() {
        return newItems.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldItems.get(oldItemPosition).equals(newItems.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return true;
    }
}
