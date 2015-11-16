package com.kyangc.demoplus.dialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DialogFragment;

/**
 * Created by chengkangyang on 十一月.12.2015
 */
public abstract class DialogAnimationView extends AnimationView {

    DialogFragment fragment;

    public DialogAnimationView(DialogFragment fragment) {
        this.fragment = fragment;
    }

    public DialogAnimationView setInteractionListener(final IInteractionListener listener) {
        setAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isAnimationRunning = true;
                if (listener != null) {
                    listener.onStart(fragment);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimationRunning = false;
                if (listener != null) {
                    listener.onEnd(fragment);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                isAnimationRunning = false;
                if (listener != null) {
                    listener.onCancel(fragment);
                }
            }
        });
        return this;
    }

    public interface IInteractionListener {

        void onCancel(DialogFragment dialog);

        void onEnd(DialogFragment dialog);

        void onStart(DialogFragment dialog);
    }
}
