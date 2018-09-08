package com.mcc.alltv.activity;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.android.gms.ads.AdView;
import com.mcc.alltv.R;
import com.mcc.alltv.adapter.FavoriteAdapter;
import com.mcc.alltv.data.constants.AppConstant;
import com.mcc.alltv.data.sqllite.FavouriteDbController;
import com.mcc.alltv.listener.FavItemClickListener;
import com.mcc.alltv.model.Channel;
import com.mcc.alltv.utilities.ActivityUtils;
import com.mcc.alltv.utilities.AdUtils;
import com.mcc.alltv.utilities.AppUtility;
import com.mcc.alltv.utilities.GridSpacesItemDecoration;

import java.util.ArrayList;


public class FavoriteListActivity extends BaseActivity {

    private Context mContext;
    private RecyclerView rvFavorite;
    private FavoriteAdapter favoriteAdapter;
    private ArrayList<Channel> favChannelList;
    private FavouriteDbController favController;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariable();
        initView();
        initFunctionality();
        initListener();
    }

    public void initVariable() {
        mContext = getApplicationContext();
        favChannelList = new ArrayList<>();
        favController = new FavouriteDbController(this);
        mActivity = FavoriteListActivity.this;
    }

    public void initView() {

       /* set parent view */
        setContentView(R.layout.activity_favorite_list);

        /*  initiate toolbar*/
        initToolbar();
        setToolBarTittle(AppConstant.FAB_TITLE);
        enableBackButton();
        initEmptyView();

        rvFavorite = findViewById(R.id.favoriteRecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        GridSpacesItemDecoration itemDecoration = new GridSpacesItemDecoration(this, R.dimen.recycler_view_spacing);
        rvFavorite.addItemDecoration(itemDecoration);
        rvFavorite.setLayoutManager(mLayoutManager);
        rvFavorite.setItemAnimator(new DefaultItemAnimator());
        favoriteAdapter = new FavoriteAdapter(getApplicationContext(), favChannelList);
        rvFavorite.setAdapter(favoriteAdapter);
    }

    private void initFunctionality() {
       /* Load favorite data */
        loadFavouriteData();
    }

    private void initListener() {
        favoriteAdapter.setItemClickListener(new FavItemClickListener() {
            @Override
            public void onItemListener(View view, int position) {
                if (favChannelList.get(position).getStreamUrl() != null) {
                    if (isYouTubeUrl(favChannelList.get(position).getStreamUrl())) {
                        ActivityUtils.getInstance().invokeYoutubePlayerViewActivity(mActivity, favChannelList.get(position), favChannelList);

                    } else {
                        ActivityUtils.getInstance().invokeExoPlayerViewActivity(mActivity, favChannelList.get(position), favChannelList);
                    }
                }
            }

            @Override
            public void onFavIconListener(View view, int position) {
                favController.deleteFavItem(String.valueOf(favChannelList.get(position).getChannelId()));
                loadFavouriteData();
            }
        });

        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private void loadFavouriteData() {
        if (!favChannelList.isEmpty()) {
            favChannelList.clear();

        }
        favChannelList.addAll(favController.getAllData());
        favoriteAdapter.notifyDataSetChanged();

        if (favChannelList.isEmpty()) {
            showEmptyView();
        } else {
            hideEmptyView();
        }
    }

    private boolean isYouTubeUrl(String url) {
        if (url.contains(AppConstant.HTTP)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!AppUtility.isNetworkAvailable(mContext)) {
            AppUtility.noInternetWarning(findViewById(R.id.parentFav), mContext);
        }
        // load banner ad
        AdUtils.getInstance(mContext).showBannerAd((AdView) findViewById(R.id.adView));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

