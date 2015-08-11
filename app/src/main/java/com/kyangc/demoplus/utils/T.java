package com.kyangc.demoplus.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by chengkangyang on 七月.29.2015
 * <p/>
 * Toast管理工具类
 */
public class T {

    private T() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static void showShort(Context context, CharSequence message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showShort(Context context, int message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showLong(Context context, CharSequence message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void showLong(Context context, int message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void show(Context context, CharSequence message, int duration) {
        Toast.makeText(context, message, duration).show();
    }

    public static void show(Context context, int message, int duration) {
        Toast.makeText(context, message, duration).show();
    }
}
