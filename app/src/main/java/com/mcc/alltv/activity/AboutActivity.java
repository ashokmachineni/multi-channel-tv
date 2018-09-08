package com.mcc.alltv.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mcc.alltv.R;
import com.mcc.alltv.data.constants.AppConstant;
import com.mcc.alltv.utilities.AppUtility;

public class AboutActivity extends BaseActivity {

    private Context mContext;
    private TextView tvAppVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVar();
        initView();
        initFunctionality();
        initListener();
    }

    private void initVar() {
        mContext = getApplicationContext();
    }

    private void initView() {
        setContentView(R.layout.activity_about);

        tvAppVersion = (TextView) findViewById(R.id.tvAppVersion);
        /*  initiate toolbar*/
        initToolbar();
        setToolBarTittle(AppConstant.ABOUT_TITLE);
        enableBackButton();
    }

    private void initFunctionality() {
        String appVersionName = AppUtility.getInstance().getAppVersionName(mContext);
        if (!appVersionName.isEmpty() || appVersionName.equals("")) {
            tvAppVersion.setText(getString(R.string.version) + appVersionName);
        } else {
            tvAppVersion.setVisibility(View.GONE);
        }
    }

    private void initListener() {
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
