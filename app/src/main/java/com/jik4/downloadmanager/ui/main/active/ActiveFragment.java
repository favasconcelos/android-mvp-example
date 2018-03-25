package com.jik4.downloadmanager.ui.main.active;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jik4.downloadmanager.R;
import com.jik4.downloadmanager.database.model.Download;
import com.jik4.downloadmanager.ui.base.BaseFragment;
import com.jik4.downloadmanager.ui.base.DownloadListAdapter;

import java.util.List;

public class ActiveFragment extends BaseFragment {

    // Presenter
    private ActiveFragmentPresenterImpl mPresenter;
    // View
    private RecyclerView mRecyclerView;
    // Adapter
    private DownloadListAdapter mActiveListAdapter;

    public static ActiveFragment newInstance() {
        Bundle args = new Bundle();
        ActiveFragment fragment = new ActiveFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mPresenter = new ActiveFragmentPresenterImpl();
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending, container, false);
        mRecyclerView = view.findViewById(R.id.pending_recycler_view);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        mPresenter.onDetach();
        super.onDestroyView();
    }

    @Override
    protected void setUp(View view) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        mActiveListAdapter = new DownloadListAdapter();
        mRecyclerView.setAdapter(mActiveListAdapter);

        ViewModelProviders
                .of(this)
                .get(ActiveListViewModel.class)
                .getActiveList()
                .observe(this, new Observer<List<Download>>() {
                    @Override
                    public void onChanged(@Nullable List<Download> downloads) {
                        Log.d("jika", "ActiveFragment - onChanged");
                        mActiveListAdapter.update(downloads);
                    }
                });
    }

}
