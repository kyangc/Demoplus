package com.kyangc.demoplus.utils;

import com.kyangc.demoplus.R;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by chengkangyang on 七月.29.2015
 *
 * Snackbar 管理工具类
 */
public class S {

    private S() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static void show(Context context, View v, String hint, String click, boolean isLong,
            View.OnClickListener listener) {
        Snackbar.make(v, hint, isLong ? Snackbar.LENGTH_LONG : Snackbar.LENGTH_SHORT)
                .setActionTextColor(context.getResources().getColor(R.color.white))
                .setAction(click, listener)
                .show();
    }

    public static void showShort(Context context, View v, String hint, String click,
            View.OnClickListener listener) {
        Snackbar.make(v, hint, Snackbar.LENGTH_SHORT)
                .setActionTextColor(context.getResources().getColor(R.color.white))
                .setAction(click, listener)
                .show();
    }

    public static void showLong(Context context, View v, String hint, String click,
            View.OnClickListener listener) {
        Snackbar.make(v, hint, Snackbar.LENGTH_LONG)
                .setActionTextColor(context.getResources().getColor(R.color.white))
                .setAction(click, listener)
                .show();
    }

    public static void show(Context context, View v, String hint, View.OnClickListener listener) {
        Snackbar.make(v, hint, Snackbar.LENGTH_SHORT)
                .setActionTextColor(context.getResources().getColor(R.color.white))
                .setAction("Cancel", listener)
                .show();
    }
}
