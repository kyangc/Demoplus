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

import com.kyangc.developkit.gesture.MoveGestureDetector;
import com.kyangc.developkit.gesture.RotateGestureDetector;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PointF;
import android.os.Build;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;

public class PhotoViewAttacher<PHOTOVIEW extends View & IPhotoView> implements OnTouchListener {

    private PHOTOVIEW photoview;

    private ScaleGestureDetector mScaleDetector;

    private RotateGestureDetector mRotateDetector;

    private MoveGestureDetector mMoveDetector;

    private GestureDetector gestureDetector;

    private float mScaleFactor = 1;

    private float mRotationDegrees = 0;

    private float mFocusX = 0;

    private float mFocusY = 0;

    private OnPhotoTapListener onPhotoTapListener;

    private SimpleOnGestureListener simpleOnGestureListener = new SimpleOnGestureListener() {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (mScaleFactor < 0.5) {
                mScaleFactor = 0.5f;
            } else if (mScaleFactor < 1) {
                mScaleFactor = 1f;
            } else if (mScaleFactor < 2) {
                mScaleFactor = 2f;
            } else {
                mScaleFactor = 0.5f;
            }
            mFocusX = 0;
            mFocusY = 0;
            return true;
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mFocusX += distanceX;
            mFocusY += distanceY;
            photoview.setScale(mScaleFactor, mFocusX, mFocusY);
            return false;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (onPhotoTapListener != null) {
                onPhotoTapListener.onPhotoTap(photoview, e);
            }
            return super.onSingleTapUp(e);
        }
    };

    public PhotoViewAttacher(PHOTOVIEW photoview) {
        super();
        this.photoview = photoview;
        Context context = photoview.getContext();
        photoview.setOnTouchListener(this);

        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        mRotateDetector = new RotateGestureDetector(context, new RotateListener());
        mMoveDetector = new MoveGestureDetector(context, new MoveListener());
        gestureDetector = new GestureDetector(context, simpleOnGestureListener);
    }

    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouch(View v, MotionEvent event) {
        mScaleDetector.onTouchEvent(event);
        mRotateDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        photoview.setScale(mScaleFactor, mFocusX, mFocusY);

        // indicate event was handled
        return true;
    }

    public void setOnPhotoTapListener(OnPhotoTapListener onPhotoTapListener) {
        this.onPhotoTapListener = onPhotoTapListener;
    }

    public interface OnPhotoTapListener {

        void onPhotoTap(View view, MotionEvent e);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            // scale change since
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 100.0f));
            return true;
        }
    }

    private class RotateListener extends RotateGestureDetector.SimpleOnRotateGestureListener {

        @Override
        public boolean onRotate(RotateGestureDetector detector) {
            mRotationDegrees -= detector.getRotationDegreesDelta();
            return true;
        }
    }

    private class MoveListener extends MoveGestureDetector.SimpleOnMoveGestureListener {

        @Override
        public boolean onMove(MoveGestureDetector detector) {
            PointF d = detector.getFocusDelta();
            mFocusX -= d.x;
            mFocusY -= d.y;
            return true;
        }
    }

}
