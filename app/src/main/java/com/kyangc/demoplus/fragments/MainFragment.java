package com.kyangc.demoplus.fragments;

import com.kyangc.demoplus.R;
import com.kyangc.demoplus.activities.BannerActivity;
import com.kyangc.demoplus.activities.LargeImageActivity;
import com.kyangc.developkit.base.BaseFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by chengkangyang on 七月.29.2015
 * <p/>
 * 主Fragment，App第一屏
 */
public class MainFragment extends BaseFragment {

    public static String TAG = MainFragment.class.getSimpleName();

    public static final String TITLE = "Demo+";

    @Bind(R.id.ivPhoto)
    ImageView ivPhoto;

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
        mView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, mView);
        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), BannerActivity.class));
            }
        });
        return mView;
    }
}
