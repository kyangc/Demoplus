package com.kyangc.demoplus.dialog;

import com.kyangc.demoplus.R;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by chengkangyang on 十一月.12.2015
 */
public class DuitangShopAnimatedView extends DialogAnimationView {

    /**
     * Animation configs
     */
    private static int LOGO_WIDTH = 50;//dp

    private static int LOGO_HEIGHT = 50;//dp

    private static float LOGO_SCALE = 1.2f;

    private static long DURATION_PULSE = 800;//ms

    private static long DURATION_SCALE = 420;//ms

    private static long DURATION_BACKGROUND_DISMISS = 80;//ms

    private static long DURATION_DISMISS = 420;//ms

    /**
     * View
     */
    ImageView ivLogo;

    FrameLayout flLogoBackground;

    RelativeLayout rlContainer;

    public DuitangShopAnimatedView(DialogFragment fragment) {
        super(fragment);
    }

    @Override
    public View bindView(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.dialog_animated_progress, parent);
        flLogoBackground = (FrameLayout) view.findViewById(R.id.flLogoBackground);
        rlContainer = (RelativeLayout) view.findViewById(R.id.rlContainer);
        ivLogo = (ImageView) view.findViewById(R.id.ivLogo);
        ivLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAnimatorSet != null) {
                    mAnimatorSet.cancel();
                }
            }
        });
        return view;
    }

    @Override
    public DuitangShopAnimatedView prepareAnimation() {
        //logo background pulse y
        ObjectAnimator logoBackgroundScaleY = ObjectAnimator
                .ofFloat(flLogoBackground, View.SCALE_Y, 1f, LOGO_SCALE, 1f);
        logoBackgroundScaleY.setDuration(DURATION_PULSE)
                .setInterpolator(new AccelerateDecelerateInterpolator());
        logoBackgroundScaleY.setRepeatCount(1);
        logoBackgroundScaleY.setRepeatMode(ValueAnimator.RESTART);

        //logo background pulse x
        ObjectAnimator logoBackgroundScaleX = ObjectAnimator
                .ofFloat(flLogoBackground, View.SCALE_X, 1f, LOGO_SCALE, 1f);
        logoBackgroundScaleX.setDuration(DURATION_PULSE)
                .setInterpolator(new AccelerateDecelerateInterpolator());
        logoBackgroundScaleX.setRepeatCount(1);
        logoBackgroundScaleX.setRepeatMode(ValueAnimator.RESTART);

        //logo pulse y
        ObjectAnimator logoScaleY = ObjectAnimator
                .ofFloat(ivLogo, View.SCALE_Y, 1f, LOGO_SCALE, 1f);
        logoScaleY.setDuration(DURATION_PULSE)
                .setInterpolator(new AccelerateDecelerateInterpolator());
        logoScaleY.setRepeatCount(1);
        logoScaleY.setRepeatMode(ValueAnimator.RESTART);

        //logo pulse x
        ObjectAnimator logoScaleX = ObjectAnimator
                .ofFloat(ivLogo, View.SCALE_X, 1f, LOGO_SCALE, 1f);
        logoScaleX.setDuration(DURATION_PULSE)
                .setInterpolator(new AccelerateDecelerateInterpolator());
        logoScaleX.setRepeatCount(1);
        logoScaleX.setRepeatMode(ValueAnimator.RESTART);

        //icon dismiss
        ObjectAnimator iconExit = ObjectAnimator.ofFloat(ivLogo, View.ALPHA, 1f, 0f);
        iconExit.setDuration(DURATION_DISMISS);

        //background scale x
        ObjectAnimator scaleLargeX = ObjectAnimator
                .ofFloat(flLogoBackground, View.SCALE_X, 1f, 15f);
        scaleLargeX.setDuration(DURATION_SCALE).setInterpolator(new AccelerateInterpolator());

        //background scale y
        ObjectAnimator scaleLargeY = ObjectAnimator
                .ofFloat(flLogoBackground, View.SCALE_Y, 1f, 15f);
        scaleLargeY.setDuration(DURATION_SCALE).setInterpolator(new AccelerateInterpolator());

        //background alpha change
        ObjectAnimator backgroundAlpha = ObjectAnimator
                .ofFloat(flLogoBackground, View.ALPHA, 1f, 1f);
        backgroundAlpha.setDuration(DURATION_BACKGROUND_DISMISS);

        //Animator set
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.play(logoScaleX).with(logoScaleY).with(logoBackgroundScaleX)
                .with(logoBackgroundScaleY);
        mAnimatorSet.play(scaleLargeX).with(scaleLargeY).with(iconExit).after(logoScaleX);
        mAnimatorSet.play(backgroundAlpha).after(scaleLargeX);
        mAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimationRunning = true;
                if (mAnimationListener != null) {
                    mAnimationListener.onStart(animation);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimationRunning = false;
                if (mAnimationListener != null) {
                    mAnimationListener.onEnd(animation);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isAnimationRunning = false;
                if (mAnimationListener != null) {
                    mAnimationListener.onCancel(animation);
                }
            }
        });
        isAnimationPrepared = true;
        return this;
    }
}
