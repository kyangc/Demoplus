package com.kyangc.demoplus.dialog;

import com.kyangc.demoplus.R;
import com.kyangc.developkit.utils.ScreenUtils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import rx.functions.Action1;

/**
 * Created by chengkangyang on 十一月.04.2015
 */
public class AnimatedProgressDialog extends DialogFragment {

    /**
     * Views
     */
    ImageView ivLogo;

    FrameLayout flLogoBackground;

    Window wWindow;

    /**
     * Data
     */
    boolean isAnimating = false;

    AnimatorSet mAnimatorSet;

    Action1<Boolean> mAction1;

    /**
     * Constants
     */
    private static int LOGO_WIDTH = 50;//dp

    private static int LOGO_HEIGHT = 50;//dp

    private static float LOGO_SCALE = 1.2f;

    private static long DURATION_PULSE = 800;//ms

    private static long DURATION_SCALE = 420;//ms

    private static long DURATION_BACKGROUND_DISMISS = 80;//ms

    private static long DURATION_DISMISS = 420;//ms

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AnimatedDialog);
        setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_animated_progress, container);
        flLogoBackground = (FrameLayout) view.findViewById(R.id.flLogoBackground);
        ivLogo = (ImageView) view.findViewById(R.id.ivLogo);
        ivLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        wWindow = getDialog().getWindow();
        wWindow.setLayout(ScreenUtils.getScreenWidth(getActivity()),
                ScreenUtils.getScreenHeight(getActivity()) - ScreenUtils.getStatusHeight(
                        getActivity()));
        wWindow.setGravity(Gravity.CENTER);
        if (mAnimatorSet == null) {
            prepareAnimations();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isAnimating) {
            mAnimatorSet.start();
        }
    }

    private void prepareAnimations() {
        //Animation elements

        //logo background pulse y
        ObjectAnimator logoBackgroundScaleY = ObjectAnimator
                .ofFloat(flLogoBackground, "scaleY", 1f, LOGO_SCALE, 1f);
        logoBackgroundScaleY.setDuration(DURATION_PULSE)
                .setInterpolator(new AccelerateDecelerateInterpolator());
        logoBackgroundScaleY.setRepeatCount(1);
        logoBackgroundScaleY.setRepeatMode(ValueAnimator.RESTART);

        //logo background pulse x
        ObjectAnimator logoBackgroundScaleX = ObjectAnimator
                .ofFloat(flLogoBackground, "scaleX", 1f, LOGO_SCALE, 1f);
        logoBackgroundScaleX.setDuration(DURATION_PULSE)
                .setInterpolator(new AccelerateDecelerateInterpolator());
        logoBackgroundScaleX.setRepeatCount(1);
        logoBackgroundScaleX.setRepeatMode(ValueAnimator.RESTART);

        //logo pulse y
        ObjectAnimator logoScaleY = ObjectAnimator
                .ofFloat(ivLogo, "scaleY", 1f, LOGO_SCALE, 1f);
        logoScaleY.setDuration(DURATION_PULSE)
                .setInterpolator(new AccelerateDecelerateInterpolator());
        logoScaleY.setRepeatCount(1);
        logoScaleY.setRepeatMode(ValueAnimator.RESTART);

        //logo pulse x
        ObjectAnimator logoScaleX = ObjectAnimator
                .ofFloat(ivLogo, "scaleX", 1f, LOGO_SCALE, 1f);
        logoScaleX.setDuration(DURATION_PULSE)
                .setInterpolator(new AccelerateDecelerateInterpolator());
        logoScaleX.setRepeatCount(1);
        logoScaleX.setRepeatMode(ValueAnimator.RESTART);

        //icon dismiss
        ObjectAnimator iconExit = ObjectAnimator.ofFloat(ivLogo, "alpha", 1f, 0f);
        iconExit.setDuration(DURATION_DISMISS);

        //background scale x
        ObjectAnimator scaleLargeX = ObjectAnimator.ofFloat(flLogoBackground, "scaleX", 1f, 15f);
        scaleLargeX.setDuration(DURATION_SCALE).setInterpolator(new AccelerateInterpolator());

        //background scale y
        ObjectAnimator scaleLargeY = ObjectAnimator.ofFloat(flLogoBackground, "scaleY", 1f, 15f);
        scaleLargeY.setDuration(DURATION_SCALE).setInterpolator(new AccelerateInterpolator());

        //background alpha change
        ObjectAnimator backgroundAlpha = ObjectAnimator
                .ofFloat(flLogoBackground, "alpha", 1f, 1f);
        backgroundAlpha.setDuration(DURATION_BACKGROUND_DISMISS);

        //Animator set
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.play(logoScaleX).with(logoScaleY).with(logoBackgroundScaleX)
                .with(logoBackgroundScaleY);
        mAnimatorSet.play(scaleLargeX).with(scaleLargeY).with(iconExit).after(logoScaleX);
        mAnimatorSet.play(backgroundAlpha).after(scaleLargeX);
        mAnimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimating = false;
                if (mAction1 != null) {
                    mAction1.call(true);
                }
                dismiss();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isAnimating = false;
                dismiss();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public AnimatedProgressDialog setDoNext(Action1<Boolean> action1) {
        mAction1 = action1;
        return this;
    }
}
