package com.kyangc.demoplus.fragments.base;

import com.kyangc.demoplus.app.DemoApp;

import android.app.Fragment;

/**
 * Created by chengkangyang on 十月.14.2015
 */
public class BaseFragment extends Fragment {

    @Override
    public void onDestroy() {
        super.onDestroy();
        DemoApp.getRefWatcher().watch(this);
    }
}
