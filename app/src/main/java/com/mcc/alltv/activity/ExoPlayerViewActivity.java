package com.mcc.alltv.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
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

public class ExoPlayerViewActivity extends AppCompatActivity {

    private RecyclerView rvRelatedChannels;
    private RelatedChannelAdapter rvchannelAdapter;
    private ArrayList<Channel> relatedChannelList;
    private ImageView imvFav, imvShare, imvFullScreen, imvExoTvLogo;
    private FavouriteDbController dbController;
    private SimpleExoPlayerView exoPlayerView;
    private ExoPlayer exoPlayer;
    private Context mContext;
    private TextView tvChanelName;
    private Activity mActivity;
    private boolean favouriteControllerFlag;
    private Channel channelData;
    private Dialog mFullScreenDialog;
    private boolean mExoPlayerFullscreen;
    private LinearLayout lytLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVariable();
        initView();
        initFunctionality();
        initListeners();
        initFullscreenDialog();
    }

    public void initVariable() {
        mContext = getApplicationContext();
        relatedChannelList = new ArrayList<>();
        dbController = new FavouriteDbController(mContext);
        mActivity = ExoPlayerViewActivity.this;
    }

    public void initView() {
        setContentView(R.layout.activity_exo_player_view);
        imvExoTvLogo = findViewById(R.id.imvExoLogo);
        imvFav = findViewById(R.id.iv_favorite);
        tvChanelName = findViewById(R.id.tvChannelName);
        imvShare = findViewById(R.id.iv_share);
        dbController = new FavouriteDbController(mContext);
        exoPlayerView = findViewById(R.id.exo_player_view);
        imvFullScreen = findViewById(R.id.full_screen);
        rvRelatedChannels = findViewById(R.id.rv_related_channels);
        lytLoadingView = findViewById(R.id.lytLoadingView);

        // Load tech categories tv as sample category
        RecyclerView.LayoutManager mExoLayoutManager = new GridLayoutManager(this, 2);
        GridSpacesItemDecoration itemDecoration = new GridSpacesItemDecoration(this, R.dimen.recycler_view_spacing);

        rvRelatedChannels.addItemDecoration(itemDecoration);
        rvRelatedChannels.setLayoutManager(mExoLayoutManager);
        rvRelatedChannels.setItemAnimator(new DefaultItemAnimator());
        rvchannelAdapter = new RelatedChannelAdapter(this, relatedChannelList);
        rvRelatedChannels.setAdapter(rvchannelAdapter);
    }


    public void initFunctionality() {
        // Load data
        Intent intent = getIntent();
        if (intent != null) {
            channelData = (Channel) intent.getSerializableExtra(AppConstant.CHANNEL_DATA);
            // load related channel list
            relatedChannelList.addAll((ArrayList<Channel>) intent.getSerializableExtra(AppConstant.RELATED_CHANNEL_LIST));
            // set channel name
            tvChanelName.setText(channelData.getChannelName());
            Glide.with(mContext).load(channelData.getChannelLogoUrl()).placeholder(R.color.colorPrimary).into(imvExoTvLogo);

            initExoPlayerLive();
            loadFavoriteIcon(String.valueOf(channelData.getChannelId()));
        }
    }

    public void initListeners() {
        // share button click listener
        imvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtility.shareApp(mActivity);
            }
        });
        // Full screen button click listener
        imvFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFullscreenDialog();
            }
        });

        //channelViewActivity favorite button click listener
        imvFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favouriteControllerFlag = dbController.isAlreadyFavourite(String.valueOf(channelData.getChannelId()));
                if (favouriteControllerFlag) {
                    imvFav.setImageResource(R.drawable.ic_unfavorite);
                    dbController.deleteFavItem(String.valueOf(channelData.getChannelId()));
                    favouriteControllerFlag = false;
                } else {
                    imvFav.setImageResource(R.drawable.ic_favorite);
                    try {
                        favouriteControllerFlag = true;
                        dbController.insertData(String.valueOf(channelData.getChannelId()), channelData.getChannelLogoUrl(), channelData.getChannelName(), channelData.getStreamUrl(), channelData.getIsLive());
                    } catch (Exception ignored) {
                    }
                }

            }

        });
        //load related Channel data
        rvchannelAdapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemListener(View view, int position) {

                if (isYouTubeUrl(relatedChannelList.get(position).getStreamUrl())) {
                    exoPlayer.stop();
                    ActivityUtils.getInstance().invokeYoutubePlayerViewActivity(mActivity, relatedChannelList.get(position), relatedChannelList);
                    finish();
                } else {
                    exoPlayer.stop();
                    ActivityUtils.getInstance().invokeExoPlayerViewActivity(mActivity, relatedChannelList.get(position), relatedChannelList);
                    finish();
                }
            }
        });

        exoPlayer.addListener(new ExoPlayer.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                if (playbackState == ExoPlayer.STATE_ENDED) {

                    //Toast.makeText(getApplicationContext(), "Playback ended", Toast.LENGTH_LONG).show();
                } else if (playbackState == ExoPlayer.STATE_BUFFERING) {
                    Toast.makeText(getApplicationContext(), getString(R.string.buffering), Toast.LENGTH_SHORT).show();
                } else if (playbackState == ExoPlayer.STATE_READY) {
                    lytLoadingView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity() {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }
        });
    }


       /*initiate ExoPlayer*/

    private void initExoPlayerLive() {

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoFactory);

        // Create Player
        exoPlayer = ExoPlayerFactory.newSimpleInstance(getApplicationContext(), trackSelector);

        Handler mHandler = new Handler();
        String userAgent = Util.getUserAgent(this, getString(R.string.user_agent));
        DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory(userAgent, null, DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS, 1800000, true);
        HlsMediaSource mediaSource = new HlsMediaSource(Uri.parse(channelData.getStreamUrl()), dataSourceFactory, 1800000, mHandler, null);
        exoPlayerView.setPlayer((SimpleExoPlayer) exoPlayer);
        exoPlayer.setPlayWhenReady(true);
        exoPlayer.prepare(mediaSource);

    }

    // load exoPlayer fullscreen
    private void initFullscreenDialog() {
        mExoPlayerFullscreen = false;
        mFullScreenDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {

            public void onBackPressed() {
                switchScreen();
                super.onBackPressed();
            }
        };
    }

    private void openFullscreenDialog() {

        ((ViewGroup) exoPlayerView.getParent()).removeView(exoPlayerView);

        mFullScreenDialog.addContentView(exoPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        mExoPlayerFullscreen = true;
        mFullScreenDialog.show();
    }

    private void closeFullscreenDialog() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ((ViewGroup) exoPlayerView.getParent()).removeView(exoPlayerView);
        ((FrameLayout) findViewById(R.id.main_media_frame)).addView(exoPlayerView);
        mExoPlayerFullscreen = false;
        mFullScreenDialog.dismiss();
    }

    private void switchScreen() {
        if (mExoPlayerFullscreen) {
            mExoPlayerFullscreen = false;
            imvFullScreen.setImageResource(R.drawable.ic_fullscreen);
            closeFullscreenDialog();
        } else {
            mExoPlayerFullscreen = true;
            imvFullScreen.setImageResource(R.drawable.ic_fullscreenoff);
            openFullscreenDialog();
        }
    }

    //check Icon Favorite or Un favorite
    private void loadFavoriteIcon(String channelId) {
        if (dbController.isAlreadyFavourite(channelId)) {
            imvFav.setImageResource(R.drawable.ic_favorite);
        } else {
            imvFav.setImageResource(R.drawable.ic_unfavorite);
        }
    }

    //check Url http or Youtube
    private boolean isYouTubeUrl(String url) {
        if (url.contains(AppConstant.HTTP)) {
            return false;
        }
        return true;
    }

    /* Destroy Exo Player*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (exoPlayer != null) {
                exoPlayer.stop();
            }
            finish();

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!AppUtility.isNetworkAvailable(mContext)) {
            AppUtility.noInternetWarning(findViewById(R.id.exoParentView), mContext);
        }
        exoPlayer.release();
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

}
