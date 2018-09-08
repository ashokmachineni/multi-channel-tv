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

import com.bumptech.glide.Glide;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.mcc.alltv.R;
import com.mcc.alltv.adapter.RelatedChannelAdapter;
import com.mcc.alltv.data.constants.AppConstant;
import com.mcc.alltv.data.sqllite.FavouriteDbController;
import com.mcc.alltv.listener.OnItemClickListener;
import com.mcc.alltv.model.Channel;
import com.mcc.alltv.utilities.ActivityUtils;
import com.mcc.alltv.utilities.AppUtility;
import com.mcc.alltv.utilities.GridSpacesItemDecoration;

import java.util.ArrayList;


public class YoutubePlayerViewActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private Context mContext;
    private static final int RECOVERY_REQUEST = 1;
    private RecyclerView rvRelatedChannel;
    private RelatedChannelAdapter relatedChannelAdapter;
    private ArrayList<Channel> relatedChannelList;
    private ImageView imvFav, imvShare, imvYouTubeTvLogo;
    private FavouriteDbController dbController;
    private TextView tvChannelName;
    private Activity mActivity;
    private YouTubePlayerView youTubeView;
    private boolean isFavorite;
    private Channel channelData;

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
        relatedChannelList = new ArrayList<>();
        dbController = new FavouriteDbController(mContext);
        mActivity = YoutubePlayerViewActivity.this;

    }

    public void initView() {
        /* set parent view */
        setContentView(R.layout.activity_youtube_player_view);

        imvYouTubeTvLogo = findViewById(R.id.imvYoutubeLogo);
        imvFav = findViewById(R.id.iv_favorite);
        tvChannelName = findViewById(R.id.tvChannelName);
        imvShare = findViewById(R.id.iv_share);
        dbController = new FavouriteDbController(mContext);

        // Load youtube player
        youTubeView = findViewById(R.id.youtube_view);
        youTubeView.initialize(AppConstant.YOUTUBE_API_KEY, this);
        rvRelatedChannel = findViewById(R.id.rv_related_channel);

        // Load tech categories tv as sample category
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        GridSpacesItemDecoration itemDecoration = new GridSpacesItemDecoration(this, R.dimen.recycler_view_spacing);
        rvRelatedChannel.addItemDecoration(itemDecoration);
        rvRelatedChannel.setLayoutManager(mLayoutManager);
        rvRelatedChannel.setItemAnimator(new DefaultItemAnimator());
        relatedChannelAdapter = new RelatedChannelAdapter(this, relatedChannelList);
        rvRelatedChannel.setAdapter(relatedChannelAdapter);
    }


    public void initFunctionality() {
        // Load data
        Intent intent = getIntent();
        if (intent != null) {
            channelData = (Channel) intent.getSerializableExtra(getString(R.string.channel_data));
            // load related channel list
            relatedChannelList.addAll((ArrayList<Channel>) intent.getSerializableExtra(getString(R.string.related_channel_list)));
            // set channel name @ TV Logo
            tvChannelName.setText(channelData.getChannelName());
            Glide.with(mContext).load(channelData.getChannelLogoUrl()).placeholder(R.color.colorPrimary).into(imvYouTubeTvLogo);
            //load Favorite Icon
            loadFavoriteIcon(String.valueOf(channelData.getChannelId()));
        }

    }

    // Test Url Http or Youtube
    private boolean isYouTubeUrl(String url) {
        if (url.contains(AppConstant.HTTP)) {
            return false;
        } else {
            return true;
        }
    }

    public void initListener() {
        // share button click listener
        imvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtility.shareApp(mActivity);
            }
        });

        //YoutubePlayerViewActivity favorite button click listener
        imvFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFavorite = dbController.isAlreadyFavourite(String.valueOf(channelData.getChannelId()));
                if (isFavorite) {
                    imvFav.setImageResource(R.drawable.ic_unfavorite);
                    dbController.deleteFavItem(String.valueOf(channelData.getChannelId()));
                    isFavorite = false;
                } else {
                    imvFav.setImageResource(R.drawable.ic_favorite);
                    try {
                        isFavorite = true;
                        dbController.insertData(String.valueOf(channelData.getChannelId()), channelData.getChannelLogoUrl(), channelData.getChannelName(), channelData.getStreamUrl(), channelData.getIsLive());
                    } catch (Exception ignored) {
                    }
                }

            }

        });

        //load related Channel data
        relatedChannelAdapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemListener(View view, int position) {
                if (isYouTubeUrl(relatedChannelList.get(position).getStreamUrl())) {
                    ActivityUtils.getInstance().invokeYoutubePlayerViewActivity(mActivity, relatedChannelList.get(position), relatedChannelList);
                    finish();
                } else {
                    ActivityUtils.getInstance().invokeExoPlayerViewActivity(mActivity, relatedChannelList.get(position), relatedChannelList);
                    finish();
                }

            }
        });

    }

    //check Icon Favorite or UnFavorite
    private void loadFavoriteIcon(String channelId) {
        if (dbController.isAlreadyFavourite(channelId)) {
            imvFav.setImageResource(R.drawable.ic_favorite);
        } else {
            imvFav.setImageResource(R.drawable.ic_unfavorite);
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
            player.cueVideo(channelData.getStreamUrl()); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(AppConstant.YOUTUBE_API_KEY, this);
        }
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return youTubeView;
    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!AppUtility.isNetworkAvailable(mContext)) {
            AppUtility.noInternetWarning(findViewById(R.id.youtubeParentView), mContext);
        }
    }
}