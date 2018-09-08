package com.mcc.alltv.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.mcc.alltv.R;
import com.mcc.alltv.utilities.ActivityUtils;
import com.mcc.alltv.utilities.AdUtils;
import com.mcc.alltv.utilities.AnalyticsUtils;
import com.mcc.alltv.utilities.AppUtility;


public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Context mContext;
    private Activity mActivity;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private LinearLayout loadingView, noDataView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariable();
    }

    private void initVariable() {
        mContext = getApplicationContext();
        mActivity = BaseActivity.this;
    }

    public void initToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    public void enableBackButton() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public Toolbar getToolbar() {
        if (mToolbar == null) {
            mToolbar = findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
        }
        return mToolbar;
    }

    public void setToolBarTittle(String toolBarTitle) {
        TextView toolTitle = findViewById(R.id.toolbar_title);
        toolTitle.setText(toolBarTitle);

    }

    public void initDrawer() {

        mDrawerLayout = findViewById(R.id.mDrawer);
        mNavigationView = findViewById(R.id.navigationView);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setItemIconTintList(null);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close);
        toggle.syncState();
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    public void initEmptyView() {
        noDataView = findViewById(R.id.itemNoDataView);
    }

    public void showEmptyView() {
        if (noDataView != null) {
            noDataView.setVisibility(View.VISIBLE);
        }
    }

    public void hideEmptyView() {
        if (noDataView != null) {
            noDataView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            mDrawerLayout.closeDrawer(GravityCompat.START);

        } else if (id == R.id.nav_channel_list) {

            // load full screen ad
            showAdThenActivity(ChannelListActivity.class);

            mDrawerLayout.closeDrawer(GravityCompat.START);

        } else if (id == R.id.nav_favorite) {
            ActivityUtils.getInstance().invokeActivity(mActivity, FavoriteListActivity.class, false);
            mDrawerLayout.closeDrawer(GravityCompat.START);

            // analytics event tiger
            AnalyticsUtils.getAnalyticsUtils(mContext).trackEvent(getString(R.string.fav_activity));

        } else if (id == R.id.nav_share) {
            AppUtility.shareApp(mActivity);
            mDrawerLayout.closeDrawer(GravityCompat.START);

        } else if (id == R.id.nav_rate) {
            AppUtility.rateThisApp(mActivity);
            mDrawerLayout.closeDrawer(GravityCompat.START);

        } else if (id == R.id.nav_about) {
            ActivityUtils.getInstance().invokeActivity(mActivity, AboutActivity.class, false);
            mDrawerLayout.closeDrawer(GravityCompat.START);

        } else if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    public void initLoader() {
        loadingView = findViewById(R.id.loadingView);
        noDataView = findViewById(R.id.noDataView);
    }

    public void hideLoader() {
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
        if (noDataView != null) {
            noDataView.setVisibility(View.GONE);
        }
    }

    public void showAdThenActivity(final Class<?> activity) {
        if (AdUtils.getInstance(mContext).showFullScreenAd()) {
            AdUtils.getInstance(mContext).getInterstitialAd().setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    ActivityUtils.getInstance().invokeActivity(mActivity, activity, false);
                }
            });
        } else {
            ActivityUtils.getInstance().invokeActivity(mActivity, activity, false);
        }
    }
}