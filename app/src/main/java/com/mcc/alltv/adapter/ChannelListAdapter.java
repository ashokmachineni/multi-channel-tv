package com.mcc.alltv.adapter;


import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mcc.alltv.data.sqllite.FavouriteDbController;
import com.mcc.alltv.model.Channel;
import java.util.ArrayList;
import com.mcc.alltv.R;
import com.mcc.alltv.utilities.ActivityUtils;

public class ChannelListAdapter extends RecyclerView.Adapter<ChannelListAdapter.ViewHolder> {

    private Activity mActivity;
    private ArrayList<Channel> dataList;

    private FavouriteDbController dbHelper;

    public ChannelListAdapter(Activity activity, ArrayList<Channel> mDataList) {
        this.mActivity = activity;
        this.dataList = mDataList;
    }


    public  class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvChannelName;
        private ImageView ivChannelLogo, ivFavorite;

        public ViewHolder(final View itemView, int viewType) {
            super(itemView);

            tvChannelName = itemView.findViewById(R.id.channelName);
            ivChannelLogo = itemView.findViewById(R.id.channelLogo);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (dataList.get(getLayoutPosition()).getStreamUrl() != null) {
                        if (isYouTubeUrl(dataList.get(getLayoutPosition()).getStreamUrl())) {
                            ActivityUtils.getInstance().invokeYoutubePlayerViewActivity(mActivity, dataList.get(getLayoutPosition()), dataList);

                        } else {
                            ActivityUtils.getInstance().invokeExoPlayerViewActivity(mActivity, dataList.get(getLayoutPosition()), dataList);
                        }
                    }
                }
            });
            ivFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Channel channelData = dataList.get(getLayoutPosition());

                    FavouriteDbController favouriteDbController = new FavouriteDbController(mActivity);

                    if (favouriteDbController.isAlreadyFavourite(String.valueOf(channelData.getChannelId()))) {
                        favouriteDbController.deleteFavItem(String.valueOf(channelData.getChannelId()));
                        notifyDataSetChanged();

                    } else {
                        favouriteDbController.insertData(String.valueOf(channelData.getChannelId()), channelData.getChannelLogoUrl(), channelData.getChannelName(), channelData.getStreamUrl(), channelData.getIsLive());
                        notifyDataSetChanged();
                    }
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_channel_list, parent, false);
        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Channel channelData = dataList.get(position);
        holder.tvChannelName.setText(channelData.getChannelName());

        Glide.with(mActivity)
                .load(channelData.getChannelLogoUrl())
                .placeholder(R.color.imgPlaceHolder)
                .into(holder.ivChannelLogo);

        dbHelper = new FavouriteDbController(mActivity);
        final boolean isAlreadyFavourite = dbHelper.isAlreadyFavourite(String.valueOf(dataList.get(position).getChannelId()));

        if (isAlreadyFavourite) {
            holder.ivFavorite.setImageResource(R.drawable.ic_favorite);
        } else {
            holder.ivFavorite.setImageResource(R.drawable.ic_unfavorite);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    //check Url http or Youtube
    private boolean isYouTubeUrl(String url) {
        if (url.contains("http")) {
            return false;
        } else {
            return true;
        }
    }
}
