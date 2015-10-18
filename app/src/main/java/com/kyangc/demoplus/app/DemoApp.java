package com.kyangc.demoplus.app;

import com.kyangc.demoplus.bus.handler.HttpRequestHandler;
import com.kyangc.demoplus.image.ImageLoaderImpl;
import com.kyangc.demoplus.utils.SysUtils;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by chengkangyang on 七月.29.2015
 */
public class DemoApp extends Application {

    /**
     * TAG
     */
    public static final String TAG = "DemoApp";

    /**
     * 两次按下返回键退出的间隔
     */
    public static final Integer QUIT_INTERVAL = 2 * 1000;

    /**
     * 我的邮箱
     */
    public static final String EMAIL = "kyangc@gmail.com";

    /**
     * 全局上下文
     */
    private static DemoApp mContext;

    /**
     * 网络请求处理器
     */
    public static HttpRequestHandler mHttpRequestHandler;

    /**
     * LeakCanary实例
     */
    private RefWatcher mRefWatcher;

    /**
     * 获取全局上下文
     */
    public static DemoApp getAppContext() {
        return mContext;
    }

    /**
     * 获取RefWatcher
     *
     * @return RefWatcher 实例
     */
    public static RefWatcher getRefWatcher() {
        return DemoApp.getAppContext().mRefWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Context
        mContext = (DemoApp) getApplicationContext();

        //Network module
        mHttpRequestHandler = HttpRequestHandler.getInstance();

        //Log setting
        Timber.plant(new Timber.DebugTree());

        //Leak watcher
        mRefWatcher = LeakCanary.install(this);

        //Image loader
        ImageLoaderImpl.getInstance().init(getApplicationContext());

        //Scan media
        SysUtils.sendMediaScanRequest(mContext);
    }
}
