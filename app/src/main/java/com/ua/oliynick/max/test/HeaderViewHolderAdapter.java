package com.ua.oliynick.max.test;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ua.oliynick.max.adapter.R;
import com.ua.oliynick.max.adapter.SortedAdapter;
import com.ua.oliynick.max.adapter.ViewHolderAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import lombok.val;

/**
 * Created by max on 02.12.17.
 */

public final class HeaderViewHolderAdapter extends ViewHolderAdapter<Post> {

    @Override
    protected boolean canHandle(int position, @NotNull SortedAdapter<Post> adapter) {
        return ((PostsAdapter) adapter).isAscending() ? position == adapter.getItemCount() - 1 : position == 0;
    }

    @Override
    protected void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, @NotNull List<Object> payload, int position, @NotNull SortedAdapter<Post> adapter) {
        final TextView title = holder.itemView.findViewById(R.id.title);

        title.setText("I'm title");
    }

    @Override
    protected void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position, @NotNull SortedAdapter<Post> adapter) {

    }

    @NotNull
    @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent) {
        val inflater = LayoutInflater.from(parent.getContext());

        return new HeaderViewHolder(inflater.inflate(R.layout.title, parent, false));
    }
}
