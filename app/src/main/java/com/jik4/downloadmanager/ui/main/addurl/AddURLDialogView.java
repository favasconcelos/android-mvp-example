package com.jik4.downloadmanager.ui.main.addurl;


import android.support.annotation.StringRes;

import com.jik4.downloadmanager.database.model.Download;
import com.jik4.downloadmanager.ui.base.BaseDialogView;

public interface AddURLDialogView extends BaseDialogView {

    void showError(@StringRes int resId);

    void dismissDialog();

    void sendDownloadIntent(Download download);

    void addDownload(Download download, AddURLViewModel.OnDataInsertCallback onDataInsertCallback);

}
