package com.amber.multiselector.video.adapter;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amber.multiselector.R;
import com.amber.multiselector.utils.TimeUtils;
import com.amber.multiselector.video.bean.VideoInfo;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by luosiyi on 2017/6/20.
 */

public class VideoGridAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private boolean showSelectIndicator = true;
    private List<VideoInfo> mVideos = new ArrayList();
    private List<VideoInfo> mSelectedVideos = new ArrayList();
    final int mGridWidth;

    public VideoGridAdapter(Context context, int column) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        boolean width = false;
        int width1;
        if(Build.VERSION.SDK_INT >= 13) {
            Point size = new Point();
            wm.getDefaultDisplay().getSize(size);
            width1 = size.x;
        } else {
            width1 = wm.getDefaultDisplay().getWidth();
        }

        mGridWidth = width1 / column;
    }
    public void showSelectIndicator(boolean b) {
        showSelectIndicator = b;
    }
    public void select(VideoInfo video) {
        if(mSelectedVideos.contains(video)) {
            mSelectedVideos.remove(video);
        } else {
            mSelectedVideos.add(video);
        }
        notifyDataSetChanged();
    }
    public void setDefaultSelected(ArrayList<String> resultList) {
        Iterator var2 = resultList.iterator();

        while(var2.hasNext()) {
            String path = (String)var2.next();
            VideoInfo video = getVideoPath(path);
            if(video != null) {
                mSelectedVideos.add(video);
            }
        }

        if(mSelectedVideos.size() > 0) {
            notifyDataSetChanged();
        }

    }
    private VideoInfo getVideoPath(String path) {
        if(mVideos != null && mVideos.size() > 0) {
            Iterator var2 = mVideos.iterator();

            while(var2.hasNext()) {
                VideoInfo video = (VideoInfo)var2.next();
                if(video.getPath().equalsIgnoreCase(path)) {
                    return video;
                }
            }
        }
        return null;
    }
    public void setData(List<VideoInfo> videos) {
        mSelectedVideos.clear();
        if(videos != null && videos.size() > 0) {
            mVideos = videos;
        } else {
            mVideos.clear();
        }

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mVideos.size();
    }

    @Override
    public Object getItem(int i) {
        return mVideos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        VideoGridAdapter.ViewHolder holder;
        if(view == null) {
            view = mInflater.inflate(R.layout.video_item, viewGroup, false);
            holder = new VideoGridAdapter.ViewHolder(view);
        } else {
            holder = (VideoGridAdapter.ViewHolder)view.getTag();
        }

        if(holder != null) {
            holder.bindData((VideoInfo) getItem(i));
        }

        return view;
    }
    class ViewHolder {
        ImageView image;
        ImageView indicator;
        View mask;
        TextView duration;

        ViewHolder(View view) {
            image = (ImageView)view.findViewById(R.id.image);
            indicator = (ImageView)view.findViewById(R.id.checkmark);
            mask = view.findViewById(R.id.mask);
            duration = (TextView) view.findViewById(R.id.tv_duration);
            view.setTag(this);
        }

        void bindData(VideoInfo data) {
            if(data != null) {
                duration.setText(TimeUtils.longToString(data.getDuration(),"HH:mm:ss"));

                if(showSelectIndicator) {
                    indicator.setVisibility(View.VISIBLE);
                    if(mSelectedVideos.contains(data)) {
                        indicator.setImageResource(R.drawable.mis_btn_selected);
                        mask.setVisibility(View.VISIBLE);
                    } else {
                        indicator.setImageResource(R.drawable.mis_btn_unselected);
                        mask.setVisibility(View.GONE);
                    }
                } else {
                    indicator.setVisibility(View.GONE);
                }
                if(data.getThumbPath()!=null) {
                    File videoFile = new File(data.getThumbPath());
                    if (videoFile.exists()) {
                        Glide.with(mContext)
                                .load(videoFile).
                                placeholder(R.drawable.mis_default_error)
//                     .tag("MultiVideoSelectorFragment")
//                     .resize(mGridWidth, mGridWidth)
                                .centerCrop()
                                .into(image);
                    } else {
                        image.setImageResource(R.drawable.mis_default_error);
                    }
                }else {
                    image.setImageResource(R.drawable.mis_default_error);
                }

            }
        }
    }


}
