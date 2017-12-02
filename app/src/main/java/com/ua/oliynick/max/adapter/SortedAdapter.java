package com.ua.oliynick.max.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.ua.oliynick.max.adapter.util.Precondition;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import lombok.experimental.var;
import lombok.val;

/**
 * <p>
 * A subclass of {@link RecyclerView.Adapter} whose data set is sorted
 * according to supplied {@link Comparator} in constructor.
 * This adapter eases using of multiple view types. For such
 * purposes you subclass {@link ViewHolderAdapter}
 * </p>
 * <p>
 * Insertion of items collection whose size is n runs O(log2(m) + n), where m
 * is size of underlying data set. This estimation doesn't take into count the operation
 * of possible array shifting or re-allocation for subclasses of {@link java.util.ArrayList}
 * and assumes underlying list implements {@link java.util.RandomAccess} interface.
 * That's why performance of this adapter operations heavily relies on the underlying list implementation.
 * To provide own list implementation you can override {@link #createList()}
 * </p>
 * <p>
 * It's highly recommended to use immutable data types for this adapter. If fields of used data are mutable
 * and used for comparing at the same time, then there is possible situation when after changing of
 * one of these fields adapter's sort order can be violated; new insertions and deletions will cause
 * runtime exception
 * </p>
 * Created by max on 7/19/17.
 */

