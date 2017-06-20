package com.github.chuross.rx.internal;


import android.support.v7.util.DiffUtil;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

public class DiffCalculationObservable implements SingleOnSubscribe<DiffUtil.DiffResult> {

    private DiffCallback<?> callback;

    public DiffCalculationObservable(DiffCallback<?> callback) {
        this.callback = callback;
    }

    @Override
    public void subscribe(SingleEmitter<DiffUtil.DiffResult> emitter) throws Exception {
        try {
            emitter.onSuccess(DiffUtil.calculateDiff(callback));
        } catch (Exception e) {
            emitter.onError(e);
        }
    }
}
