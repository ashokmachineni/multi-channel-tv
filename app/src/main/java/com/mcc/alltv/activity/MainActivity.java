package com.mcc.alltv.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mcc.alltv.adapter.CategoryListAdapter;
import com.mcc.alltv.api.HttpParams;
import com.mcc.alltv.api.RetrofitClient;
import com.mcc.alltv.R;
import com.mcc.alltv.adapter.ChannelAdapter;
import com.mcc.alltv.adapter.CustomSwipeAdapter;
import com.mcc.alltv.data.constants.AppConstant;
import com.mcc.alltv.listener.FavItemClickListener;
import com.mcc.alltv.listener.OnItemClickListener;
import com.mcc.alltv.model.Category;
import com.mcc.alltv.model.CategoryList;
import com.mcc.alltv.model.CategoryListItem;
import com.mcc.alltv.model.Channel;
import com.mcc.alltv.model.ChannelList;
import com.mcc.alltv.utilities.ActivityUtils;
import com.mcc.alltv.utilities.AdUtils;
import com.mcc.alltv.utilities.AnalyticsUtils;
import com.mcc.alltv.utilities.ChannelUtils;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {

    // variables
    private Context mContext;
    private Activity mActivity;
    private static final int TIMER_DURATION = 3000;
    private ViewPager viewPager;
    public LayoutManager layoutManager;

    private CategoryListAdapter categoryListAdapter;
    private ArrayList<Channel> liveTvList;
    private RecyclerView rvCategoryList;

    // Dynamic category list
    private ArrayList<Category> categoriesData;
    private ArrayList<CategoryListItem> categoryList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVariable();
        initView();
        loadData();

        //load fullscreen ad
        AdUtils.getInstance(mContext).loadFullScreenAd(mActivity);
    }

    public void initVariable() {

        mContext = getApplicationContext();
        mActivity = MainActivity.this;
        categoriesData = new ArrayList<>();
        categoryList = new ArrayList<>();
        liveTvList = new ArrayList<>();

        // analytics event tiger
        AnalyticsUtils.getAnalyticsUtils(mContext).trackEvent("ChannelList Activity");

    }

    /* All view */
    public void initView() {
        setContentView(R.layout.activity_main);

        /* Load navigation Drawer and Toolbar */
        initToolbar();
        initDrawer();
        initLoader();

        // Initialize category list adapter
        rvCategoryList = findViewById(R.id.rvCategoryList);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        categoryListAdapter = new CategoryListAdapter(mActivity, categoryList);
        rvCategoryList.setLayoutManager(layoutManager);
        rvCategoryList.setAdapter(categoryListAdapter);
    }

    /* All functionality */
    public void loadData() {
        loadCategories();
    }

    private void loadCategories() {

        RetrofitClient.getClient().getCategoryList(HttpParams.SHEET_ID, HttpParams.SHEET_NAME_CATEGORY).enqueue(new Callback<CategoryList>() {
            @Override
            public void onResponse(Call<CategoryList> call, Response<CategoryList> response) {

                AppConstant.ALL_CATEGORY_LIST.clear();
                AppConstant.ALL_CATEGORY_LIST.addAll(response.body().getCategory());

                if (!categoriesData.isEmpty()) {
                    categoriesData.clear();
                }
                categoriesData.addAll(response.body().getCategory());

                if (!categoriesData.isEmpty()) {
                    loadChannelList();
                }
                else {
                    hideLoader();
                    showEmptyView();
                }
            }

            @Override
            public void onFailure(Call<CategoryList> call, Throwable t) {
                Log.d("TimeTesting", "Second Req DetailsDataNotFound");
            }
        });
    }

    private void loadChannelList() {

        AppConstant.ALL_CHANNEL_LIST.clear();
        RetrofitClient.getClient().getChannelList(HttpParams.SHEET_ID, HttpParams.SHEET_NAME).enqueue(new Callback<ChannelList>() {
            @Override
            public void onResponse(Call<ChannelList> call, Response<ChannelList> response) {
                ArrayList<Channel> allChannelList = new ArrayList<>();

                allChannelList.addAll(response.body().getChannel());
                AppConstant.ALL_CHANNEL_LIST.addAll(allChannelList);

                // get live channels
                for (int i = 0; i < allChannelList.size(); i++) {

                    if (allChannelList.get(i).getIsLive() == 1) {
                        liveTvList.add(allChannelList.get(i));
                    }
                }

                // get channel by category
                for (int i = 0; i < categoriesData.size(); i++) {

                    int categoryId = categoriesData.get(i).getCategoryId();
                    String categoryName = categoriesData.get(i).getCategoryName();
                    ArrayList<Channel> channelList = new ArrayList<>();


                    for (int j = 0; j < allChannelList.size(); j++) {

                        Channel channel = allChannelList.get(j);

                        if (channel.getCategoryId() == categoryId) {
                            channelList.add(allChannelList.get(j));
                        }
                    }

                    categoryList.add(new CategoryListItem(categoryId, categoryName, channelList));

                    categoryListAdapter.notifyDataSetChanged();
                }

                loadViewPager();

                hideLoader();
            }

            @Override
            public void onFailure(Call<ChannelList> call, Throwable t) {
                Log.d("TimeTesting", "Second Req DetailsDataNotFound");
                hideLoader();
            }
        });
    }

    /*Reload data to update favorite list List */
    private void reloadChannelList() {

        if (!AppConstant.ALL_CHANNEL_LIST.isEmpty()) {

            if (!categoryList.isEmpty()) {
                categoryList.clear();
            }

            ArrayList<Channel> allChannelList = new ArrayList<>();

            allChannelList.addAll(AppConstant.ALL_CHANNEL_LIST);


            // get channel by category
            for (int i = 0; i < categoriesData.size(); i++) {

                int categoryId = categoriesData.get(i).getCategoryId();
                String categoryName = categoriesData.get(i).getCategoryName();
                ArrayList<Channel> channelList = new ArrayList<>();


                for (int j = 0; j < allChannelList.size(); j++) {

                    Channel channel = allChannelList.get(j);

                    if (channel.getCategoryId() == categoryId) {
                        channelList.add(allChannelList.get(j));
                    }
                }

                categoryList.add(new CategoryListItem(categoryId, categoryName, channelList));

                categoryListAdapter.notifyDataSetChanged();
            }
        }
    }

    /*Load view pager */
    private void loadViewPager() {

        final CustomSwipeAdapter swipeAdapter = new CustomSwipeAdapter(mContext, liveTvList);
        viewPager = findViewById(R.id.vpImageSlider);
        viewPager.setAdapter(swipeAdapter);
        CircleIndicator indicator = findViewById(R.id.sliderIndicator);
        indicator.setViewPager(viewPager);

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                int setPosition = viewPager.getCurrentItem() + 1;
                if (setPosition == liveTvList.size()) {
                    setPosition = AppConstant.VALUE_ZERO;
                }
                viewPager.setCurrentItem(setPosition, true);
                swipeAdapter.notifyDataSetChanged();
            }
        };

        //  Auto animated timer
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, TIMER_DURATION, TIMER_DURATION);

        swipeAdapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemListener(View view, int position) {

                if (liveTvList.get(position).getStreamUrl() != null) {
                    if (isYouTubeUrl(liveTvList.get(position).getStreamUrl())) {
                        ActivityUtils.getInstance().invokeYoutubePlayerViewActivity(mActivity, liveTvList.get(position), liveTvList);
                    } else {
                        ActivityUtils.getInstance().invokeExoPlayerViewActivity(mActivity, liveTvList.get(position), liveTvList);
                    }
                }

            }
        });
    }

    private boolean isYouTubeUrl(String url) {
        return !url.contains(AppConstant.HTTP);
    }

    public void onResume() {
        super.onResume();
        reloadChannelList();
    }

}
