package com.ua.oliynick.max.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * <p>
 * Base class which responsible for a certain view
 * type creation, binding and etc. in {@link SortedAdapter}
 * </p>
 * Created by max on 02.12.17.
 */

public abstract class ViewHolderAdapter<T extends HasKey> {

    protected abstract boolean canHandle(int position, @NotNull SortedAdapter<T> adapter);

    protected abstract void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, @NotNull List<Object> payload, int position, @NotNull SortedAdapter<T> adapter);

    protected abstract void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position, @NotNull SortedAdapter<T> adapter);

    protected void onViewAttachedToWindow(@NotNull RecyclerView.ViewHolder holder, @NotNull SortedAdapter<T> adapter) {
    }

    protected void onViewDetachedFromWindow(@NotNull RecyclerView.ViewHolder holder, @NotNull SortedAdapter<T> adapter) {
    }

    protected boolean onFailedToRecycleView(RecyclerView.ViewHolder holder, @NotNull SortedAdapter<T> adapter) {
        return false;
    }

    protected void onViewRecycled(RecyclerView.ViewHolder holder, @NotNull SortedAdapter<T> adapter) {
    }

    @NotNull
    protected abstract RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent);

}
