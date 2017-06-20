package com.amber.multiselector.video;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.amber.multiselector.R;

import java.util.ArrayList;

/**
 * Created by luosiyi on 2017/6/20.
 */

public class MultiVideoSelectorActivity extends AppCompatActivity implements MultiVideoSelectorFragment.Callback {
    public static final int MODE_SINGLE = 0;
    public static final int MODE_MULTI = 1;
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    public static final String EXTRA_SELECT_MODE = "select_count_mode";
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    public static final String EXTRA_RESULT = "select_result";
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_list";
    private static final int DEFAULT_IMAGE_SIZE = 9;
    private ArrayList<String> resultList = new ArrayList();
    private Button mSubmitButton;
    private int mDefaultCount = 9;

    public MultiVideoSelectorActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.MIS_NO_ACTIONBAR);
        setContentView(R.layout.multi_activity_default);
        if(Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.BLACK);

        }

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        if(toolbar != null) {
            setSupportActionBar(toolbar);
        }

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        mDefaultCount = intent.getIntExtra("max_select_count", 9);
        int mode = intent.getIntExtra("select_count_mode", 1);
        boolean isShow = intent.getBooleanExtra("show_camera", true);
        if(mode == 1 && intent.hasExtra("default_list")) {
            resultList = intent.getStringArrayListExtra("default_list");
        }

        mSubmitButton = (Button)findViewById(R.id.commit);
        if(mode == 1) {
            updateDoneText(resultList);
            mSubmitButton.setVisibility(View.VISIBLE);
            mSubmitButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if(resultList != null && resultList.size() > 0) {
                        Intent data = new Intent();
                        data.putStringArrayListExtra("select_result", resultList);
                        setResult(-1, data);
                    } else {
                        setResult(0);
                    }

                    finish();
                }
            });
        } else {
            mSubmitButton.setVisibility(View.GONE);
        }

        if(savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putInt("max_select_count", mDefaultCount);
            bundle.putInt("select_count_mode", mode);
            bundle.putBoolean("show_camera", isShow);
            bundle.putStringArrayList("default_list", resultList);
            getSupportFragmentManager().
                    beginTransaction().
                    add(R.id.image_grid, Fragment.instantiate(this, MultiVideoSelectorFragment.class.getName(), bundle))
                    .commit();
        }

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateDoneText(ArrayList<String> resultList) {
        int size = 0;
        if(resultList != null && resultList.size() > 0) {
            size = resultList.size();
            mSubmitButton.setEnabled(true);
        } else {
            mSubmitButton.setText(R.string.multi_action_done);
            mSubmitButton.setEnabled(false);
        }

        mSubmitButton.setText(getString(R.string.multi_action_button_string, new Object[]{getString(R.string.multi_action_done), Integer.valueOf(size), Integer.valueOf(mDefaultCount)}));
    }
    @Override
    public void onSingleVideoSelected(String path) {
        Intent data = new Intent();
        resultList.add(path);
        data.putStringArrayListExtra("select_result", resultList);
        setResult(-1, data);
        finish();
    }

    @Override
    public void onVideoSelected(String path) {
        if(!resultList.contains(path)) {
            resultList.add(path);
        }

        updateDoneText(resultList);


    }

    @Override
    public void onVideoUnselected(String path) {
        if(resultList.contains(path)) {
            resultList.remove(path);
        }

        updateDoneText(resultList);
    }
}
