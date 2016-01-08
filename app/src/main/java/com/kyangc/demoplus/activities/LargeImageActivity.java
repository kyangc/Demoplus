package com.kyangc.demoplus.activities;

import com.kyangc.demoplus.R;
import com.kyangc.demoplus.views.DetailedImageView;
import com.kyangc.demoplus.views.LargeImageView;
import com.kyangc.demoplus.views.LongImageView;
import com.kyangc.demoplus.views.PhotoView;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LargeImageActivity extends AppCompatActivity {

    @Bind(R.id.ivPhoto)
    DetailedImageView mLongImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_large_image);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            mLongImageView.setInputStream(getAssets().open("ccc.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
