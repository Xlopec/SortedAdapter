package com.ua.oliynick.max.test;

import com.ua.oliynick.max.adapter.SortedAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

/**
 * Created by max on 02.12.17.
 */

public final class PostsAdapter extends SortedAdapter<Post> {

    private static final Comparator<Post> ASCENDING_CMP = new Comparator<Post>() {
        @Override
        public int compare(Post o1, Post o2) {
            return o1.getTimestamp().compareTo(o2.getTimestamp());
        }
    };

    private static final Comparator<Post> DESCENDING_CMP = new Comparator<Post>() {
        @Override
        public int compare(Post o1, Post o2) {
            return o2.getTimestamp().compareTo(o1.getTimestamp());
        }
    };

    private boolean isAscending = false;

    public PostsAdapter() {
        super(DESCENDING_CMP, new PostViewHolderAdapter(), new HeaderViewHolderAdapter());
    }

    @Override
    protected int getAdapterOffset(int dataSetPosition, @NotNull Post item) {
        return isAscending() ? 0 : 1;
    }

    @Override
    protected int getDataOffset(int position) {
        return isAscending() ? 0 : -1;
    }

    @Override
    public int getItemCount() {
        return getData().size() + 1;
    }

    public void toggleComparator() {
        isAscending = !isAscending;

        if (isAscending()) {
            setComparator(DESCENDING_CMP);
        } else {
            setComparator(ASCENDING_CMP);
        }
    }

    public boolean isAscending() {
        return isAscending;
    }

}
