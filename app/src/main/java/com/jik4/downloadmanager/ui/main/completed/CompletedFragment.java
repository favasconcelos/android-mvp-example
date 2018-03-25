package com.jik4.downloadmanager.ui.main.completed;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jik4.downloadmanager.R;
import com.jik4.downloadmanager.database.model.Download;
import com.jik4.downloadmanager.ui.base.BaseFragment;
import com.jik4.downloadmanager.ui.base.DownloadListAdapter;

import java.util.List;

public class CompletedFragment extends BaseFragment {

    // View
    private RecyclerView mRecyclerView;
    // Adapter
    private DownloadListAdapter mListAdapter;
    private CompletedFragmentPresenter mPresenter;

    public static CompletedFragment newInstance() {
        Bundle args = new Bundle();
        CompletedFragment fragment = new CompletedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mPresenter = new CompletedFragmentPresenterImpl();
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

        mListAdapter = new DownloadListAdapter();
        mRecyclerView.setAdapter(mListAdapter);

        ViewModelProviders
                .of(this)
                .get(CompletedListViewModel.class)
                .getCompletedList()
                .observe(this, new Observer<List<Download>>() {
                    @Override
                    public void onChanged(@Nullable List<Download> downloads) {
                        mListAdapter.update(downloads);
                    }
                });
    }

}
