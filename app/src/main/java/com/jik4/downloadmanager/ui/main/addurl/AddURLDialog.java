package com.jik4.downloadmanager.ui.main.addurl;


import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.jik4.downloadmanager.R;
import com.jik4.downloadmanager.database.model.Download;
import com.jik4.downloadmanager.service.BRConstants;
import com.jik4.downloadmanager.service.DownloadIntentService;
import com.jik4.downloadmanager.ui.base.BaseDialog;

public class AddURLDialog extends BaseDialog implements AddURLDialogView, View.OnClickListener {

    private static final String TAG = "AddURLDialog";

    // Presenter
    private AddURLDialogPresenter mPresenter;
    // Views
    private EditText mUrlEditText;
    private View mSubmitButton;
    // ViewModel
    private AddURLViewModel mViewModel;

    public static AddURLDialog newInstance() {
        AddURLDialog fragment = new AddURLDialog();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mPresenter = new AddURLDialogPresenter();
        mViewModel = ViewModelProviders.of(this).get(AddURLViewModel.class);
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_url, container, false);
        mUrlEditText = view.findViewById(R.id.edt_url);
        mSubmitButton = view.findViewById(R.id.btn_submit);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        mPresenter.onDetach();
        super.onDestroyView();
    }

    public void show(FragmentManager fragmentManager) {
        super.show(fragmentManager, TAG);
    }

    @Override
    public void showError(int resId) {
        mUrlEditText.setError(getString(resId));
    }

    @Override
    public void dismissDialog() {
        super.dismissDialog(TAG);
    }

    @Override
    public void sendDownloadIntent(Download download) {
        Intent it = new Intent(getActivity(), DownloadIntentService.class);
        it.putExtra(BRConstants.EXTRA_DATA_DOWNLOAD, download);
        getActivity().startService(it);
    }

    @Override
    public void addDownload(Download download) {
        ViewModelProviders
                .of(this)
                .get(AddURLViewModel.class).insert(download, mPresenter);
    }

    @Override
    protected void setUp(View view) {
        mSubmitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                mUrlEditText.setError(null);
                mPresenter.onSubmit(mUrlEditText.getText().toString());
                break;
        }
    }

}
