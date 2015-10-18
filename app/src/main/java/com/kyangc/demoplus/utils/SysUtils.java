package com.kyangc.demoplus.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by chengkangyang on 十月.15.2015
 */
public class SysUtils {

    private static final int VERSION = android.os.Build.VERSION.SDK_INT;

    public static long getAvailableExternalStorageSize() {
        long size = 0;
        if (VERSION >= 18) {
            size = SDK18.getAvailableExternalStorageSize();
        } else if (Environment.getExternalStorageDirectory() != null) {
            try {
                StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
                size = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
        return size;
    }

    public static boolean getIsSDAvalable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    public static void sendMediaScanRequest(Context context) {
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri
                .parse("file://" + Environment.getExternalStorageDirectory())));
    }

    @TargetApi(11)
    private static class SDK11 {

        public static void setLayerType(View view, int layerType) {
            view.setLayerType(layerType, null);
        }
    }

    @TargetApi(16)
    private static class SDK16 {

        public static void postOnAnimation(View view, Runnable runnable) {
            view.postOnAnimation(runnable);
        }

        public static void setBackground(View view, Drawable background) {
            view.setBackground(background);
        }

        public static void removeGlobalLayoutListener(ViewTreeObserver observer,
                ViewTreeObserver.OnGlobalLayoutListener listener) {
            observer.removeOnGlobalLayoutListener(listener);
        }
    }

    @TargetApi(18)
    private static class SDK18 {

        public static long getAvailableExternalStorageSize() {
            long size = 0;
            if (Environment.getExternalStorageDirectory() != null) {
                StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
                size = stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
            }
            return size;
        }

        public static long getTotalExternalStorageSize() {
            long size = 0;
            if (Environment.getExternalStorageDirectory() != null) {
                StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
                size = stat.getFreeBlocksLong() * stat.getBlockSizeLong();
            }
            return size;
        }
    }
}
