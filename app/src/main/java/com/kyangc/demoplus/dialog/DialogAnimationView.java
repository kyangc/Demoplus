package com.kyangc.demoplus.dialog;

import android.animation.Animator;
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
        setAnimatorListener(new AnimationListenerAdapter() {
            @Override
            public void onStart(Animator animation) {
                super.onStart(animation);
                if (listener != null) {
                    listener.onStart(fragment);
                }
            }

            @Override
            public void onEnd(Animator animation) {
                super.onEnd(animation);
                if (listener != null) {
                    listener.onEnd(fragment);
                }
            }

            @Override
            public void onCancel(Animator animation) {
                super.onCancel(animation);
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
