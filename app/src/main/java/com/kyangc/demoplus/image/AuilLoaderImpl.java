package com.kyangc.demoplus.image;

import android.content.Context;
import android.view.View;

import java.io.File;

/**
 * Created by chengkangyang on 十月.15.2015
 */
public class AuilLoaderImpl implements IImageLoader {

    private static AuilLoaderImpl ourInstance = new AuilLoaderImpl();

    public static AuilLoaderImpl getInstance() {
        return ourInstance;
    }

    private AuilLoaderImpl() {
    }

    @Override
    public void init(Context context) {

    }

    @Override
    public void loadImage(View v, String uri) {

    }

    @Override
    public void loadDetailImage(View v, String uri) {

    }

    @Override
    public void loadSmallImage(View v, String uri) {

    }

    @Override
    public File getCachedImageOnDisk(String uri) {
        return null;
    }
}
