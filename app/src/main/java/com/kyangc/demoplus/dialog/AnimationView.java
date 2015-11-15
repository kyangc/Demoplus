package com.kyangc.demoplus.dialog;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by chengkangyang on 十一月.12.2015
 */
public abstract class AnimationView {

    boolean isAnimationPrepared = false;

    boolean isCancelable = false;

    boolean isAnimationRunning = false;

    AnimatorSet mAnimatorSet;

    IAnimationListener mAnimationListener;

    abstract View bindView(LayoutInflater inflater, ViewGroup parent);

    abstract AnimationView prepareAnimation();

    public AnimationView setAnimatorListener(IAnimationListener listener) {
        this.mAnimationListener = listener;
        return this;
    }

    public void start() {
        mAnimatorSet.start();
    }

    public void pause() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (mAnimatorSet != null) {
                mAnimatorSet.pause();
            }
        }
    }

    public void cancel() {
        mAnimatorSet.cancel();
    }

    public boolean isCancelable() {
        return isCancelable;
    }

    public AnimationView setIsCancelable(boolean isCancelable) {
        this.isCancelable = isCancelable;
        return this;
    }

    public boolean isAnimationPrepared() {
        return isAnimationPrepared;
    }

    public boolean isAnimationRunning() {
        return isAnimationRunning;
    }

    public interface IAnimationListener {

        void onStart(Animator animation);

        void onEnd(Animator animation);

        void onCancel(Animator animation);

        void onRepeat(Animator animation);
    }
}
