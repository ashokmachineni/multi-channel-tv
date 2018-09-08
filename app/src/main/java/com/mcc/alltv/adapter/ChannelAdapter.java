package com.mcc.alltv.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mcc.alltv.R;
import com.mcc.alltv.data.sqllite.FavouriteDbController;
import com.mcc.alltv.listener.FavItemClickListener;
import com.mcc.alltv.model.Channel;

import java.util.ArrayList;


public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<Channel> dataList;
    private FavouriteDbController dbHelper;

    // Listener
    private FavItemClickListener channelListener;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvChannelName;
        private ImageView ivChannelLogo, ivFavorite;

        MyViewHolder(View view) {
            super(view);
            tvChannelName = view.findViewById(R.id.channelName);
            ivChannelLogo = view.findViewById(R.id.channelLogo);
            ivFavorite = view.findViewById(R.id.ivFavorite);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    channelListener.onItemListener(view, getLayoutPosition());
                }
            });
        }
    }

    public ChannelAdapter(Context mContext, ArrayList<Channel> channelList) {
        this.mContext = mContext;
        this.dataList = channelList;
    }

    @Override
    public ChannelAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_channel_list, parent, false);
        return new ChannelAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ChannelAdapter.MyViewHolder holder, final int position) {
        Channel channelData = dataList.get(position);
        holder.tvChannelName.setText(channelData.getChannelName());

        /*loading album cover using Glide library*/
        Glide.with(mContext)
                .load(channelData.getChannelLogoUrl())
                .placeholder(R.color.imgPlaceHolder)
                .into(holder.ivChannelLogo);

        dbHelper = new FavouriteDbController(mContext);
        final boolean isAlreadyFavourite = dbHelper.isAlreadyFavourite(String.valueOf(dataList.get(position).getChannelId()));

        if (isAlreadyFavourite) {
            holder.ivFavorite.setImageResource(R.drawable.ic_favorite);
        } else {
            holder.ivFavorite.setImageResource(R.drawable.ic_unfavorite);
        }

        holder.ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                channelListener.onFavIconListener(view, position);

            }
        });
        holder.ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                channelListener.onFavIconListener(view, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setItemClickListener(FavItemClickListener channelList) {
        this.channelListener = channelList;
    }

}
