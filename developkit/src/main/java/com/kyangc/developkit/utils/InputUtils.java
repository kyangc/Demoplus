package com.kyangc.developkit.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.regex.Pattern;

/**
 * Created by chengkangyang on 十月.27.2015
 */
public class InputUtils {

    private static final String PATTERN_USER_NAME = "^\\w+$";

    private static final String PATTERN_EMAIL
            = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";

    private static final String PATTERN_MOBILE = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";

    public static boolean isValidUserName(String username) {
        if (TextUtils.isEmpty(username)) {
            return false;//为空
        }
        if (!username.trim().equals(username)) {
            return false;//前后有空格
        }
        if (username.length() < 6 || username.length() > 15) {
            return false;//长度不符合
        }
        Pattern p = Pattern.compile(PATTERN_USER_NAME);
        return p.matcher(username).matches();
    }

    public static boolean isValidEmailAddress(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        }
        Pattern p = Pattern.compile(PATTERN_EMAIL);//复杂匹配
        return p.matcher(email).matches();
    }

    public static boolean isValidPhoneNumber(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return false;
        }
        Pattern p = Pattern.compile(PATTERN_MOBILE);
        return p.matcher(phone).matches();
    }

    public static boolean isValidPassword(String password) {
        if (TextUtils.isEmpty(password)) {
            return false;
        }
        return !(password.length() < 6 || password.length() > 30);
    }

    /**
     * 打开软键盘
     */
    public static void openKeybord(EditText mEditText, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * 关闭软键盘
     */
    public static void closeKeybord(EditText mEditText, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    /**
     * 屏蔽复制、粘贴功能
     */
    public static void pasteUnable(EditText editText) {
        editText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
        editText.setLongClickable(false);
    }
}
