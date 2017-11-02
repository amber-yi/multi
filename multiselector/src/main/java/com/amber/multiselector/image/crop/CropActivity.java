package com.amber.multiselector.image.crop;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amber.multiselector.R;
import com.amber.multiselector.image.bean.CropBean;
import com.amber.multiselector.utils.StatusBarUtil;


/**
 * Created by luosiyi on 2017/7/5.
 */

public class CropActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView ivBack;

    TextView tvTitle;

    Button btnCommit;

    CropView mCropView;

    ImageView ivRotate;

    private CropBean cropBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_crop);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.multi_actionbar_color), 0);
        ivBack= (ImageView) findViewById(R.id.iv_back);
        tvTitle= (TextView) findViewById(R.id.tv_title);
        btnCommit= (Button) findViewById(R.id.commit);
        mCropView= (CropView) findViewById(R.id.mCropView);
        ivRotate= (ImageView) findViewById(R.id.iv_rotate);
        tvTitle.setText("图片裁剪");
        btnCommit.setText("完成");
        ivBack.setOnClickListener(this);
        btnCommit.setOnClickListener(this);
        ivRotate.setOnClickListener(this);
        Intent intent=getIntent();
        cropBean= intent.getParcelableExtra("crop");
        if(cropBean!=null){
            mCropView.setCropBean(cropBean);
        }

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.iv_back) {
            finish();
        } else if (i == R.id.commit) {
            String path = mCropView.cropToFile();
            Intent intent = new Intent();
            intent.putExtra("path", path);
            setResult(RESULT_OK, intent);
            finish();
        } else if (i == R.id.iv_rotate) {
            mCropView.setRotate(90);

        }
    }
}
