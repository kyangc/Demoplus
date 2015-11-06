package com.kyangc.developkit.base;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Created by chengkangyang on 十月.14.2015
 */
public class BaseFragment extends Fragment {

    public Activity mActivity;

    public Class mClass;

    public View mView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mClass = this.getClass();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        mActivity = null;
        mClass = null;
        mView = null;
    }

    public void initViews() {
    }

    public void initData() {
    }

    public void initAdapter() {
    }

    public void bindReceiver() {
    }

    public View setView(LayoutInflater inflater, int resId, ViewGroup container) {
        mView = inflater.inflate(resId, container, false);
        ButterKnife.bind(this, mView);
        return mView;
    }
}
