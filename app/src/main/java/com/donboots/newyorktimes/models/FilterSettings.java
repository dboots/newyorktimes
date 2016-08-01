package com.donboots.newyorktimes.models;


import android.util.Log;

import java.util.HashMap;

public class FilterSettings {
    public String beginDate;
    public String sortOrder;
    public String newsDesks;

    public FilterSettings() {}

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder.toLowerCase();
    }

    public void setNewsDesks(HashMap<String, Boolean> map) {
        String result = "";
        for (String key : map.keySet()) {
            Log.d("LOG", key + " = " + map.get(key));
            if (map.get(key)) {
                result += "\"" + key + "\",";
            }
        }

        this.newsDesks = "news_desk:(" + result + ")";
    }

    public String getNewsDesks() {
        return this.newsDesks;
    }

    public String getBeginDate() {
        return this.beginDate;
    }

    public String getSortOrder() {
        return this.sortOrder;
    }
}