public abstract class SortedAdapter<T extends HasKey> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<T> data;
    private final SparseArray<ViewHolderAdapter> viewHolders;
    private Comparator<? super T> comparator;

    protected SortedAdapter(@NotNull Comparator<? super T> comparator) {
        this.data = Precondition.isNotNull(createList());
        this.comparator = Precondition.isNotNull(comparator);
        this.viewHolders = new SparseArray<>(1);
    }

    protected SortedAdapter(@NotNull Comparator<? super T> comparator, @NotNull ViewHolderAdapter viewHolder) {
        Precondition.isNotNullAll(comparator, viewHolder);

        this.data = Precondition.isNotNull(createList());
        this.comparator = comparator;
        this.viewHolders = new SparseArray<>(1);

        addViewHolder(viewHolder);
    }

    @SafeVarargs
    protected SortedAdapter(@NotNull Comparator<? super T> comparator,
                            ViewHolderAdapter<? extends T>... viewHolders) {
        Precondition.isNotNullAll(comparator, viewHolders);

        this.data = Precondition.isNotNull(createList());
        this.comparator = comparator;
        this.viewHolders = new SparseArray<>(Math.max(1, viewHolders.length));

        for (val viewHolder : viewHolders) {
            addViewHolder(viewHolder);
        }
    }

    /**
     * Sets new comparator, according to which data set will
     * be sorted
     *
     * @param comparator comparator to apply, can't be null
     */
    public final void setComparator(@NotNull Comparator<T> comparator) {
        Precondition.isNotNull(comparator);

        this.comparator = comparator;

        Collections.sort(data, comparator);
        notifyItemRangeChanged(0, getItemCount());
    }

    /**
     * <p>
     * Removes items from data set.
     * </p>
     * <p>Note, that if module build config is
     * debug, then additional constraint checks are performed,
     * which can significantly decrease performance
     * </p>
     *
     * @param items items to remove, can't be null
     */
    public final void remove(@NotNull Collection<? extends T> items) {
        Precondition.isNotNull(items);

        if (items.isEmpty()) {
            return;
        }

        val data = this.data;

        if (BuildConfig.DEBUG) {
            // Does heavy constraint checks, don't use this
            // in production code
            SortedAdapter.ensureDataSetValid(data, comparator);
        }

        for (val item : items) {
            // log(N) complexity
            val i = Collections.binarySearch(data, item, comparator);
            var j = i < 0 ? -1 * i - 1 : i;

            for (; j > 0 && (j - 1 <= 0 || comparator.compare(item, data.get(j - 1)) == 0) && item.getViewId() != data.get(j).getViewId(); --j)
                ;

            var stopPosFound = false;

            for (; !stopPosFound && j < data.size(); ++j) {
                val it = data.get(j);

                stopPosFound = comparator.compare(it, item) != 0;

                if (it.getViewId() == item.getViewId()) {
                    removeItem(j);
                }
            }
        }
    }

    /**
     * Removes items from data set for keys
     *
     * @param keys item's keys to remove
     */
    public final void removeViaKeys(@NotNull Collection<? extends HasKey> keys) {
        Precondition.isNotNull(keys);

        if (keys.isEmpty()) {
            return;
        }
        // mid
        var n = data.size() + 1 >> 1;
        var found = false;

        for (val hasKey : keys) {
            val key = hasKey.getViewId();

            for (var i = 0; i < n && !found; ++i) {
                // check both at the start and at the end of the data set
                if (found = key == data.get(i).getViewId() // lo
                        // hi
                        || key == data.get(data.size() - i - 1).getViewId()) {
                    removeItem(i);
                }
            }

            if (found) {
                // recalculate mid
                n = data.size() + 1 >> 1;
                found = false;
            }
        }
    }

    /**
     * <p>
     * Inserts given item into underlying list or updates if item
     * with same view id is found. In the first case {@link #notifyItemInserted(int)}
     * will be called while in the second - {@link #notifyItemChanged(int)}.
     * </p>
     * <p>
     * Note, that if module build config is
     * debug, then additional constraint checks are performed,
     * which can significantly decrease performance
     * </p>
     *
     * @param item item to insert or update
     */
    public final void addOrUpdate(@NotNull T item) {
        doAddOrUpdate(Precondition.isNotNull(item, "Item to insert was null"));
    }

    /**
     * <p>
     * Inserts given items into underlying list or updates if item
     * with same view id is found. In the first case {@link #notifyItemInserted(int)}
     * will be called while in the second - {@link #notifyItemChanged(int)}.
     * </p>
     * <p>
     * Note, that if module build config is
     * debug, then additional constraint checks are performed,
     * which can significantly decrease performance
     * </p>
     *
     * @param items items to insert or update
     */
    public final void addOrUpdate(@NotNull Collection<? extends T> items) {
        Precondition.isNotNull(items);

        if (items.isEmpty()) {
            return;
        }

        if (BuildConfig.DEBUG) {
            // Does heavy constraint checks, don't use this
            // in production code
            SortedAdapter.ensureDataSetValid(data, comparator);
        }

        for (val item : items) {
            doAddOrUpdate(item);
        }
    }

    /**
     * @return underlying unmodifiable list
     */
    @NotNull
    public final List<T> getData() {
        return Collections.unmodifiableList(data);
    }

    /**
     * Returns data item on the given position
     *
     * @param position positions to get item from, no offsets are applied
     */
    public final T getItem(int position) {
        return data.get(position);
    }

    /**
     * Adds view holder adapter
     */
    public final void addViewHolder(@NotNull ViewHolderAdapter viewHolder) {
        viewHolders.append(viewHolders.size(), Precondition.isNotNull(viewHolder));
    }

    /**
     * @return copy of used view holder adapters
     */
    @NotNull
    public final SparseArray<ViewHolderAdapter> getViewHolders() {
        return viewHolders.clone();
    }

    /**
     * @return comparator used by this adapter instance
     */
    @NotNull
    public final Comparator<? super T> getComparator() {
        return comparator;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final int getItemViewType(int position) {
        val size = viewHolders.size();

        for (var i = 0; i < size; ++i) {
            val holder = viewHolders.valueAt(i);

            if (holder.canHandle(position, this)) {
                return viewHolders.keyAt(i);
            }
        }

        throw new IllegalStateException(
                String.format("Couldn't find acceptable view holder for position %d", position));
    }

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return getAdapterOrThrow(viewType).onCreateViewHolder(parent);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void onBindViewHolder(RecyclerView.ViewHolder h, int position, List<Object> payloads) {
        getAdapterOrThrow(getItemViewType(position)).onBindViewHolder(h, payloads, position + getDataOffset(position), this);
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
        getAdapterOrThrow(getItemViewType(position)).onBindViewHolder(h, position + getDataOffset(position), this);
    }

    @Override
    public final void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        getAdapterOrThrow(holder.getItemViewType()).onViewAttachedToWindow(holder, this);
    }

    @Override
    public final void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        getAdapterOrThrow(holder.getItemViewType()).onViewDetachedFromWindow(holder, this);
    }

    @Override
    public boolean onFailedToRecycleView(RecyclerView.ViewHolder holder) {
        return getAdapterOrThrow(holder.getItemViewType()).onFailedToRecycleView(holder, this);
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        getAdapterOrThrow(holder.getItemViewType()).onViewRecycled(holder, this);
    }

    /**
     * This method returns offset for adapter to correctly notify
     * recycler view about data changes
     *
     * @param dataSetPosition position in the underlying data list
     * @param item            item which triggered event
     * @return calculated offset
     */
    protected abstract int getAdapterOffset(int dataSetPosition, @NotNull T item);

    /**
     * In a contrary to {@link #getAdapterOffset(int, HasKey)} this method returns
     * offset to convert adapter's position into calculated position to be able
     * to access underlying data
     *
     * @param position position in adapter
     * @return calculated offset
     */
    protected abstract int getDataOffset(int position);

    /**
     * Override this method to provide own {@link List} implementation.
     * Performance of this adapter is highly depends on used list implementation
     *
     * @return list implementation to be used by the adapter
     */
    @NotNull
    protected List<T> createList() {
        return new ArrayList<>();
    }

    private void doAddOrUpdate(@NotNull T item) {
        // log(N) complexity; index where a new item may be
        // inserted
        val i = Collections.binarySearch(data, item, comparator);

        if (i >= 0) {
            var j = i;
            // move back because there can be the situation when
            // comparator.compare(item, raw[i]) == 0 for some items
            // which are from the left side of i-th
            for (; j > 0 && comparator.compare(item, data.get(j)) == 0 && item.getViewId() != data.get(j).getViewId(); --j)
                ;
            // index somewhere in the middle
            // of the list was found
            var identityFound = false;
            var isPosFound = false;
            // iterate through list until items are smaller than #item
            // or item with same identity will be found in the data set
            for (; !identityFound && !isPosFound && j < data.size(); ++j) {
                val it = data.get(j);

                if (identityFound = item.getViewId() == it.getViewId()) {
                    // avoid if possible redundant adapter's item
                    // resetting because it causes undesired
                    // 'blinking' effect when redrawing
                    if (!it.equals(item)) {
                        setItem(j, item);
                    }
                } else if (isPosFound = comparator.compare(item, it) < 0) {
                    // we've found the first index where our item can be
                    // inserted or replaced
                    addItem(j, item);
                }
            }
            // we've reached the end of the list
            if (!identityFound && !isPosFound) {
                addItem(item);
            }
        } else {
            val calcPos = -1 * i - 1;
            addItem(calcPos, item);
        }
    }

    private void setItem(int position, T item) {
        data.set(position, item);
        notifyItemChanged(position + getAdapterOffset(position, item));
    }

    private void addItem(T t) {
        val oldSize = data.size();

        data.add(t);
        notifyItemInserted(data.size() + getAdapterOffset(oldSize, t));
    }

    private void addItem(int position, T item) {
        data.add(position, item);
        notifyItemInserted(position + getAdapterOffset(position, item));
    }

    private void removeItem(int position) {
        val item = data.remove(position);

        if (item != null) {
            notifyItemRemoved(position + getAdapterOffset(position, item));
        }
    }

    @SuppressWarnings("unchecked")
    private ViewHolderAdapter<T> getAdapterOrThrow(int viewType) {
        return Precondition.isNotNull(viewHolders.get(viewType), "Couldn't find view holder for view type %d", viewType);
    }

    private static <T> void ensureSorted(List<? extends T> check, Comparator<T> c) {
        T prev = null;

        for (var i = 0; i < check.size(); ++i) {
            val item = check.get(i);

            if (prev != null) {
                Precondition.checkArgument(c.compare(item, prev) >= 0,
                        String.format(Locale.ENGLISH, "Wrong data order for %s, %s, pos %d", prev, item, i));
            }
            prev = item;
        }
    }

    private static <T extends HasKey> void ensureNoDuplicates(Collection<? extends T> check) {
        val ids = new HashSet<Long>(check.size());

        for (val c : check) {
            Precondition.checkArgument(ids.add(c.getViewId()), "Found id duplicate in collection %s", check);
        }
    }

    private static <T extends HasKey> void ensureDataSetValid(List<T> items, Comparator<? super T> comparator) {
        SortedAdapter.ensureSorted(items, comparator);
        SortedAdapter.ensureNoDuplicates(items);
    }

}
