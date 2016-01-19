package com.kyangc.demoplus.activities;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kyangc.demoplus.R;
import com.kyangc.demoplus.adapters.LoopViewPagerAdapter;
import com.kyangc.demoplus.views.LoopViewPager;
import com.kyangc.developkit.base.BaseActivity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

public class BannerActivity extends BaseActivity {

    @Bind(R.id.banner)
    LoopViewPager mViewPager;

    LoopViewPagerAdapter<Integer> mAdapter;

    ArrayList<Integer> list = new ArrayList<>();

    ArrayList<String> urls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        for (int i = 0; i < 5; i++) {
            list.add(i);
        }
        urls.add("http://pic2.ooopic.com/01/03/51/25b1OOOPIC19.jpg");
        urls.add("http://pic14.nipic.com/20110522/7411759_164157418126_2.jpg");
        urls.add("http://img.taopic.com/uploads/allimg/130501/240451-13050106450911.jpg");
        urls.add("http://pic.nipic.com/2007-11-09/200711912453162_2.jpg");
        urls.add("http://pic28.nipic.com/20130402/9252150_190139450381_2.jpg");

        mAdapter = new LoopViewPagerAdapter<Integer>(mViewPager, list) {
            @Override
            public View inflateView(int position, Integer data) {
                return getLayoutInflater().inflate(R.layout.item, null);
            }

            @Override
            public void updateView(View view, int position, Integer data) {
                TextView tv = (TextView) view.findViewById(R.id.tvNumber);
                SimpleDraweeView dv = (SimpleDraweeView) view.findViewById(R.id.ivSD);
                tv.setText("" + list.get(data));
                dv.setImageURI(Uri.parse(urls.get(data)));
                dv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        urls.set(0, "http://pic28.nipic.com/20130402/9252150_190139450381_2.jpg");
                        urls.set(1, "http://pic.nipic.com/2007-11-09/200711912453162_2.jpg");
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        };
        mViewPager.setAdapter(mAdapter);
        mViewPager.setScrollSpeed(0.8f);
        mViewPager.setAutoScroll(true, true , 3000, 2000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mViewPager.startScroll();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mViewPager.stopScroll();
    }
}
