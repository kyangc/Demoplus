package com.kyangc.developkit.image.impl;

import com.facebook.drawee.controller.ControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.kyangc.developkit.image.internal.IFrescoLoader;
import com.kyangc.developkit.image.internal.IImageLoader;

import android.content.Context;
import android.view.View;

import java.io.File;

/**
 * Created by chengkangyang on 十月.15.2015
 */
public class ImageLoaderImpl {

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
}
