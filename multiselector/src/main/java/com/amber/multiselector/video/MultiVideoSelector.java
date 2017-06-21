package com.amber.multiselector.video;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.amber.multiselector.R;

import java.util.ArrayList;

/**
 * 视频选择器
 * Created by luosiyi on 2017/6/21.
 */

public class MultiVideoSelector {

    public static final String EXTRA_RESULT = MultiVideoSelectorActivity.EXTRA_RESULT;
    private static MultiVideoSelector sSelector;

    private int mMaxCount = 9;
    private int mMode = MultiVideoSelectorActivity.MODE_SINGLE;
    private ArrayList<String> mOriginData;

    private MultiVideoSelector() {
    }

    public static MultiVideoSelector create() {
        if (sSelector == null) {
            sSelector = new MultiVideoSelector();
        }
        return sSelector;
    }

    public MultiVideoSelector count(int count) {
        mMaxCount = count;
        return sSelector;
    }

    public MultiVideoSelector single() {
        mMode = MultiVideoSelectorActivity.MODE_SINGLE;
        return sSelector;
    }

    public MultiVideoSelector multi() {
        mMode = MultiVideoSelectorActivity.MODE_MULTI;
        return sSelector;
    }

    public MultiVideoSelector origin(ArrayList<String> videos) {
        mOriginData = videos;
        return sSelector;
    }

    public void start(Activity activity, int requestCode) {
        final Context context = activity;
        if (hasPermission(context)) {
            activity.startActivityForResult(createIntent(context), requestCode);
        } else {
            Toast.makeText(context, R.string.multi_error_no_permission, Toast.LENGTH_SHORT).show();
        }
    }

    public void start(Fragment fragment, int requestCode) {
        final Context context = fragment.getContext();
        if (hasPermission(context)) {
            fragment.startActivityForResult(createIntent(context), requestCode);
        } else {
            Toast.makeText(context, R.string.multi_error_no_permission, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean hasPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // Permission was added in API Level 16
            return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private Intent createIntent(Context context) {
        Intent intent = new Intent(context, MultiVideoSelectorActivity.class);
        intent.putExtra(MultiVideoSelectorActivity.EXTRA_SELECT_COUNT, mMaxCount);
        if (mOriginData != null) {
            intent.putStringArrayListExtra(MultiVideoSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mOriginData);
        }
        intent.putExtra(MultiVideoSelectorActivity.EXTRA_SELECT_MODE, mMode);
        return intent;
    }
}
