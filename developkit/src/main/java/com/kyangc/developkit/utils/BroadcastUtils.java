package com.kyangc.developkit.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by chengkangyang on 十月.08.2015
 */
public class BroadcastUtils {

    public static final String TAG = BroadcastUtils.class.getSimpleName();

    /**
     * Send local safe broadcast
     *
     * @param i intent that contains action
     */
    public static void sendLocal(Context context, Intent i) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(i);
    }

    /**
     * Register local broadcast receiver
     *
     * @param receiver broadcast receiver
     * @param filter   broadcast filter
     */
    public static void registerLocal(Context context, BroadcastReceiver receiver,
            IntentFilter filter) {
        LocalBroadcastManager.getInstance(context)
                .registerReceiver(receiver, filter);
    }

    /**
     * Unregister local broadcast receiver
     *
     * @param receiver broadcast receiver
     */
    public static void unregisterLocal(Context context, BroadcastReceiver receiver) {
        LocalBroadcastManager.getInstance(context)
                .unregisterReceiver(receiver);
    }

    /**
     * Send overall broadcast
     *
     * @param i Intent which contains action
     */
    public static void sendOverall(Context context, Intent i) {
        context.sendBroadcast(i);
    }

    /**
     * Register overall broadcast receiver
     *
     * @param receiver Broadcast receiver
     * @param filter   Broadcast filter
     */
    public static void registerOverall(Context context, BroadcastReceiver receiver,
            IntentFilter filter) {
        context.registerReceiver(receiver, filter);
    }

    /**
     * Unregister overall broadcast receiver
     *
     * @param receiver Broadcast receiver
     */
    public static void unregisterOverall(Context context, BroadcastReceiver receiver) {
        context.unregisterReceiver(receiver);
    }
}
