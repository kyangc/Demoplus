package com.kyangc.developkit.base;

import android.app.Application;
import android.content.Context;

/**
 * Created by chengkangyang on 十月.27.2015
 */
public class BaseApp extends Application {

    protected static BaseApp mContext;

    public static Context getAppContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;
    }
}
