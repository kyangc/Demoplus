package com.kyangc.demoplus.app;

import com.kyangc.demoplus.bus.handler.HttpRequestHandler;

import android.app.Application;
import android.content.Context;

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
    private static Context context;

    /**
     * 网络请求处理器
     */
    HttpRequestHandler httpRequestHandler;

    /**
     * 获取全局上下文
     */
    public static Context getAppContext() {
        return DemoApp.context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        httpRequestHandler = HttpRequestHandler.getInstance();
    }
}
