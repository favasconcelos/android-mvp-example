package com.jik4.downloadmanager.ui.main.addurl;


import com.jik4.downloadmanager.ui.base.BasePresenter;

public interface AddURLDialogPresenter<V extends AddURLDialogView> extends BasePresenter<V> {

    void onSubmit(String url);
}
