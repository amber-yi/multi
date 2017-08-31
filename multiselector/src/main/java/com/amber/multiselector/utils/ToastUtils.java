package com.amber.multiselector.utils;

import android.support.annotation.StringRes;
import android.widget.Toast;

/**
 * Created by luosiyi on 2017/8/30.
 * 提示弹窗
 */

public class ToastUtils {
    private static ToastUtils utils;
    private Toast toast;

    private ToastUtils() {
    }

    /**
     * 单例模式 不将方法写成动态，是可能我会面会做一个从上往下的一个提示框，避免以后修改
     * @return
     */
    public static ToastUtils getInstance() {
        if (utils == null) {
            synchronized (ToastUtils.class) {
                if (utils == null) {
                    utils = new ToastUtils();
                }
            }
        }
        return utils;
    }

    public void showToast(String msg) {
        if (toast == null) {
            toast = Toast.makeText(MultiApp.getMultiApp(), msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    public void showToast(@StringRes int msgId) {
        if (toast == null) {
            toast = Toast.makeText(MultiApp.getMultiApp(), msgId, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msgId);
        }
        toast.show();
    }
}
