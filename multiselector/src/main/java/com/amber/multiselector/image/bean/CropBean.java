package com.amber.multiselector.image.bean;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.amber.multiselector.utils.MultiApp;
import com.amber.multiselector.utils.ScreenUtils;


/**
 * Created by luosiyi on 2017/6/23.
 */

public class CropBean implements Parcelable {

    /**
     * 裁剪区域的 长
     */
    public int aspectX;
    /**
     * 裁剪区域的 高
     */
    public int aspectY;
    /**
     * 图片输出的 长
     */
    public int outputX;
    /**
     * 图片输出的  高
     */
    public int outputY;
    /**
     * 裁剪区域是否是圆形
     */
    public boolean circleCrop;
    /**
     * 待裁剪图片的Uri
     */
    public Uri uri;


    private CropBean() {
    }

    /**
     * 获取自定义的crop
     *
     * @param aspectX
     * @param aspectY
     * @param outputX
     * @param outputY
     * @param circleCrop
     */
    public CropBean(int aspectX, int aspectY, int outputX, int outputY, boolean circleCrop) {
        this.aspectX = aspectX;
        this.aspectY = aspectY;
        this.outputX = outputX;
        this.outputY = outputY;
        this.circleCrop = circleCrop;
    }

    /**
     * 获取圆形的crop 半径为屏幕的1/3
     *
     * @return
     */
    public static CropBean getCircleCrop() {
        CropBean cropBean = new CropBean();
        int X = 2*ScreenUtils.getScreenSize(MultiApp.getMultiApp()).x / 3;
        cropBean.aspectX = X;
        cropBean.aspectY = X;
        cropBean.outputX = X;
        cropBean.outputY = X;
        cropBean.circleCrop = true;
        return cropBean;
    }

    /**
     * 获取正方形的裁剪区 边长为屏幕的宽度的2/3
     *
     * @return
     */
    public static CropBean getSquareCrop() {
        CropBean cropBean = new CropBean();
        int X = 2 * ScreenUtils.getScreenSize(MultiApp.getMultiApp()).x / 3;
        cropBean.aspectX = X;
        cropBean.aspectY = X;
        cropBean.outputX = X;
        cropBean.outputY = X;
        cropBean.circleCrop = false;
        return cropBean;
    }

    /**
     * 获取长方形的裁剪区 长为屏幕宽度的2/3，高度为长的2/3
     *
     * @return
     */
    public static CropBean getRectangleCrop() {
        CropBean cropBean = new CropBean();
        int X = 2 * ScreenUtils.getScreenSize(MultiApp.getMultiApp()).x / 3;
        int Y = 2 * X / 3;
        cropBean.aspectX = X;
        cropBean.aspectY = Y;
        cropBean.outputX = X;
        cropBean.outputY = Y;
        cropBean.circleCrop = false;
        return cropBean;
    }


    public int getAspectX() {
        return aspectX;
    }

    public void setAspectX(int aspectX) {

        this.aspectX = aspectX;
    }

    public int getAspectY() {
        return aspectY;
    }

    public void setAspectY(int aspectY) {
        this.aspectY = aspectY;
    }

    public int getOutputX() {
        return outputX;
    }

    public void setOutputX(int outputX) {
        this.outputX = outputX;
    }

    public int getOutputY() {
        return outputY;
    }

    public void setOutputY(int outputY) {
        this.outputY = outputY;
    }

    public Uri getUri() {
        return uri;
    }

    public void setmUri(Uri mUri) {
        this.uri = mUri;
    }

    public boolean isCircleCrop() {
        return circleCrop;
    }

    public void setCircleCrop(boolean circleCrop) {
        this.circleCrop = circleCrop;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.aspectX);
        dest.writeInt(this.aspectY);
        dest.writeInt(this.outputX);
        dest.writeInt(this.outputY);
        dest.writeByte(this.circleCrop ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.uri, flags);
    }

    protected CropBean(Parcel in) {
        this.aspectX = in.readInt();
        this.aspectY = in.readInt();
        this.outputX = in.readInt();
        this.outputY = in.readInt();
        this.circleCrop = in.readByte() != 0;
        this.uri = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Creator<CropBean> CREATOR = new Creator<CropBean>() {
        @Override
        public CropBean createFromParcel(Parcel source) {
            return new CropBean(source);
        }

        @Override
        public CropBean[] newArray(int size) {
            return new CropBean[size];
        }
    };
}
