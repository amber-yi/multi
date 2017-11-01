package com.amber.multiselector.image;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.amber.multiselector.R;
import com.amber.multiselector.image.bean.CropBean;
import com.amber.multiselector.unify.ResultCallback;

import java.util.ArrayList;

/**
 * 图片选择器
 * Created by luosiyi on 2017/6/21.
 */

public class MultiImageSelector {
    public static final String EXTRA_RESULT = MultiImageSelectorActivity.EXTRA_RESULT;//选择的结果的标志

    private static MultiImageSelector sSelector;

    private ResultCallback callback;//选择的回调，如果不设置回调则在onActivityResult里面获取选择的结果
    private CropBean crop;//裁剪的bean

    private boolean mShowCamera = true;//是否支持拍摄
    private int mMaxCount = 9;//最多选择多少图片
    private int mMode = MultiImageSelectorActivity.MODE_MULTI;//选择模式（默认多选）
    private ArrayList<String> mOriginData;//上次选择的结果


    private MultiImageSelector() {
    }

    /**
     * 实例化 并非单例
     *
     * @return
     */
    public static MultiImageSelector create() {
        sSelector = new MultiImageSelector();
        return sSelector;
    }

    /**
     * 是否支持拍摄
     *
     * @param show
     * @return
     */
    public MultiImageSelector showCamera(boolean show) {
        mShowCamera = show;
        return sSelector;
    }

    /**
     * 设置选择器的最大数量
     *
     * @param count
     * @return
     */
    public MultiImageSelector count(int count) {
        mMaxCount = count;
        return sSelector;
    }

    /**
     * 设置选择器为单选模式
     *
     * @return
     */
    public MultiImageSelector single() {
        mMode = MultiImageSelectorActivity.MODE_SINGLE;
        return sSelector;
    }

    /**
     * 设置选择器为多选模式
     *
     * @return
     */
    public MultiImageSelector multi() {
        mMode = MultiImageSelectorActivity.MODE_MULTI;
        return sSelector;
    }

    /**
     * 选择结果后的回调
     *
     * @param callback
     * @return
     */
    public MultiImageSelector callback(ResultCallback callback) {
        this.callback = callback;
        return sSelector;
    }

    /**
     * 设置上次的选择结果
     *
     * @param images
     * @return
     */
    public MultiImageSelector origin(ArrayList<String> images) {
        mOriginData = images;
        return sSelector;
    }

    /**
     * 设置裁剪区域参数
     * @param cropX 裁剪区域的宽
     * @param cropY 裁剪区域的高
     * @param outputX 图片输出的宽
     * @param outputY 图片输出的高
     * @param circleCrop 裁剪区域是否是圆形
     * @return
     */
    public MultiImageSelector crop(int cropX, int cropY, int outputX, int outputY, boolean circleCrop) {
        crop = new CropBean(cropX, cropY, outputX, outputY, circleCrop);
        return sSelector;
    }

    /**
     * 设置裁剪区域参数
     * @param cropBean
     * @return
     */
    public MultiImageSelector crop(CropBean cropBean) {
        crop = cropBean;
        return sSelector;
    }

    /**
     * 状态栏与标题栏颜色
     * @param color
     * @return
     */
    public MultiImageSelector titleColor(@ColorInt int color) {
        MultiImageSelectorActivity.TOP_COLOR = color;
        return sSelector;
    }

    /**
     * 开始开启选择器
     *
     * @param activity
     * @param requestCode
     */
    public void start(Activity activity, int requestCode) {
        final Context context = activity;
        if (hasPermission(context)) {
            activity.startActivityForResult(createIntent(context), requestCode);
        } else {
            Toast.makeText(context, R.string.multi_error_no_permission, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 开始开启选择器
     *
     * @param fragment
     * @param requestCode
     */
    public void start(Fragment fragment, int requestCode) {
        final Context context = fragment.getContext();
        if (hasPermission(context)) {
            fragment.startActivityForResult(createIntent(context), requestCode);
        } else {
            Toast.makeText(context, R.string.multi_error_no_permission, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 判断是否拥有权限
     *
     * @param context
     * @return
     */
    private boolean hasPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // Permission was added in API Level 16
            return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    /**
     * 创建跳转的intent
     *
     * @param context
     * @return
     */
    private Intent createIntent(Context context) {
        Intent intent = new Intent(context, MultiImageSelectorActivity.class);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, mShowCamera);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, mMaxCount);
        if (mOriginData != null) {
            intent.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mOriginData);
        }
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, mMode);
        if (callback != null) {
            MultiImageSelectorActivity.resultCallback = callback;
        }
        if (crop != null) {
            intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_CROP, crop);
        }
        return intent;
    }
}
