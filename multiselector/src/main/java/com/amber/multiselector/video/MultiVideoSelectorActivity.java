package com.amber.multiselector.video;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amber.multiselector.R;
import com.amber.multiselector.utils.StatusBarUtil;

import java.util.ArrayList;

/**
 * Created by luosiyi on 2017/6/20.
 */

public class MultiVideoSelectorActivity extends AppCompatActivity implements MultiVideoSelectorFragment.Callback {
    public static final int MODE_SINGLE = 0;
    public static final int MODE_MULTI = 1;
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    public static final String EXTRA_SELECT_MODE = "select_count_mode";
    public static final String EXTRA_RESULT = "select_result";
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_list";
    private static final int DEFAULT_IMAGE_SIZE = 9;
    private ArrayList<String> resultList = new ArrayList();
    private Button mSubmitButton;
    private ImageView mBack;
    private TextView mTitle;
    private int mDefaultCount = 3;

    public MultiVideoSelectorActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.MIS_NO_ACTIONBAR);
        setContentView(R.layout.multi_activity_default);
        StatusBarUtil.setColor(this,getResources().getColor(R.color.multi_actionbar_color),0);

        Intent intent = getIntent();
        mDefaultCount = intent.getIntExtra(EXTRA_SELECT_COUNT, DEFAULT_IMAGE_SIZE);
        int mode = intent.getIntExtra(EXTRA_SELECT_MODE, MODE_SINGLE);
        if(mode == MODE_MULTI && intent.hasExtra(EXTRA_DEFAULT_SELECTED_LIST)) {
            resultList = intent.getStringArrayListExtra(EXTRA_DEFAULT_SELECTED_LIST);
        }

        mSubmitButton = (Button)findViewById(R.id.commit);
        mBack= (ImageView) findViewById(R.id.iv_back);
        mTitle= (TextView) findViewById(R.id.tv_title);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mTitle.setText(R.string.multi_video_all);
        if(mode == MODE_MULTI) {
            updateDoneText(resultList);
            mSubmitButton.setVisibility(View.VISIBLE);
            mSubmitButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if(resultList != null && resultList.size() > 0) {
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

        if(savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putInt(EXTRA_SELECT_COUNT, mDefaultCount);
            bundle.putInt(EXTRA_SELECT_MODE, mode);
            bundle.putStringArrayList(EXTRA_DEFAULT_SELECTED_LIST, resultList);
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
            mSubmitButton.setText(getString(R.string.multi_action_button_string_, new Object[]{getString(R.string.multi_action_done), Integer.valueOf(size)}));

        } else {
            mSubmitButton.setText(R.string.multi_action_done);
            mSubmitButton.setEnabled(false);
        }

//        mSubmitButton.setText(getString(R.string.multi_action_button_string, new Object[]{getString(R.string.multi_action_done), Integer.valueOf(size), Integer.valueOf(mDefaultCount)}));

    }
    @Override
    public void onSingleVideoSelected(String path) {
        Intent data = new Intent();
        resultList.add(path);
        data.putStringArrayListExtra(EXTRA_RESULT, resultList);
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

    @Override
    public void onFolderChange(String folderName) {
        mTitle.setText(folderName);
    }
}
