package com.mcc.alltv.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mcc.alltv.R;
import com.mcc.alltv.listener.OnItemClickListener;
import com.mcc.alltv.model.Channel;

import java.util.ArrayList;


public class CustomSwipeAdapter extends PagerAdapter {

    private ArrayList<Channel> dataList;
    private LayoutInflater inflater;
    private Context mContext;

    // Listener
    private OnItemClickListener mListener;

    public CustomSwipeAdapter(Context context, ArrayList<Channel> dataList) {
        this.mContext = context;
        this.dataList = dataList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        Log.d("LoadViewPager", "getCount   "+dataList.size());
        return dataList.size();
    }

    @Override
    public Object instantiateItem(final ViewGroup view, final int position) {

        View imageLayout = inflater.inflate(R.layout.item_channel_image_slider, view, false);
        final ImageView imageView = imageLayout.findViewById(R.id.ChannelLogo);
        Log.d("LoadViewPager", "instantiateItem   "+dataList.get(position).getChannelLogoUrl());
        Glide.with(mContext)
                .load(dataList.get(position).getChannelLogoUrl())
                .placeholder(R.color.colorPrimary)
                .into(imageView);

        view.addView(imageLayout);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                        mListener.onItemListener(view, position);
                }
            }
        });

        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    public void setItemClickListener(OnItemClickListener mListener) {
        this.mListener = mListener;
    }

}