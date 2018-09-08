package com.mcc.alltv.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcc.alltv.R;
import com.mcc.alltv.adapter.ChannelAdapter;
import com.mcc.alltv.data.constants.AppConstant;
import com.mcc.alltv.data.sqllite.FavouriteDbController;
import com.mcc.alltv.listener.FavItemClickListener;
import com.mcc.alltv.model.Channel;
import com.mcc.alltv.utilities.ActivityUtils;
import com.mcc.alltv.utilities.AnalyticsUtils;
import com.mcc.alltv.utilities.AppUtility;
import com.mcc.alltv.utilities.ChannelUtils;
import com.mcc.alltv.utilities.GridSpacesItemDecoration;

import java.util.ArrayList;

public class ChannelListActivity extends BaseActivity {

    // init variables
    private Context mContext;
    private Activity mActivity;
    private RecyclerView rvChannel;
    private ChannelAdapter channelAdapter;
    private ArrayList<Channel> dataList;
    private FavouriteDbController favouriteDbController;
    private Intent mIntent;
    public int categoryId;
    public ImageView ivFavorite;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVariable();
        initView();
        initFunctionality();
        initListener();
    }

    public void initVariable() {
        favouriteDbController = new FavouriteDbController(this);
        dataList = new ArrayList<>();
        mIntent = getIntent();
        mActivity = ChannelListActivity.this;
        mContext = getApplicationContext();
        categoryId = mIntent.getIntExtra(AppConstant.CATEGORY_ID, AppConstant.VALUE_ZERO);

        // analytics event tiger
        AnalyticsUtils.getAnalyticsUtils(mContext).trackEvent(getString(R.string.channel_list_activity));
    }

    public void initView() {
        // set parent view
        setContentView(R.layout.activity_channel_list);

        /* initiate toolbar and back button*/
        initToolbar();
        loadToolBarTittle();
        enableBackButton();

        mActivity.setTitle(AppConstant.CHANNEL_TITLE);
        ivFavorite = findViewById(R.id.ivFavorite);

        // Load tech categories tv as sample category
        rvChannel = findViewById(R.id.channelRecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        rvChannel.setLayoutManager(mLayoutManager);
        GridSpacesItemDecoration itemDecoration = new GridSpacesItemDecoration(this, R.dimen.recycler_view_spacing);
        rvChannel.addItemDecoration(itemDecoration);
        rvChannel.setItemAnimator(new DefaultItemAnimator());
        channelAdapter = new ChannelAdapter(this, dataList);
        rvChannel.setAdapter(channelAdapter);
    }

    public void initFunctionality() {
        dataList.addAll(ChannelUtils.getTvChannelList(AppConstant.ALL_CHANNEL_LIST, AppConstant.All_CATEGORY));
        channelAdapter.notifyDataSetChanged();
    }

    public void initListener() {
        //   ChannelList item OnClickListener
        channelAdapter.setItemClickListener(new FavItemClickListener() {
            @Override
            public void onItemListener(View view, int position) {
                if (dataList.get(position).getStreamUrl() != null) {
                    if (isYouTubeUrl(dataList.get(position).getStreamUrl())) {
                        ActivityUtils.getInstance().invokeYoutubePlayerViewActivity(mActivity, dataList.get(position), dataList);

                    } else {
                        ActivityUtils.getInstance().invokeExoPlayerViewActivity(mActivity, dataList.get(position), dataList);
                    }
                }
            }

            @Override
            public void onFavIconListener(View view, int position) {
                Channel channelData = dataList.get(position);
                if (favouriteDbController.isAlreadyFavourite(String.valueOf(channelData.getChannelId()))) {
                    favouriteDbController.deleteFavItem(String.valueOf(channelData.getChannelId()));
                    channelAdapter.notifyDataSetChanged();

                } else {
                    favouriteDbController.insertData(String.valueOf(channelData.getChannelId()), channelData.getChannelLogoUrl(), channelData.getChannelName(), channelData.getStreamUrl(), channelData.getIsLive());
                    channelAdapter.notifyDataSetChanged();
                }
            }
        });

        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void loadToolBarTittle() {
        TextView toolTitle = findViewById(R.id.toolbar_title);
        toolTitle.setText(AppConstant.CHANNEL_TITLE);
    }

    //check Url http or Youtube
    private boolean isYouTubeUrl(String url) {
        if (url.contains("http")) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!AppUtility.isNetworkAvailable(mContext)) {

            AppUtility.noInternetWarning(findViewById(R.id.parentView), mContext);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}