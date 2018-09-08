package com.mcc.alltv.model;

import java.util.ArrayList;

/**
 * Created by Nasir on 8/23/17.
 */

public class CategoryListItem {
    public int id;
    public String name;
    public ArrayList<Channel> channelList;

    public CategoryListItem(int id, String name, ArrayList<Channel> channelLIst){
        this.id = id;
        this.name = name;
        this.channelList = channelLIst;
    }
}
