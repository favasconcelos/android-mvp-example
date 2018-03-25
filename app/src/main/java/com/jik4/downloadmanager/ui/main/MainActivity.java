package com.jik4.downloadmanager.ui.main;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.jik4.downloadmanager.R;
import com.jik4.downloadmanager.ui.base.BaseActivity;
import com.jik4.downloadmanager.ui.main.addurl.AddURLDialog;

public class MainActivity extends BaseActivity implements MainView {

    // Presenter
    private MainPresenterImpl mPresenter;
    // Adapter
    private DownloadPagerAdapter mPagerAdapter;
    // Views
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPresenter = new MainPresenterImpl();
        mPresenter.onAttach(this);
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDetach();
        super.onDestroy();
    }

    @Override
    public void showAddDownloadDialog() {
        AddURLDialog.newInstance().show(getSupportFragmentManager());
    }

    @Override
    public void setUp() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onFABClick();
            }
        });

        mPagerAdapter = new DownloadPagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.view_pager);
        mViewPager.setAdapter(mPagerAdapter);

        mTabLayout = findViewById(R.id.tab_layout);
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.in_progress)));
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.completed)));

        mViewPager.setOffscreenPageLimit(mTabLayout.getTabCount());

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }
}
