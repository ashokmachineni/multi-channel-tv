package com.mcc.alltv.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Admin on 4/24/2018.
 */

public class CategoryList {
    @SerializedName("category")
    @Expose
    private List<Category> category = null;

    public List<Category> getCategory() {
        return category;
    }

    public void setCategory(List<Category> category) {
        this.category = category;
    }
}
