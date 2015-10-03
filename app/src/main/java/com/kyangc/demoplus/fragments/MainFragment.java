package com.kyangc.demoplus.fragments;

import com.kyangc.demoplus.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by chengkangyang on 七月.29.2015
 * <p/>
 * 主Fragment，App第一屏
 */
public class MainFragment extends Fragment {

    public static final String TAG = "MainFragment";

    public static final String TITLE = "Demo+";

    public MainFragment() {
        // Required empty public constructor
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
        return inflater.inflate(R.layout.fragment_main, container, false);
    }
}
