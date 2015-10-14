package com.kyangc.demoplus.activities.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by chengkangyang on 九月.30.2015
 */
public class BaseActivity extends AppCompatActivity {

    /**
     * TAG
     */
    public String TAG;

    /**
     * 当前的class
     */
    public Class mClass;

    /**
     * 当前的上下文
     */
    public Context mContext;

    /**
     * 跳转进入的Intent
     */
    public Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TAG = this.getClass().getSimpleName();
        mContext = this;
        mIntent = getIntent();
        mClass = this.getClass();

        Timber.i(mClass.getSimpleName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        mContext = null;
        mClass = null;
        mIntent = null;
    }

    /**
     * 初始化Activity显示需要的数据
     */
    public void initData() {
    }

    /**
     * 初始化Activity中的控件
     */
    public void initViews() {
        ButterKnife.bind(this);
    }

    /**
     * 初始化Activity中的广播接收器
     */
    public void initReceivers() {
    }

    /**
     * 初始化Activity中的Fragment
     */
    public void initFragments() {
    }

    /**
     * 初始化Activity中的适配器
     */
    public void initAdapters() {
    }
}
