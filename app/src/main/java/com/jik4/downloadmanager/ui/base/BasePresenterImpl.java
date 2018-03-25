package com.jik4.downloadmanager.ui.base;


public class BasePresenterImpl<V extends BaseView> implements BasePresenter<V> {

    private V view;

    @Override
    public void onAttach(V view) {
        this.view = view;
    }

    @Override
    public void onDetach() {
        view = null;
    }

    @Override
    public V getView() {
        return this.view;
    }

    @Override
    public boolean isViewAttached() {
        return this.view != null;
    }
}
