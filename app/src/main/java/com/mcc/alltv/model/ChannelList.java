package com.mcc.alltv.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChannelList {

    @SerializedName("channel")
    @Expose
    private List<Channel> channel = null;

    public List<Channel> getChannel() {
        return channel;
    }

    public void setChannel(List<Channel> channel) {
        this.channel = channel;
    }

}
