package com.jik4.downloadmanager.ui.main.addurl;


import android.util.Patterns;

import com.jik4.downloadmanager.R;
import com.jik4.downloadmanager.database.model.Download;
import com.jik4.downloadmanager.ui.base.BasePresenterImpl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddURLDialogPresenterImpl<V extends AddURLDialogView> extends BasePresenterImpl<V> implements AddURLDialogPresenter<V>, AddURLViewModel.OnDataInsertCallback {

    @Override
    public void onSubmit(String url) {
        if (url == null || url.isEmpty()) {
            getView().showError(R.string.error_empty_url);
            return;
        }
        if (!isValidUrl(url)) {
            getView().showError(R.string.error_valid_url);
            return;
        }
        getView().addDownload(new Download(url));
    }

    /**
     * This is used to check the given URL is valid or not.
     *
     * @param url
     * @return true if url is valid, false otherwise.
     */
    private boolean isValidUrl(String url) {
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url.toLowerCase());
        return m.matches();
    }

    @Override
    public void onDataInserted(Download download) {
        getView().sendDownloadIntent(download);
        getView().dismissDialog();
    }
}
