package com.amber.multiselector.image.bean;

import android.text.TextUtils;

import java.util.List;

/**
 * Created by luosiyi on 2017/6/21.
 */

public class ImageFolder {
    public String name;
    public String path;
    public ImageInfo cover;
    public List<ImageInfo> images;

    @Override
    public boolean equals(Object o) {
        try {
            ImageFolder other = (ImageFolder) o;
            return TextUtils.equals(other.path, path);
        }catch (ClassCastException e){
            e.printStackTrace();
        }
        return super.equals(o);
    }
}
