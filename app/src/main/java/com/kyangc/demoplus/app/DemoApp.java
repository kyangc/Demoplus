package com.kyangc.demoplus.app;

import com.kyangc.developkit.base.BaseApp;

/**
 * Created by chengkangyang on 七月.29.2015
 */
public class DemoApp extends BaseApp {

    /**
     * 两次按下返回键退出的间隔
     */
    public static final Integer QUIT_INTERVAL = 2 * 1000;

    /**
     * 我的邮箱
     */
    public static final String EMAIL = "kyangc@gmail.com";

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;
    }
}
