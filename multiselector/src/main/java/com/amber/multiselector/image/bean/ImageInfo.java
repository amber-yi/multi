package com.amber.multiselector.image.bean;

import android.text.TextUtils;

import com.amber.multiselector.unify.FileInfoUnify;


/**
 * 图片实体
 * Created by luosiyi on 2017/6/21.
 */

public class ImageInfo implements FileInfoUnify {
    public String path;
    public String name;
    public long time;

    public ImageInfo(String path, String name, long time){
        this.path = path;
        this.name = name;
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        try {
            ImageInfo other = (ImageInfo) o;
            return TextUtils.equals(this.path, other.path);
        }catch (ClassCastException e){
            e.printStackTrace();
        }
        return super.equals(o);
    }
}
