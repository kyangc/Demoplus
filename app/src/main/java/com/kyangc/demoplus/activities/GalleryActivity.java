package com.kyangc.demoplus.activities;

import com.kyangc.demoplus.R;
import com.kyangc.demoplus.activities.base.BaseActivity;
import com.kyangc.demoplus.adapters.GalleryAdapter;
import com.kyangc.demoplus.image.ILocalImageLoader;
import com.kyangc.demoplus.utils.GalleryUtils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.TreeMap;

import butterknife.Bind;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class GalleryActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.rvGallery)
    RecyclerView rvGallery;

    TreeMap<Long, ILocalImageLoader.LocalImageEntity> mGalleryItemMap;

    GalleryAdapter mGalleryAdapter;

    Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        initData();
        initAdapters();
        initViews();
        queryAllLocalPhotos();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscription != null && !subscription.isUnsubscribed()) {
            Timber.i("Unsubscription");
            subscription.unsubscribe();
        }
    }

    @Override
    public void initData() {
        super.initData();
        mGalleryItemMap = new TreeMap<>();
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
        mGalleryAdapter.setData(mGalleryItemMap);
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, GalleryActivity.class));
    }

    private void queryAllLocalPhotos() {
        Observable
                .create(new Observable.OnSubscribe<TreeMap<Long, ILocalImageLoader.LocalImageEntity>>() {
                    @Override
                    public void call(Subscriber<? super TreeMap<Long, ILocalImageLoader.LocalImageEntity>> subscriber) {
                        subscriber.onNext(GalleryUtils.getPhotoList());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<TreeMap<Long, ILocalImageLoader.LocalImageEntity>>() {
                    @Override
                    public void call(TreeMap<Long, ILocalImageLoader.LocalImageEntity> longGalleryItemTreeMap) {
                        GalleryActivity.this.mGalleryItemMap.putAll(longGalleryItemTreeMap);
                        mGalleryAdapter.notifyDataSetChanged();
                    }
                });
    }
}
