package com.mcc.alltv.utilities;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class AnalyticsUtils {
    private FirebaseAnalytics mFireBaseAnalytics;
    private static AnalyticsUtils analyticsUtils;

    public static AnalyticsUtils getAnalyticsUtils(Context context) {
        if(analyticsUtils == null) {
            analyticsUtils = new AnalyticsUtils(context);
        }
        return analyticsUtils;
    }

    private AnalyticsUtils(Context context) {
        mFireBaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public void trackEvent(String eventName) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, eventName);
        mFireBaseAnalytics.logEvent("PAGE_VISIT", bundle);
    }

}
