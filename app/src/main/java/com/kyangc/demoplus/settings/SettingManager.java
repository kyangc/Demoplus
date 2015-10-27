package com.kyangc.demoplus.settings;

import com.kyangc.developkit.utils.SPUtils;

/**
 * Created by chengkangyang on 七月.29.2015
 * <p/>
 * 设置管理者
 */
public class SettingManager {

    public static final String TAG = "SettingManager";

    public static boolean getIsFabShown() {
        return (boolean) SPUtils.get(Constants.IS_FAB_SHOWN, false);
    }

    public static void setIsFabShown(boolean isShown) {
        SPUtils.put(Constants.IS_FAB_SHOWN, isShown);
    }

    public static boolean getIsHttpsFirst() {
        return (boolean) SPUtils.get(Constants.IS_HTTP_FIRST, true);
    }

    public static void setIsHttpFirst(boolean isHttpFirst) {
        SPUtils.put(Constants.IS_HTTP_FIRST, isHttpFirst);
    }
}
