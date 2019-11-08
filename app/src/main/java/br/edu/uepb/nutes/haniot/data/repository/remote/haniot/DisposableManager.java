package br.edu.uepb.nutes.haniot.data.repository.remote.haniot;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class DisposableManager {
    private static CompositeDisposable compositeDisposable;

    public static void clear() {
        compositeDisposable.clear();
    }

    public static void add(@NonNull Disposable disposable) {
        getCompositeDisposable().add(disposable);
    }

    public static void addAll(@NonNull Disposable... ds) {
        getCompositeDisposable().addAll(ds);
    }

    public static void dispose() {
        getCompositeDisposable().dispose();
    }

    private static CompositeDisposable getCompositeDisposable() {
        if (compositeDisposable == null || compositeDisposable.isDisposed()) {
            compositeDisposable = new CompositeDisposable();
        }
        return compositeDisposable;
    }

    private DisposableManager() {
    }
}
