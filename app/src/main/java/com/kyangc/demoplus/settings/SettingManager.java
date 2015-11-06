package com.kyangc.demoplus.settings;

import com.kyangc.developkit.utils.SPUtils;

import android.content.Context;

/**
 * Created by chengkangyang on 七月.29.2015
 * <p/>
 * 设置管理者
 */
public class SettingManager {

    public static final String TAG = "SettingManager";

    public static boolean getIsFabShown(Context context) {
        return (boolean) SPUtils.get(context, Constants.IS_FAB_SHOWN, false);
    }

    public static void setIsFabShown(Context context, boolean isShown) {
        SPUtils.put(context, Constants.IS_FAB_SHOWN, isShown);
    }

    public static boolean getIsHttpsFirst(Context context) {
        return (boolean) SPUtils.get(context, Constants.IS_HTTP_FIRST, true);
    }

    public static void setIsHttpFirst(Context context, boolean isHttpFirst) {
        SPUtils.put(context, Constants.IS_HTTP_FIRST, isHttpFirst);
    }
}
