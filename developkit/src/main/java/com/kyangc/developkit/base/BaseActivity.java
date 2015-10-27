package com.kyangc.developkit.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import butterknife.ButterKnife;

/**
 * Created by chengkangyang on 九月.30.2015
 */
public class BaseActivity extends AppCompatActivity {

    public String TAG;

    public Class mClass;

    public BaseActivity mContext;

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

    protected void initData() {
    }

    protected void initViews() {
        ButterKnife.bind(this);
    }

    protected void bindReceivers() {
    }

    protected void initFragments() {
    }

    protected void initAdapters() {
    }

    protected void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    protected void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    protected void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
    }
}
