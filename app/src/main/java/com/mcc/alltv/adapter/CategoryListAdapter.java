package com.mcc.alltv.adapter;


import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.ivbaranov.mfb.Utils;
import com.mcc.alltv.data.sqllite.FavouriteDbController;
import com.mcc.alltv.listener.FavItemClickListener;
import com.mcc.alltv.model.CategoryListItem;
import com.mcc.alltv.model.Channel;
import java.util.ArrayList;
import com.mcc.alltv.R;
import com.mcc.alltv.utilities.ActivityUtils;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ViewHolder> {

    private  Activity mActivity;
    private ArrayList<CategoryListItem> dataList;

    public CategoryListAdapter(Activity activity, ArrayList<CategoryListItem> dataList) {
        this.mActivity = activity;
        this.dataList = dataList;
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAttributeName;
        private ChannelListAdapter channelListAdapter;
        private RecyclerView rvChannelList;
        private ArrayList<Channel> arrayList;

        public ViewHolder(final View itemView, int viewType) {
            super(itemView);
            Context context = itemView.getContext();
            tvAttributeName = (TextView) itemView.findViewById(R.id.tvListTitle);

            rvChannelList = (RecyclerView) itemView.findViewById(R.id.rvDataList);
            rvChannelList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

            arrayList = new ArrayList<>();
            channelListAdapter = new ChannelListAdapter(mActivity, arrayList);
            rvChannelList.setAdapter(channelListAdapter);

/*            channelListAdapter.setItemClickListener(new FavItemClickListener() {
                @Override
                public void onItemListener(View view, int position) {
                    if (dataList.get(position).channelList.get(position).getStreamUrl() != null) {
                        if (isYouTubeUrl(dataList.get(position).channelList.get(position).getStreamUrl())) {
                            ActivityUtils.getInstance().invokeYoutubePlayerViewActivity(mActivity, dataList.get(position).channelList.get(position),
                                    dataList.get(position).channelList);

                        } else {
                            ActivityUtils.getInstance().invokeExoPlayerViewActivity(mActivity, dataList.get(position).channelList.get(position),
                                    dataList.get(position).channelList);
                        }
                    }
                }

                @Override
                public void onFavIconListener(View view, int position) {
                    Channel channelData = dataList.get(position).channelList.get(position);

                    FavouriteDbController favouriteDbController = new FavouriteDbController(mActivity);

                    if (favouriteDbController.isAlreadyFavourite(String.valueOf(channelData.getChannelId()))) {
                        favouriteDbController.deleteFavItem(String.valueOf(channelData.getChannelId()));
                        channelListAdapter.notifyDataSetChanged();

                    } else {
                        favouriteDbController.insertData(String.valueOf(channelData.getChannelId()), channelData.getChannelLogoUrl(), channelData.getChannelName(), channelData.getStreamUrl(), channelData.getIsLive());
                        channelListAdapter.notifyDataSetChanged();
                    }
                }
            });*/

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_main_list, parent, false);
        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        // set attribute name
        holder.tvAttributeName.setText(dataList.get(position).name);

        // set attribute value
        if (!holder.arrayList.isEmpty()) {
            holder.arrayList.clear();
        }
        holder.arrayList.addAll(dataList.get(position).channelList);

        holder.channelListAdapter.notifyDataSetChanged(); // List of Strings

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

}
