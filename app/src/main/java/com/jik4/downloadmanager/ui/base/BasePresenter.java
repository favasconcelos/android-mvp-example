package com.jik4.downloadmanager.ui.base;


public class BasePresenter<V extends BaseView> {

    private V view;

    public void onAttach(V view) {
        this.view = view;
    }

    public void onDetach() {
        view = null;
    }

    public V getView() {
        return this.view;
    }

    public boolean isViewAttached() {
        return this.view != null;
    }
}
