package com.kyangc.developkit.base;

import com.kyangc.developkit.image.impl.ImageLoaderImpl;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import android.app.Application;
import android.content.Context;

import timber.log.Timber;

/**
 * Created by chengkangyang on 十月.27.2015
 */
public class BaseApp extends Application {

    protected static BaseApp mContext;

    protected static RefWatcher mRefWatcher;

    public static RefWatcher getRefWatcher() {
        return mRefWatcher;
    }

    public static Context getAppContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;

        //Log setting
        Timber.plant(new Timber.DebugTree());

        //Leak watcher
        mRefWatcher = LeakCanary.install(this);

        //Image loader
        ImageLoaderImpl.getInstance().init(getApplicationContext());
    }
}
