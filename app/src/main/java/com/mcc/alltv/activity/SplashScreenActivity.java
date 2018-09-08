package com.mcc.alltv.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.mcc.alltv.R;
import com.mcc.alltv.utilities.ActivityUtils;

public class SplashScreenActivity extends AppCompatActivity {

    // init variables
    private Context mContext;
    private Activity mActivity;
    private static final int SPLASH_DURATION = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // to view full screen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initVariables();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initFunctionality();
    }

    private void initVariables() {
        mActivity = SplashScreenActivity.this;
        mContext = mActivity.getApplicationContext();
    }

    private void initView() {
        setContentView(R.layout.activity_splash);
    }

    private void initFunctionality() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ActivityUtils.getInstance().invokeActivity(mActivity, MainActivity.class, true);
            }
        }, SPLASH_DURATION);
    }
}
