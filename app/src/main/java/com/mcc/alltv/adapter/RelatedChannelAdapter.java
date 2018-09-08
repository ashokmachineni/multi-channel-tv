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
import com.mcc.alltv.listener.OnItemClickListener;
import com.mcc.alltv.model.Channel;

import java.util.ArrayList;

public class RelatedChannelAdapter extends RecyclerView.Adapter<RelatedChannelAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<Channel> relatedChannelList;

    private OnItemClickListener mListener;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView relatedChannelTittle;
        public ImageView relatedChannelLogo, ivFavorite;


        MyViewHolder(View view) {
            super(view);
            relatedChannelTittle = view.findViewById(R.id.tvRelatedChannelName);
            relatedChannelLogo = view.findViewById(R.id.ivRelatedChannelLogo);
            ivFavorite = view.findViewById(R.id.ivFavorite);
        }
    }


    public RelatedChannelAdapter(Context mContext, ArrayList<Channel> relatedChannelList) {
        this.mContext = mContext;
        this.relatedChannelList = relatedChannelList;
    }

    @Override
    public RelatedChannelAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_related_channel, parent, false);

        return new RelatedChannelAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RelatedChannelAdapter.MyViewHolder holder, final int position) {
        Channel category = relatedChannelList.get(position);
        holder.relatedChannelTittle.setText(category.getChannelName());


        // loading album cover using Glide library
        Glide.with(mContext)
                .load(category.getChannelLogoUrl())
                .placeholder(R.color.imgPlaceHolder)
                .into(holder.relatedChannelLogo);

        holder.relatedChannelLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemListener(view, position);

                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return relatedChannelList.size();
    }

    public void setItemClickListener(OnItemClickListener mListener) {
        this.mListener = mListener;
    }
}
