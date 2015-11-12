package com.kyangc.demoplus.dialog;

import com.kyangc.demoplus.R;
import com.kyangc.developkit.utils.ScreenUtils;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

/**
 * Created by chengkangyang on 十一月.04.2015
 */
public class AnimatedProgressDialog extends DialogFragment {

    /**
     * Views
     */
    Window wWindow;

    DialogAnimationView mAnimationView;

    DialogAnimationView.IInteractionListener mInteractionListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AnimatedDialog);
        setCancelable(mAnimationView.isCancelable());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return mAnimationView.bindView(inflater, container);
    }

    @Override
    public void onStart() {
        super.onStart();

        //Prepare canvas as full screen
        wWindow = getDialog().getWindow();
        wWindow.setLayout(ScreenUtils.getScreenWidth(getActivity()),
                ScreenUtils.getScreenHeight(getActivity()) - ScreenUtils.getStatusHeight(
                        getActivity()));
        wWindow.setGravity(Gravity.CENTER);

        //Prepare animation
        if (!mAnimationView.isAnimationPrepared()) {
            mAnimationView
                    .setInteractionListener(mInteractionListener)
                    .prepare();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mAnimationView.isAnimationRunning()) {
            mAnimationView.start();
        }
    }

    public AnimatedProgressDialog setAnimationView(
            DialogAnimationView animationView) {
        mAnimationView = animationView;
        return this;
    }

    public AnimatedProgressDialog setInteractionListener(
            DialogAnimationView.IInteractionListener interactionListener) {
        mInteractionListener = interactionListener;
        return this;
    }
}
