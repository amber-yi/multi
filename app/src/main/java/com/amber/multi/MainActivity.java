package com.amber.multi;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.amber.multi.adapter.PhotoAdapter;
import com.amber.multi.utils.SpaceItemDecoration;
import com.amber.multi.widget.FullyGridLayoutManager;
import com.amber.multiselector.image.MultiImageSelector;
import com.amber.multiselector.image.MultiImageSelectorActivity;
import com.amber.multiselector.image.bean.CropBean;
import com.amber.multiselector.video.MultiVideoSelector;
import com.amber.multiselector.video.MultiVideoSelectorActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {

    private boolean showImage = true;
    private static final int REQUEST_IMAGE = 2;
    private static final int REQUEST_VIDEO = 1;
    protected static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    Unbinder unbinder;
    private ArrayList<String> mImageSelectPath;
    private ArrayList<String> mVideoSelectPath;
    private int cropModel = 0;

    @BindView(R.id.rg_mode)
    RadioGroup rgMode;
    @BindView(R.id.tv_max_count)
    TextView tvMaxCount;
    @BindView(R.id.et_max_count)
    EditText etMaxCount;
    @BindView(R.id.rg_camera)
    RadioGroup rgCamera;
    @BindView(R.id.tv_crop)
    TextView tvCrop;
    @BindView(R.id.rg_crop)
    RadioGroup rgCrop;
    @BindView(R.id.btn_show_image)
    TextView btnShowImage;
    @BindView(R.id.btn_show_video)
    TextView btnShowVideo;
    @BindView(R.id.tv_result)
    TextView tvResult;
    @BindView(R.id.lv_result)
    RecyclerView lv_result;

    @OnClick(R.id.btn_show_image)
    public void submit(View view) {
        showImage = true;
        pick();
    }

    @OnClick(R.id.btn_show_video)
    public void submit2(View view) {
        showImage = false;
        pick();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        rgMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if(i==R.id.rb_single){
                    tvMaxCount.setVisibility(View.GONE);
                    etMaxCount.setVisibility(View.GONE);
                    tvCrop.setVisibility(View.VISIBLE);
                    rgCrop.setVisibility(View.VISIBLE);
                }else {
                    tvMaxCount.setVisibility(View.VISIBLE);
                    etMaxCount.setVisibility(View.VISIBLE);
                    tvCrop.setVisibility(View.GONE);
                    rgCrop.setVisibility(View.GONE);
                }
            }
        });
        rgMode.check(R.id.rb_multi);
        rgCamera.check(R.id.rb_show_camera);
        rgCrop.check(R.id.rb_no_crop);
//        lv_result.setNestedScrollingEnabled(false);
//        FullyGridLayoutManager layoutManager=new  FullyGridLayoutManager(this, 2);
//        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
//        layoutManager.setSmoothScrollbarEnabled(true);
        lv_result.setHasFixedSize(true);
        lv_result.setNestedScrollingEnabled(false);
        lv_result.setLayoutManager(new GridLayoutManager(this,2));
        lv_result.addItemDecoration(new SpaceItemDecoration(this));
    }

    void pick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.permission_rationale),
                    REQUEST_STORAGE_READ_ACCESS_PERMISSION);
        } else {
            if (showImage) pickImage();
            else pickVideo();

        }
    }

    void pickImage() {
        boolean showCamera = rgCamera.getCheckedRadioButtonId() == R.id.rb_show_camera;
        int maxNum = 9;

        if (!TextUtils.isEmpty(etMaxCount.getText())) {
            try {
                maxNum = Integer.valueOf(etMaxCount.getText().toString());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        MultiImageSelector selector = MultiImageSelector.create();
        selector.showCamera(showCamera);
        selector.count(maxNum);
        if (rgMode.getCheckedRadioButtonId() == R.id.rb_single) {
            selector.single();
            if (rgCrop.getCheckedRadioButtonId() == R.id.rb_circle_crop) {
                selector.crop(CropBean.getCircleCrop());
            } else if (rgCrop.getCheckedRadioButtonId() == R.id.rb_square_crop) {
                selector.crop(CropBean.getSquareCrop());
            } else if (rgCrop.getCheckedRadioButtonId() == R.id.rb_rectangle_crop) {
                selector.crop(CropBean.getRectangleCrop());
            }
        } else {
            selector.multi();
        }
        selector.origin(mImageSelectPath);
        selector.start(MainActivity.this, REQUEST_IMAGE);
    }

    void pickVideo() {
        int maxNum = 3;
        if (!TextUtils.isEmpty(etMaxCount.getText())) {
            try {
                maxNum = Integer.valueOf(etMaxCount.getText().toString());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        MultiVideoSelector selector = MultiVideoSelector.create();
        selector.count(maxNum);
        if (rgMode.getCheckedRadioButtonId() == R.id.rb_single) {
            selector.single();
        } else {
            selector.multi();
        }
        selector.origin(mVideoSelectPath);
        selector.start(MainActivity.this, REQUEST_VIDEO);
    }

    private void requestPermission(final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.permission_dialog_title)
                    .setMessage(rationale)
                    .setPositiveButton(R.string.permission_dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
                        }
                    })
                    .setNegativeButton(R.string.permission_dialog_cancel, null)
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_STORAGE_READ_ACCESS_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pick();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                mImageSelectPath = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);
//                StringBuilder sb = new StringBuilder();
//                for (String p : mImageSelectPath) {
//                    sb.append(p);
//                    sb.append("\n");
//                }
//                tvResult.setText(sb.toString());
                tvResult.setText("");
                PhotoAdapter adapter=new PhotoAdapter(this,mImageSelectPath);
                lv_result.setAdapter(adapter);

            }
        } else if (requestCode == REQUEST_VIDEO) {
            if (resultCode == RESULT_OK) {
                mVideoSelectPath = data.getStringArrayListExtra(MultiVideoSelector.EXTRA_RESULT);
                StringBuilder sb = new StringBuilder();
                for (String p : mVideoSelectPath) {
                    sb.append(p);
                    sb.append("\n");
                }
                tvResult.setText(sb.toString());
                lv_result.setAdapter(null);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
