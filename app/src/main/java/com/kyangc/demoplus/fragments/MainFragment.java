package com.kyangc.demoplus.fragments;

import com.kyangc.demoplus.R;
import com.kyangc.developkit.base.BaseFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by chengkangyang on 七月.29.2015
 * <p/>
 * 主Fragment，App第一屏
 */
public class MainFragment extends BaseFragment {

    public static String TAG = MainFragment.class.getSimpleName();

    public static final String TITLE = "Demo+";

    public MainFragment() {
    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return setView(inflater, R.layout.fragment_main, container);
    }
}
