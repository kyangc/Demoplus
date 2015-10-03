package com.kyangc.demoplus.activities.base;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by chengkangyang on 九月.30.2015
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected abstract void initData();

    protected abstract void initViews();

    protected abstract void initReceivers();
}
