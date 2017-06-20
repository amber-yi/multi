package com.amber.multiselector.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.amber.multiselector.R;

/**
 * 比例FrameLayout
 * Created by luosiyi on 2017/6/20.
 */

public class ProportionFrameLayout extends FrameLayout {
    private float proportion = 1;//比例，默认正方形
    private boolean fixed_width = true;


    public ProportionFrameLayout(Context context, float proportion, boolean fixed_width) {
        super(context);
        this.proportion = proportion;
        this.fixed_width = fixed_width;
    }

    public ProportionFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ProportionFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.Proportion);
            proportion = array.getFloat(R.styleable.Proportion_proportion, 1);
            fixed_width = array.getBoolean(R.styleable.Proportion_fixed_width, true);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (fixed_width)
            setMeasuredDimension(getMeasuredWidth(), (int) (getMeasuredWidth() * proportion));
        else
            setMeasuredDimension((int) (getMeasuredHeight() * proportion), getMeasuredHeight());
    }

}
