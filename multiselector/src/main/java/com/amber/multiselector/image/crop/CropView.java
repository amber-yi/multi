package com.amber.multiselector.image.crop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;


import com.amber.multiselector.R;
import com.amber.multiselector.image.bean.CropBean;
import com.amber.multiselector.utils.FileUtils;
import com.amber.multiselector.utils.ScreenUtils;
import com.amber.multiselector.utils.UriToPathUtil;

import java.math.BigDecimal;

/**
 * 裁剪 view
 * Created by luosiyi on 2017/7/5.
 */

public class CropView extends ImageView implements ScaleGestureDetector.OnScaleGestureListener {

    private static int mRadius = 200;
    public static final float SCALE_MAX = 5.0f;

    private Paint mPaint;
    private RectF shelterR;
    private RectF cropR;
    private RectF minR;

    private Bitmap mBitmap;
    private float initScale = 0.5f;

    private float mLastX;
    private float mLastY;

    private int lastPointerCount;


    private CropBean cropBean;

    private int cropWith = 0;
    private int cropHeight = 0;

    private float oldRotation = 0;


    /**
     * 缩放的手势检测
     */
    private ScaleGestureDetector mScaleGestureDetector = null;

    private GestureDetector mGestureDetector;

    private boolean isAutoScale;

    /**
     * 用于存放矩阵的9个值
     */
    private final float[] matrixValues = new float[9];
    private final Matrix mScaleMatrix = new Matrix();
    private boolean once = true;

    public CropView(Context context) {
        this(context, null);
    }

