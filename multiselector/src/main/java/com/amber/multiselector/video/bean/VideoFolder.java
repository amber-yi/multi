package com.amber.multiselector.video.bean;

import android.text.TextUtils;

import java.util.List;

/**
 * Created by luosiyi on 2017/6/20.
 */

public class VideoFolder {
    public String name;
    public String path;
    public VideoInfo video;
    public List<VideoInfo> videos;

    @Override
    public boolean equals(Object o) {
        try {
            VideoFolder other = (VideoFolder) o;
            return TextUtils.equals(other.path, path);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return super.equals(o);
    }
}
