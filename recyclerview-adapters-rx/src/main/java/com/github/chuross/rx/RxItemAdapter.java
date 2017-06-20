package com.github.chuross.rx;


import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;

import com.github.chuross.recyclerviewadapters.BaseItemAdapter;
import com.github.chuross.rx.internal.DiffCalculationObservable;
import com.github.chuross.rx.internal.DiffCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public abstract class RxItemAdapter<I, VH extends RecyclerView.ViewHolder> extends BaseItemAdapter<I, VH> {

    private Disposable disposable;
    private Disposable calculateDisposable;
    private Flowable<List<I>> flowable;
    private OnItemDataChangedListener itemDataChangedListener;
    private List<I> cachedItems = Collections.emptyList();

    public RxItemAdapter(@NonNull Context context, @NonNull Flowable<List<I>> flowable) {
        super(context);
        this.flowable = flowable;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        disposable = flowable.filter(new Predicate<List<I>>() {
            @Override
            public boolean test(List<I> is) throws Exception {
                return isAttached();
            }
        }).subscribe(new Consumer<List<I>>() {
            @Override
            public void accept(List<I> is) throws Exception {
                calculateDiff(cachedItems, is);
            }
        });
    }

    private void calculateDiff(final List<I> oldItems, final List<I> newItems) {
        Single<DiffUtil.DiffResult> single = Single.create(new DiffCalculationObservable(new DiffCallback<>(oldItems, newItems)));

        calculateDisposable = single
                .subscribeOn(Schedulers.from(AsyncTask.SERIAL_EXECUTOR))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DiffUtil.DiffResult>() {
                    @Override
                    public void accept(DiffUtil.DiffResult diffResult) throws Exception {
                        cachedItems = new ArrayList<>(newItems);
                        diffResult.dispatchUpdatesTo(RxItemAdapter.this);
                        if (itemDataChangedListener != null) itemDataChangedListener.onDataChanged();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                    }
                });
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        if (disposable != null) disposable.dispose();
        if (calculateDisposable != null) calculateDisposable.dispose();
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return cachedItems.size();
    }

    @NonNull
    @Override
    public I get(int position) {
        return cachedItems.get(position);
    }

    public void setOnDataSetChangedListener(OnItemDataChangedListener listener) {
        itemDataChangedListener = listener;
    }
}
