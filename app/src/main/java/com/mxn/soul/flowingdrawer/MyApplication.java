package com.mxn.soul.flowingdrawer;

import android.app.Application;

import com.mxn.soul.flowingdrawer.network.RetrofitUtil;

/**
 * Created by weijie.liu on 17/04/2017.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RetrofitUtil.init();
    }
}
