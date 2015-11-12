package com.kyangc.demoplus.activities;

import com.kyangc.demoplus.R;
import com.kyangc.demoplus.adapters.GalleryAdapter;
import com.kyangc.developkit.base.BaseActivity;
import com.kyangc.developkit.helper.GalleryHelper;
import com.kyangc.developkit.image.internal.ILocalImageLoader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.List;

import butterknife.Bind;
import rx.Subscription;

public class GalleryActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.rvGallery)
    RecyclerView rvGallery;

    List<ILocalImageLoader.LocalImageEntity> mGalleryItemList;

    GalleryAdapter mGalleryAdapter;

    GalleryHelper mGalleryHelper;

    Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        initData();
        initAdapters();
        initViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    @Override
    public void initData() {
        super.initData();
        mGalleryHelper = GalleryHelper.getInstance().init(this);
        mGalleryItemList = mGalleryHelper.getLocalPhotoList();
    }

    @Override
    public void initViews() {
        super.initViews();

        //Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //List
        rvGallery.setAdapter(mGalleryAdapter);
        rvGallery.setLayoutManager(new GridLayoutManager(this, 3));
    }

    @Override
    public void initAdapters() {
        super.initAdapters();
        mGalleryAdapter = new GalleryAdapter(this);
        mGalleryAdapter.setData(mGalleryItemList);
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, GalleryActivity.class));
    }
}
