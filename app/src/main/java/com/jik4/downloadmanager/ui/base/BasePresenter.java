package com.jik4.downloadmanager.ui.base;


public interface BasePresenter<V extends BaseView> {

    void onAttach(V mvpView);

    void onDetach();

    V getView();

    boolean isViewAttached();
}
