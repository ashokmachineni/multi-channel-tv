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
import com.mcc.alltv.listener.FavItemClickListener;
import com.mcc.alltv.model.Channel;

import java.util.ArrayList;


public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<Channel> dataList;
    // Listener
    private FavItemClickListener favListener;

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView channelName;
        private ImageView channelLogo, ivFavorite;

        private MyViewHolder(View view) {
            super(view);
            channelName = view.findViewById(R.id.fav_ChannelName);
            channelLogo = view.findViewById(R.id.fav_ChannelLogo);
            ivFavorite = view.findViewById(R.id.ivFavorite);
        }
    }


    public FavoriteAdapter(Context mContext, ArrayList<Channel> mFavoriteList) {
        this.mContext = mContext;
        this.dataList = mFavoriteList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Channel favItem = dataList.get(position);
        holder.channelName.setText(favItem.getChannelName());

        // loading album cover using Glide library
        Glide.with(mContext)
                .load(favItem.getChannelLogoUrl())
                .placeholder(R.color.imgPlaceHolder)
                .into(holder.channelLogo);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favListener.onItemListener(view, position);
            }
        });
        holder.ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favListener.onFavIconListener(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


    public void setItemClickListener(FavItemClickListener mListener) {
        this.favListener = mListener;
    }
}



