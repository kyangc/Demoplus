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
        //displayAllPhotos();
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

//    private void displayAllPhotos() {
//        subscription = Observable.create(
//                new Observable.OnSubscribe<TreeMap<Long, GalleryItem>>() {
//                    @Override
//                    public void call(Subscriber<? super TreeMap<Long, GalleryItem>> subscriber) {
//                        subscriber.onNext(GalleryUtils.getPhotoList());
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .flatMap(new Func1<TreeMap<Long, GalleryItem>, Observable<GalleryItem>>() {
//                    @Override
//                    public Observable<GalleryItem> call(
//                            TreeMap<Long, GalleryItem> longGalleryItemTreeMap) {
//                        mGalleryAdapter.addItems(longGalleryItemTreeMap.values());
//                        return Observable.from(longGalleryItemTreeMap.values());
//                    }
//                })
//                .onBackpressureBuffer()
//                .filter(new Func1<GalleryItem, Boolean>() {
//                    @Override
//                    public Boolean call(GalleryItem galleryItem) {
//                        return galleryItem.getThumbnailPath() == null;
//                    }
//                })
//                .buffer(3000, 0, TimeUnit.MILLISECONDS)
//                .map(new Func1<List<GalleryItem>, TreeMap<Long, GalleryItem>>() {
//                    @Override
//                    public TreeMap<Long, GalleryItem> call(List<GalleryItem> items) {
//                        if (items != null && items.size() > 0) {
//                            for (GalleryItem item : items) {
//                                Timber.i("Compressed photo :" + item.toString());
//                                GalleryUtils.prepareThumbnail(GalleryActivity.this, item.getId());
//                            }
//                            return GalleryUtils.getPhotoList(GalleryActivity.this);
//                        } else {
//                            return null;
//                        }
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<TreeMap<Long, GalleryItem>>() {
//                    @Override
//                    public void onCompleted() {
//                        Timber.i("Finished");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Timber.e(e.toString());
//                    }
//
//                    @Override
//                    public void onNext(TreeMap<Long, GalleryItem> longGalleryItemTreeMap) {
//                        if (longGalleryItemTreeMap != null && longGalleryItemTreeMap.size() > 0) {
//                            for (GalleryItem item : longGalleryItemTreeMap.values()) {
//                                mGalleryAdapter.addItem(item);
//                            }
//                        }
//                    }
//                });
//    }

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

    public static class GalleryItem {

        public GalleryItem() {
        }

        private String originPath;

        private String thumbnailPath;

        private Long id;

        private Long modifiedDate;

        public GalleryItem setId(Long id) {
            this.id = id;
            return this;
        }

        public Long getModifiedDate() {
            return modifiedDate;
        }

        public GalleryItem setModifiedDate(Long modifiedDate) {
            this.modifiedDate = modifiedDate;
            return this;
        }

        public String getOriginPath() {
            return originPath;
        }

        public GalleryItem setOriginPath(String originPath) {
            this.originPath = originPath;
            return this;
        }

        public String getThumbnailPath() {
            return thumbnailPath;
        }

        public GalleryItem setThumbnailPath(String thumbnailPath) {
            this.thumbnailPath = thumbnailPath;
            return this;
        }

        public Long getId() {
            return id;
        }

        public String getPath() {
            return thumbnailPath == null ? originPath : thumbnailPath;
        }

        @Override
        public String toString() {
            return "GalleryItem{" +
                    "originPath='" + originPath + '\'' +
                    ", thumbnailPath='" + thumbnailPath + '\'' +
                    ", id=" + id +
                    ", modifiedDate=" + modifiedDate +
                    '}';
        }
    }
}
