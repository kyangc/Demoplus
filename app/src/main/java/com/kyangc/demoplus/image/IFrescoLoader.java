package com.kyangc.demoplus.image;

import com.facebook.drawee.controller.ControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;

import android.view.View;

/**
 * Created by chengkangyang on 十月.15.2015
 */
public interface IFrescoLoader {

    void loadDetailImage(View v, String uri, ControllerListener<ImageInfo> listener);
}