    public CropView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setScaleType(ScaleType.MATRIX);
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        mGestureDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        if (isAutoScale == true)
                            return true;
                        float x = e.getX();
                        float y = e.getY();
                        if (getScale() < SCALE_MAX) {
                            CropView.this.postDelayed(
                                    new AutoScaleRunnable(SCALE_MAX, x, y), 16);
                            isAutoScale = true;
                        } else {
                            CropView.this.postDelayed(
                                    new AutoScaleRunnable(initScale, x, y), 16);
                            isAutoScale = true;
                        }
                        return true;
                    }
                });
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(2);
        mPaint.setAntiAlias(true);
        mPaint.setColor(getResources().getColor(R.color.crop_background));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();


    }

    private float getScale() {
        mScaleMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }

    private float getTranslateX() {
        mScaleMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MTRANS_X];
    }

    private float getTranslateY() {
        mScaleMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MTRANS_Y];
    }
    private float getRightPosition() {
        Rect rectTemp = getDrawable().getBounds();
        mScaleMatrix.getValues(matrixValues);
        return (matrixValues[Matrix.MTRANS_X]+rectTemp.width()*matrixValues[Matrix.MSCALE_X]);
    }
    private float getBottomPosition() {
        Rect rectTemp = getDrawable().getBounds();
        mScaleMatrix.getValues(matrixValues);
        return (matrixValues[Matrix.MTRANS_Y]+rectTemp.height()*matrixValues[Matrix.MSCALE_X]);
    }
    private int getCurrHeight() {
        Rect rectTemp = getDrawable().getBounds();
        return rectTemp.height();
    }
    private int getCurrWidth() {
        Rect rectTemp = getDrawable().getBounds();
        return rectTemp.width();
    }
    /**
     * 整个阴影部分
     *
     * @return
     */
    private RectF getShelterRectF() {
        if (shelterR == null) {
            shelterR = new RectF(0, 0, getWidth(), getHeight());
        } else {
            shelterR.set(0, 0, getWidth(), getHeight());
        }
        return shelterR;
    }

    /**
     * 剪切部分
     *
     * @return
     */
    private void drawCircle(Canvas canvas, Paint paint) {

        if (cropR == null) {
            cropR = new RectF(getWidth() / 2 - mRadius, getHeight() / 2 - mRadius, (getWidth()) / 2 + cropWith, (getHeight()) / 2 + cropHeight);
        } else {
            cropR.set(getWidth() / 2 - mRadius, getHeight() / 2 - mRadius, (getWidth()) / 2 + cropWith, (getHeight()) / 2 + cropHeight);
        }
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, mRadius, paint);
        if (minR == null) {
            minR = new RectF(getWidth() / 2 - mRadius, getHeight() / 2 - mRadius, getWidth() / 2 + mRadius, getHeight() / 2 + mRadius);
        }
    }

    /**
     * 剪切部分
     *
     * @param canvas
     * @param paint
     */
    private void drawRect(Canvas canvas, Paint paint) {
        if (cropR == null) {
            cropR = new RectF((getWidth() - cropWith) / 2, (getHeight() - cropHeight) / 2, (getWidth() + cropWith) / 2, (getHeight() + cropHeight) / 2);
        } else {
            cropR.set((getWidth() - cropWith) / 2, (getHeight() - cropHeight) / 2, (getWidth() + cropWith) / 2, (getHeight() + cropHeight) / 2);
        }
        canvas.drawRect(cropR, paint);
        if (minR == null) {
            minR = new RectF((getWidth() - cropWith) / 2, (getHeight() - cropHeight) / 2, (getWidth() + cropWith) / 2, (getHeight() + cropHeight) / 2);
        }
    }

    public void setImageResource(int resourceId) {
        super.setImageResource(resourceId);
        mBitmap = BitmapFactory.decodeResource(getResources(), resourceId);
    }

    public void setImageBitmap(Bitmap bitmap) {
        super.setImageBitmap(bitmap);
        mBitmap = bitmap;
    }

    /**
     * 剪裁头像
     *
     * @return
     */
    public String cropToFile() {
        Bitmap bitmap = cropToBitmap();
        String oldPath = UriToPathUtil.getRealFilePath(getContext(), cropBean.getUri());
        String cropPath = FileUtils.saveBitmap(bitmap, "multi", System.currentTimeMillis() + "");
        return cropPath == null ? oldPath : cropPath;
    }

    public Bitmap cropToBitmap() {
        if (cropBean.getUri() == null) {
            throw new RuntimeException("资源Uri不能为null！！！");
        }
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        int dw = getDrawable().getIntrinsicWidth();
        int dh = getDrawable().getIntrinsicHeight();
        float x = getTranslateX() - (getWidth() - dw) / 2;
        float y = getTranslateY() - (getHeight() - dh) / 2;
//        float y = getTranslateY() - (dh- getHeight()) / 2;
        mBitmap = zoomBitmap(mBitmap);
        Bitmap target;
        if (cropBean.isCircleCrop()) {
            target = Bitmap.createBitmap(mRadius * 2, mRadius * 2, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(target);
//            canvas.drawColor(Color.WHITE);//奇怪的bug！！！！不添加这个，最终画的图片高度就不对！！！！！
            canvas.drawCircle(mRadius, mRadius, mRadius, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(mBitmap, -(dw / 2 - mRadius) + x, -(dh / 2 - mRadius) + y, paint);
        } else {
            target = Bitmap.createBitmap((int) cropR.width(), (int) cropR.height(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(target);
            canvas.drawRect(cropR, paint);
            canvas.drawColor(Color.WHITE);//奇怪的bug！！！！不添加这个，最终画的图片高度就不对！！！！！
//            canvas.drawColor(Color.parseColor("#17abe3"));
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            float ww = cropR.width();
            float hh = cropR.height();

            canvas.drawBitmap(mBitmap, -((dw - cropR.width()) / 2) + x, -((dh - cropR.height()) / 2) + y, paint);
        }

//        canvas.drawBitmap(mBitmap,- (dw / 2 - cropR.width()) + x ,- (dh / 2 - cropR.height()) + y, paint);
//        canvas.drawBitmap(mBitmap, -((dw - cropR.width()) / 2) + x, -((dh - cropR.height()) / 2) + y, paint);
        return target;
    }

    private Bitmap zoomBitmap(Bitmap bitmap) {
        int imageWidth = bitmap.getWidth();
        int imageHeight = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(getScale(), getScale());
        Bitmap imageBitmap = Bitmap.createBitmap(bitmap, 0, 0, imageWidth,
                imageHeight, matrix, true);
        return imageBitmap;
    }

    private class AutoScaleRunnable implements Runnable {
        static final float BIGGER = 1.07f;
        static final float SMALLER = 0.93f;
        private float mTargetScale;
        private float tmpScale;

        /**
         * 缩放的中心
         */
        private float x;
        private float y;

        /**
         * 传入目标缩放值，根据目标值与当前值，判断应该放大还是缩小
         *
         * @param targetScale
         */
        public AutoScaleRunnable(float targetScale, float x, float y) {
            this.mTargetScale = targetScale;
            this.x = x;
            this.y = y;
            if (getScale() < mTargetScale) {
                tmpScale = BIGGER;
            } else {
                tmpScale = SMALLER;
            }

        }

        @Override
        public void run() {
            // 进行缩放
            mScaleMatrix.postScale(tmpScale, tmpScale, x, y);
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);

            final float currentScale = getScale();
            // 如果值在合法范围内，继续缩放
            if (((tmpScale > 1f) && (currentScale < mTargetScale))
                    || ((tmpScale < 1f) && (mTargetScale < currentScale))) {
                CropView.this.postDelayed(this, 16);
            } else
            // 设置为目标的缩放比例
            {
                final float deltaScale = mTargetScale / currentScale;
                mScaleMatrix.postScale(deltaScale, deltaScale, x, y);
                checkBorderAndCenterWhenScale();
                setImageMatrix(mScaleMatrix);
                isAutoScale = false;
            }

        }
    }

    /**
     * 在缩放时，进行图片显示范围的控制
     */
    private void checkBorderAndCenterWhenScale() {

        RectF rect = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        // 如果宽或高大于屏幕，则控制范围
        if (rect.width() >= width) {
            if (rect.left > 0) {
                deltaX = -rect.left;
            }
            if (rect.right < width) {
                deltaX = width - rect.right;
            }
        }
        if (rect.height() >= height) {
            if (rect.top > 0) {
                deltaY = -rect.top;
            }
            if (rect.bottom < height) {
                deltaY = height - rect.bottom;
            }
        }
        // 如果宽或高小于屏幕，则让其居中
        if (rect.width() < width) {
            deltaX = width * 0.5f - rect.right + 0.5f * rect.width();
        }
        if (rect.height() < height) {
            deltaY = height * 0.5f - rect.bottom + 0.5f * rect.height();
        }
        mScaleMatrix.postTranslate(deltaX, deltaY);
    }

    /**
     * 根据当前图片的Matrix获得图片的范围
     *
     * @return
     */
    private RectF getMatrixRectF() {
        Matrix matrix = mScaleMatrix;
        RectF rect = new RectF();
        Drawable d = getDrawable();
        if (null != d) {
            rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrix.mapRect(rect);
        }
        return rect;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getDrawable() == null||cropBean==null) {
            return;
        }
        getShelterRectF();
        // 画入前景圆形蒙板层
        int sc = canvas.saveLayer(shelterR, null, Canvas.MATRIX_SAVE_FLAG
                | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
                | Canvas.FULL_COLOR_LAYER_SAVE_FLAG
                | Canvas.CLIP_TO_LAYER_SAVE_FLAG | Canvas.ALL_SAVE_FLAG);
        canvas.drawRect(shelterR, mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        mPaint.setColor(Color.WHITE);
        if (cropBean.isCircleCrop())
            drawCircle(canvas, mPaint);
        else
            drawRect(canvas, mPaint);


        canvas.restoreToCount(sc);
        mPaint.setXfermode(null);
        mPaint.setColor(getResources().getColor(R.color.crop_background));
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event))
            return true;
        mScaleGestureDetector.onTouchEvent(event);
        float x = 0, y = 0;
        // 拿到触摸点的个数
        final int pointerCount = event.getPointerCount();
        // 得到多个触摸点的x与y均值
        for (int i = 0; i < pointerCount; i++) {
            x += event.getX(i);
            y += event.getY(i);
        }
        x = x / pointerCount;
        y = y / pointerCount;
        /**
         * 每当触摸点发生变化时，重置mLasX , mLastY
         */
        if (pointerCount != lastPointerCount) {
            mLastX = x;
            mLastY = y;
        }
        lastPointerCount = pointerCount;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = x - mLastX;
                float dy = y - mLastY;


                if (getTranslateX() + dx >= minR.left || getRightPosition() + dx <= minR.right) {
                    dx = 0;
                }
                if (getTranslateY() + dy > minR.top || getBottomPosition() + dy <= minR.bottom) {
                    dy = 0;
                }

                mScaleMatrix.postTranslate(dx, dy);
                setImageMatrix(mScaleMatrix);
                mLastX = x;
                mLastY = y;
                postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                lastPointerCount = 0;
                break;
        }
        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scale = getScale();
        float scaleFactor = detector.getScaleFactor();

        if (getDrawable() == null)
            return true;

        /**
         * 缩放的范围控制
         */
        if ((scale < SCALE_MAX && scaleFactor > 1.0f)
                || (scale > initScale && scaleFactor < 1.0f)) {
            /**
             * 最大值最小值判断
             */
            if (scaleFactor * scale < initScale) {
                scaleFactor = initScale / scale;
            }
            if (scaleFactor * scale > SCALE_MAX) {
                scaleFactor = SCALE_MAX / scale;
            }
            /**
             * 设置缩放比例
             */
            mScaleMatrix.postScale(scaleFactor, scaleFactor,
                    detector.getFocusX(), detector.getFocusX());
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);
        }
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    public void setCropBean(CropBean cropBean) {
        this.cropBean = cropBean;
        Point point = ScreenUtils.getScreenSize(getContext());
        mBitmap = FileUtils.convertToBitmap(UriToPathUtil.getRealFilePath(getContext(), cropBean.getUri()), point.x, point.y);
        setImageBitmap(mBitmap);
        postDelayed(new AutoScaleRunnable(1, point.x / 2, -point.y / 2), 16);
        if(cropBean.circleCrop){
            if (cropBean.getAspectX() < 20)
                mRadius = point.x / 3;
            else
                mRadius = cropBean.getAspectX()/2;
            float xScale=div(mRadius*2,mBitmap.getWidth(),4);
            float yScale=div(mRadius*2,mBitmap.getHeight(),4);
            initScale=xScale>yScale?xScale:yScale;
        }else {
            if (cropBean.getAspectX() < 20) {
                if (cropBean.getAspectX() < cropBean.getAspectY()) {
                    cropHeight = 2 * point.y / 3;
                    cropWith = cropHeight * cropBean.getAspectX() / cropBean.getAspectY();
                } else {
                    cropWith = 3 * point.x / 4;
                    cropHeight = cropWith * cropBean.getAspectY() / cropBean.getAspectX();
                }
            } else {
                cropWith = cropBean.getAspectX();
                cropHeight = cropWith * cropBean.getAspectY() / cropBean.getAspectX();
            }
            float xScale=div(cropWith,mBitmap.getWidth(),4);
            float yScale=div(cropHeight,mBitmap.getHeight(),4);
            initScale=xScale>yScale?xScale:yScale;
        }

    }

    private float dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (dpValue * scale + 0.5f);
    }

    // 取旋转角度
    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    public void setRotate(int angle) {
        mScaleMatrix.postRotate(angle, getWidth() / 2, getHeight() / 2);// 旋转
        setImageMatrix(mScaleMatrix);
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     * @param v1 被除数
     * @param v2 除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static float div(float v1, float v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Float.toString(v1));
        BigDecimal b2 = new BigDecimal(Float.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).floatValue();
    }
}
