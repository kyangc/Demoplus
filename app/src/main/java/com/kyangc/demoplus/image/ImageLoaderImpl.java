package com.kyangc.demoplus.image;

import com.facebook.drawee.controller.ControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.kyangc.demoplus.utils.GalleryUtils;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import java.io.File;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by chengkangyang on 十月.15.2015
 */
public class ImageLoaderImpl implements ILocalImageLoader {

    public enum LoaderType {Fresco, AUIL}

    private static LoaderType mLoaderType = LoaderType.Fresco;

    private static ImageLoaderImpl ourInstance = new ImageLoaderImpl();

    private static IImageLoader imageLoader;

    public static synchronized ImageLoaderImpl getInstance() {
        return ourInstance;
    }

    private ImageLoaderImpl() {
        switch (ImageLoaderImpl.mLoaderType) {
            case Fresco:
                imageLoader = FrescoLoaderImpl.getInstance();
                break;
            case AUIL:
                imageLoader = AuilLoaderImpl.getInstance();
                break;
            default:
                break;
        }
    }

    public void init(Context context) {
        imageLoader.init(context);
    }

    public void loadImage(View v, String uri) {
        imageLoader.loadImage(v, uri);
    }

    public void loadDetailImage(View v, String uri) {
        imageLoader.loadDetailImage(v, uri);
    }

    public void loadDetailImage(View v, String uri, ControllerListener<ImageInfo> listener) {
        ((IFrescoLoader) imageLoader).loadDetailImage(v, uri, listener);
    }

    public void loadSmallImage(View v, String uri) {
        imageLoader.loadSmallImage(v, uri);
    }

    public File getCachedImageOnDisk(String uri) {
        return imageLoader.getCachedImageOnDisk(uri);
    }

    @Override
    public void loadLocalImage(final View v, final LocalImageEntity imageEntity) {
        if (!TextUtils.isEmpty(imageEntity.getThumbPath())) {
            loadImage(v, imageEntity.getThumbPath());
            return;
        }

        loadImage(v, imageEntity.getOriginPath());

        if (workingMap.containsKey(imageEntity.getId())
                || workingMap.size() > 10) {
            return;
        }

        Observable.create(
                new Observable.OnSubscribe<LocalImageEntity>() {
                    @Override
                    public void call(Subscriber<? super LocalImageEntity> subscriber) {
                        workingMap.put(imageEntity.getId(), true);
                        GalleryUtils.prepareThumbnail(imageEntity.getId());
                        subscriber.onNext(GalleryUtils.getPhoto(imageEntity.getId()));
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<LocalImageEntity>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e.toString());
                    }

                    @Override
                    public void onNext(LocalImageEntity localImageEntity) {
                        if (localImageEntity != null) {
                            workingMap.remove(localImageEntity.getId());
                            imageEntity.setThumbPath(localImageEntity.getThumbPath());
                            Timber.i(localImageEntity.getId().toString() + " thumbnail prepared");
                        }
                    }
                });
    }
}
