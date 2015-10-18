package com.kyangc.demoplus.image;

import android.content.Context;
import android.view.View;

import java.io.File;

/**
 * Created by chengkangyang on 十月.15.2015
 */
public interface IImageLoader {

    void init(Context context);

    void loadImage(View v, String uri);

    void loadDetailImage(View v, String uri);

    void loadSmallImage(View v, String uri);

    File getCachedImageOnDisk(String uri);
}
