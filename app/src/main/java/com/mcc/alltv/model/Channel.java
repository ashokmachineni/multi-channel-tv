package com.mcc.alltv.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Channel implements Serializable{


    @SerializedName("channel_id")
    @Expose
    private Integer channelId;
    @SerializedName("category_id")
    @Expose
    private Integer categoryId;
    @SerializedName("channel_name")
    @Expose
    private String channelName;
    @SerializedName("is_live")
    @Expose
    private Integer isLive;
    @SerializedName("channel_logo_url")
    @Expose
    private String channelLogoUrl;
    @SerializedName("stream_url")
    @Expose
    private String streamUrl;

    public Channel(Integer channelId, Integer categoryId, String channelName, Integer isLive, String channelLogoUrl, String streamUrl) {
        this.channelId = channelId;
        this.categoryId = categoryId;
        this.channelName = channelName;
        this.isLive = isLive;
        this.channelLogoUrl = channelLogoUrl;
        this.streamUrl = streamUrl;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public Integer getIsLive() {
        return isLive;
    }

    public void setIsLive(Integer isLive) {
        this.isLive = isLive;
    }

    public String getChannelLogoUrl() {
        return channelLogoUrl;
    }

    public void setChannelLogoUrl(String channelLogoUrl) {
        this.channelLogoUrl = channelLogoUrl;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

}
