package com.jik4.downloadmanager.ui.main;


import com.jik4.downloadmanager.ui.base.BasePresenter;

public class MainPresenter<V extends MainView> extends BasePresenter<V> {

    @Override
    public void onAttach(V view) {
        super.onAttach(view);
        getView().setUp();
    }

    public void onFABClick() {
        getView().showAddDownloadDialog();
    }

}
