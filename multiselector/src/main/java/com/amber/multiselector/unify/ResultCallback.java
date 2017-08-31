package com.amber.multiselector.unify;

import android.content.Context;
import android.widget.Toast;

import com.amber.multiselector.utils.ToastUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by luosiyi on 2017/6/23.
 */

public abstract class ResultCallback implements Serializable {


    public ResultCallback() {
    }

    /**
     * 检测图片合法性
     * @param context
     * @param results
     */
    public void detectionResults(Context context, List<String> results) {
        if (results == null || results.size() == 0) {
            ToastUtils.getInstance().showToast("没有获取到有效图片!");
        } else {
            results(results);
        }
    }

    public abstract void results(List<String> results);
}
