package com.kyangc.demoplus.activities.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        mContext = null;
        mClass = null;
        mIntent = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * 初始化Activity显示需要的数据
     */
    protected void initData() {
    }

    /**
     * 初始化Activity中的控件
     */
    protected void initViews() {
        ButterKnife.bind(this);
    }

    /**
     * 初始化Activity中的广播接收器
     */
    protected void initReceivers() {
    }

    /**
     * 初始化Activity中的Fragment
     */
    protected void initFragments() {
    }

    /**
     * 初始化Activity中的适配器
     */
    protected void initAdapters() {
    }
}
