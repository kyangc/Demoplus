package com.kyangc.demoplus.views;

import com.facebook.drawee.view.SimpleDraweeView;

import android.content.Context;
import android.util.AttributeSet;

public class SquareImageView extends SimpleDraweeView {

    public SquareImageView(Context context) {
        this(context, null);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setAspectRatio(1);
    }
}
