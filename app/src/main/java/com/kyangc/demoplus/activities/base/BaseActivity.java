package com.kyangc.demoplus.activities.base;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by chengkangyang on 九月.30.2015
 */
public abstract class BaseActivity extends AppCompatActivity {

    abstract void initData();

    abstract void initViews();

    abstract void initReceivers();
}
