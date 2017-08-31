package com.amber.multiselector.image;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amber.multiselector.R;
import com.amber.multiselector.image.bean.CropBean;
import com.amber.multiselector.image.crop.CropActivity;
import com.amber.multiselector.unify.ResultCallback;
import com.amber.multiselector.utils.FileSizeUtil;
import com.amber.multiselector.utils.FileUtils;
import com.amber.multiselector.utils.StatusBarUtil;
import com.amber.multiselector.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luosiyi on 2017/6/21.
 */

public class MultiImageSelectorActivity extends AppCompatActivity
        implements MultiImageSelectorFragment.Callback {
    /**
     * 图片选择的回调
     */
    public static ResultCallback resultCallback;

    private CropBean crop;
    // Single choice
    public static final int MODE_SINGLE = 0;
    // Multi choice
    public static final int MODE_MULTI = 1;
    /**
     * crop
     */
    public static final String EXTRA_SELECT_CROP = "crop";
    /**
     * Max image size，int，{@link #DEFAULT_IMAGE_SIZE} by default
     */
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    /**
     * Select mode，{@link #MODE_MULTI} by default
     */
    public static final String EXTRA_SELECT_MODE = "select_count_mode";
    /**
     * Whether show camera，true by default
     */
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    /**
     * Result data set，ArrayList&lt;String&gt;
     */
    public static final String EXTRA_RESULT = "select_result";
    /**
     * Original data set
     */
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_list";
    // Default image size
    private static final int DEFAULT_IMAGE_SIZE = 9;
    // go  crop  requestCode
    private static final int CROP_REQUEST = 200;

    private ArrayList<String> resultList = new ArrayList<>();
    private Button mSubmitButton;
    private ImageView mBack;
    private TextView mTitle;
    private int mDefaultCount = DEFAULT_IMAGE_SIZE;
    private Uri imageUri;
//    private static final String IMAGE_FILE_LOCATION = "file://" + FileUtils.getSDPath() + "temp.jpg";//temp file
//    private Uri tempUri = Uri.parse(IMAGE_FILE_LOCATION);//The Uri to store the big bitmap

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.MIS_NO_ACTIONBAR);
        setContentView(R.layout.multi_activity_default);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.multi_actionbar_color), 0);


        final Intent intent = getIntent();
        crop = intent.getParcelableExtra(EXTRA_SELECT_CROP);
        mDefaultCount = intent.getIntExtra(EXTRA_SELECT_COUNT, DEFAULT_IMAGE_SIZE);
        final int mode = intent.getIntExtra(EXTRA_SELECT_MODE, MODE_MULTI);
        final boolean isShow = intent.getBooleanExtra(EXTRA_SHOW_CAMERA, true);
        if (mode == MODE_MULTI && intent.hasExtra(EXTRA_DEFAULT_SELECTED_LIST)) {
            resultList = intent.getStringArrayListExtra(EXTRA_DEFAULT_SELECTED_LIST);
        }
        mBack = (ImageView) findViewById(R.id.iv_back);
        mTitle = (TextView) findViewById(R.id.tv_title);
        mSubmitButton = (Button) findViewById(R.id.commit);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mTitle.setText(R.string.multi_image_all);
        if (mode == MODE_MULTI) {
            updateDoneText(resultList);
            mSubmitButton.setVisibility(View.VISIBLE);
            mSubmitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (resultList != null && resultList.size() > 0) {
                        // Notify success
                        Intent data = new Intent();
                        data.putStringArrayListExtra(EXTRA_RESULT, resultList);
                        setResult(RESULT_OK, data);
                    } else {
                        setResult(RESULT_CANCELED);
                    }
                    finish();
                }
            });
        } else {
            mSubmitButton.setVisibility(View.GONE);
        }

        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_COUNT, mDefaultCount);
            bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_MODE, mode);
            bundle.putBoolean(MultiImageSelectorFragment.EXTRA_SHOW_CAMERA, isShow);
            bundle.putStringArrayList(MultiImageSelectorFragment.EXTRA_DEFAULT_SELECTED_LIST, resultList);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.image_grid, Fragment.instantiate(this, MultiImageSelectorFragment.class.getName(), bundle))
                    .commit();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Update done button by select image data
     *
     * @param resultList selected image data
     */
    private void updateDoneText(ArrayList<String> resultList) {
        int size = 0;
        if (resultList == null || resultList.size() <= 0) {
            mSubmitButton.setText(R.string.multi_action_done);
            mSubmitButton.setEnabled(false);
        } else {
            size = resultList.size();
            mSubmitButton.setEnabled(true);
            mSubmitButton.setText(getString(R.string.multi_action_button_string_, new Object[]{getString(R.string.multi_action_done), Integer.valueOf(size)}));

        }
//        mSubmitButton.setText(getString(R.string.multi_action_button_string,
//                getString(R.string.multi_action_done), size, mDefaultCount));
    }

    @Override
    public void onSingleImageSelected(String path) {
//        //过滤图片大小提示，也可以在查询的语句中增加判断，让不符合规格的图片不展示出来
//        double fileOrFilesSize = FileSizeUtil.getFileOrFilesSize(path, FileSizeUtil.SIZETYPE_KB);
//        if (fileOrFilesSize <= 4) {
//            ToastUtils.getInstance().showToast("上传图片不能小于4kb");
//            return;
//        }
        if (crop != null) {
            goZoom(path, crop);
        } else {
            if (resultCallback != null) {
                resultList.add(path);
                resultCallback.detectionResults(MultiImageSelectorActivity.this, resultList);
            } else {
                Intent data = new Intent();
                resultList.add(path);
                data.putStringArrayListExtra(EXTRA_RESULT, resultList);
                setResult(RESULT_OK, data);
            }
            finish();
        }

    }

    @Override
    public void onImageSelected(String path) {
        if (!resultList.contains(path)) {
            resultList.add(path);
        }
        updateDoneText(resultList);
    }

    @Override
    public void onImageUnselected(String path) {
        if (resultList.contains(path)) {
            resultList.remove(path);
        }
        updateDoneText(resultList);
    }

    @Override
    public void onCameraShot(File imageFile) {
        if (imageFile != null) {
            // notify system the image has change
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile)));
            resultList.add(imageFile.getAbsolutePath());
            if (crop != null) {
                goZoom(imageFile, crop);
            } else {

                if (resultCallback != null) {
                    resultCallback.detectionResults(MultiImageSelectorActivity.this, resultList);
                } else {
                    Intent data = new Intent();
                    data.putStringArrayListExtra(EXTRA_RESULT, resultList);
                    setResult(RESULT_OK, data);
                }
                finish();
            }

        }
    }
    private void goZoom(String path, CropBean crop) {
        goZoom(new File(path), crop);
    }
    private void goZoom(File file, CropBean crop) {
        imageUri = Uri.fromFile(file);
        crop.setmUri(imageUri);
        Intent intent = new Intent(this, CropActivity.class);
        intent.putExtra("crop", crop);
        startActivityForResult(intent, CROP_REQUEST);

    }
    @Override
    public void onFolderChange(String folderName) {
        mTitle.setText(folderName);
    }

    @Override
    public void finish() {
        super.finish();
        if (resultCallback != null)
            resultCallback = null;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CROP_REQUEST) {
            if (resultCode == RESULT_OK) {
                // 拿到剪切数据
//                Bitmap bmap = data.getParcelableExtra("data");
                List<String> list = new ArrayList<>();
                list.add(data.getStringExtra("path"));
//                list.add(UriToPathUtil.getRealFilePath(this, tempUri));
                if (resultCallback != null) {
                    resultCallback.detectionResults(MultiImageSelectorActivity.this, list);
                }else {
                    Intent intent = new Intent();
                    resultList.add(data.getStringExtra("path"));
                    intent.putStringArrayListExtra(EXTRA_RESULT, resultList);
                    setResult(RESULT_OK, intent);
                }
                finish();
            }
        }

    }
}
