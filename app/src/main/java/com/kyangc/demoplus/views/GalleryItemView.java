package com.kyangc.demoplus.views;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by YlorD on 15/9/29.
 *
 * @author yomnia
 */
public class GalleryItemView extends SimpleDraweeView {

    public GalleryItemView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
    }

    public GalleryItemView(Context context) {
        super(context);
    }

    public GalleryItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GalleryItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
