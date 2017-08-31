package com.amber.toastlibraies.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by luosiyi on 2017/8/17.
 */

public class ToastView extends View{

    public ToastView(Context context) {
        this(context,null);
    }

    public ToastView(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public ToastView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }
    private void initView(){

    }
}
