/*
Copyright 2015 shizhefei（LuckyJayce）
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
   http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.kyangc.demoplus.views;

import android.content.Context;
import android.util.AttributeSet;

public class PhotoView extends LargeImageView {

    private PhotoViewAttacher<PhotoView> photoViewAttacher;

    public PhotoView(Context context) {
        super(context);
        photoViewAttacher = new PhotoViewAttacher<PhotoView>(this);
    }

    public PhotoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        photoViewAttacher = new PhotoViewAttacher<PhotoView>(this);
    }

    public PhotoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        photoViewAttacher = new PhotoViewAttacher<PhotoView>(this);
    }

    public PhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        photoViewAttacher = new PhotoViewAttacher<PhotoView>(this);
    }

    public void setOnPhotoTapListener(PhotoViewAttacher.OnPhotoTapListener onPhotoTapListener) {
        photoViewAttacher.setOnPhotoTapListener(onPhotoTapListener);
    }

}
