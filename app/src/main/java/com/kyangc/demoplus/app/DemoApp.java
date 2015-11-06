package com.kyangc.demoplus.app;

import com.kyangc.demoplus.bus.handler.HttpRequestHandler;
import com.kyangc.developkit.base.BaseApp;
import com.kyangc.developkit.image.impl.ImageLoaderImpl;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import timber.log.Timber;

/**
 * Created by chengkangyang on 七月.29.2015
 */
public class DemoApp extends BaseApp {

    protected static RefWatcher mRefWatcher;

    protected HttpRequestHandler mHttpRequestHandler;

    public static RefWatcher getRefWatcher() {
        return mRefWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Log setting
        Timber.plant(new Timber.DebugTree());

        //Leak watcher
        mRefWatcher = LeakCanary.install(this);

        //Image loader
        ImageLoaderImpl.getInstance().init(getApplicationContext());

        //Http request handler
        mHttpRequestHandler = HttpRequestHandler.getInstance();
    }
}
