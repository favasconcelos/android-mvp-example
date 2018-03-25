package com.jik4.downloadmanager.ui.main;

import com.jik4.downloadmanager.ui.base.BasePresenterImpl;

public class MainPresenterImpl<V extends MainView> extends BasePresenterImpl<V> implements MainPresenter {

    @Override
    public void onAttach(V view) {
        super.onAttach(view);
        getView().setUp();
    }

    @Override
    public void onFABClick() {
        getView().showAddDownloadDialog();
    }

}
