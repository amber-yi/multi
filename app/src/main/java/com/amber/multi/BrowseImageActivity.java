package com.amber.multi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;



import com.amber.multi.utils.GlideRoundImage;
import com.amber.multi.widget.PhotoViewPager;
import com.amber.multi.widget.magicindicator.MagicIndicator;
import com.amber.multi.widget.magicindicator.ViewPagerHelper;
import com.amber.multi.widget.magicindicator.buildins.circlenavigator.CircleNavigator;
import com.amber.multiselector.utils.ScreenUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.bluemobi.dylan.photoview.library.PhotoView;

/**
 * Created by luosiyi on 2017/8/12.
 */

public class BrowseImageActivity extends AppCompatActivity {

    @BindView(R.id.mViewPager)
    PhotoViewPager mViewPager;
    @BindView(R.id.mIndicator)
    MagicIndicator mIndicator;
    @BindView(R.id.iv_back)
    ImageView ivBack;

    private Unbinder unbinder;
    private List<String> urlList;
    private int currCount;
    private List<PhotoView> imageViewList = new ArrayList<>();


    public static void browseImage(Activity activity, ArrayList<String> images, int currCount) {
        Intent intent = new Intent(activity, BrowseImageActivity.class);
        intent.putStringArrayListExtra("urlList",  images);
        intent.putExtra("currCount", currCount);
        activity.startActivity(intent);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_browse_image);
        unbinder= ButterKnife.bind(this);
        Intent intent = getIntent();
        urlList = intent.getStringArrayListExtra("urlList");
        currCount = intent.getIntExtra("currCount", currCount);
        initView();
    }


    protected void initView() {
        int size = 0;
        if (urlList != null && urlList.size() != 0) {
            size = size + urlList.size();
            for (int i = 0; i < urlList.size(); i++) {
                imageViewList.add(createImageView(urlList.get(i)));
            }
        }
        MyPagerAdapter photoAdapter = new MyPagerAdapter(this, imageViewList);
        mViewPager.setAdapter(photoAdapter);
        initMagicIndicator1(size);
        mViewPager.setCurrentItem(currCount);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initMagicIndicator1(int vpCount) {
        CircleNavigator circleNavigator = new CircleNavigator(this);
        circleNavigator.setCircleCount(vpCount);
        circleNavigator.setCircleColor(getResources().getColor(R.color.indicator1_color));
        circleNavigator.setCircleStyle(Paint.Style.FILL);
        circleNavigator.setIndicator(Color.RED);
        circleNavigator.setRadius(ScreenUtils.dip2px(this, (float) 5));
        circleNavigator.setCircleClickListener(new CircleNavigator.OnCircleClickListener() {
            @Override
            public void onClick(int index) {
                mViewPager.setCurrentItem(index);
            }
        });
        mIndicator.setNavigator(circleNavigator);
        ViewPagerHelper.bind(mIndicator, mViewPager);
    }

    private PhotoView createImageView(String url) {
        PhotoView imageView = new PhotoView(this);
//        ViewPager.LayoutParams params = new ViewPager.LayoutParams();
//        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setAdjustViewBounds(true);
        Glide.with(this).load(url)
                .transform(new GlideRoundImage(this))
                .error(R.mipmap.placeholder)
                .into(imageView);
        return imageView;
    }


    class MyPagerAdapter extends PagerAdapter {
        private Context mContext;
        private List<PhotoView> ivList; // ImageView的集合
        private int count = 1;

        public MyPagerAdapter(Context context, List<PhotoView> ivList) {
            super();
            this.mContext = context;
            this.ivList = ivList;
            if (ivList != null && ivList.size() > 0) {
                count = ivList.size();
            }
        }

        @Override
        public int getCount() {

            return count;

        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            int newPosition = position % count;
            // 先移除在添加，更新图片在container中的位置（把iv放至container末尾）
            PhotoView iv = ivList.get(newPosition);
            container.removeView(iv);
            container.addView(iv);
            return iv;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
