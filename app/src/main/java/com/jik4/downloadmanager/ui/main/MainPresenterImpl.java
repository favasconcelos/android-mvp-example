package com.jik4.downloadmanager.ui.main;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;

import com.jik4.downloadmanager.ui.base.BasePresenterImpl;

public class MainPresenterImpl<V extends MainView> extends BasePresenterImpl<V> implements MainPresenter, LifecycleObserver {

    @Override
    public void onAttach(V view) {
        super.onAttach(view);
        getView().setUp();
    }

    @Override
    public void onFABClick() {
        getView().showAddDownloadDialog();
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
    }

}
