package com.amber.multiselector.image.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amber.multiselector.R;
import com.amber.multiselector.image.bean.ImageFolder;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luosiyi on 2017/6/21.
 */

public class FolderAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;

    private List<ImageFolder> mFolders = new ArrayList<>();

    int mImageSize;

    int lastSelected = 0;

    public FolderAdapter(Context context){
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mImageSize = mContext.getResources().getDimensionPixelOffset(R.dimen.multi_folder_cover_size);
    }

    /**
     * 设置数据集
     * @param folders
     */
    public void setData(List<ImageFolder> folders) {
        if(folders != null && folders.size()>0){
            mFolders = folders;
        }else{
            mFolders.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mFolders.size()+1;
    }

    @Override
    public ImageFolder getItem(int i) {
        if(i == 0) return null;
        return mFolders.get(i-1);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view == null){
            view = mInflater.inflate(R.layout.multi_list_item_folder, viewGroup, false);
            holder = new ViewHolder(view);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        if (holder != null) {
            if(i == 0){
                holder.name.setText(R.string.multi_image_all);
                holder.path.setText(R.string.multi_sd_path);
                holder.size.setText(String.format("%d%s",
                        getTotalImageSize(), mContext.getResources().getString(R.string.multi_image_unit)));
                if(mFolders.size()>0){
                    ImageFolder f = mFolders.get(0);
                    if (f != null) {
                        Glide.with(mContext)
                                .load(new File(f.cover.path))
                                .error(R.drawable.mis_default_error)
                                .centerCrop()
                                .into(holder.cover);
                    }else{
                        holder.cover.setImageResource(R.drawable.mis_default_error);
                    }
                }
            }else {
                holder.bindData(getItem(i));
            }
            if(lastSelected == i){
                holder.indicator.setVisibility(View.VISIBLE);
            }else{
                holder.indicator.setVisibility(View.INVISIBLE);
            }
        }
        return view;
    }

    private int getTotalImageSize(){
        int result = 0;
        if(mFolders != null && mFolders.size()>0){
            for (ImageFolder f: mFolders){
                result += f.images.size();
            }
        }
        return result;
    }

    public void setSelectIndex(int i) {
        if(lastSelected == i) return;

        lastSelected = i;
        notifyDataSetChanged();
    }

    public int getSelectIndex(){
        return lastSelected;
    }

    class ViewHolder{
        ImageView cover;
        TextView name;
        TextView path;
        TextView size;
        ImageView indicator;
        ViewHolder(View view){
            cover = (ImageView)view.findViewById(R.id.cover);
            name = (TextView) view.findViewById(R.id.name);
            path = (TextView) view.findViewById(R.id.path);
            size = (TextView) view.findViewById(R.id.size);
            indicator = (ImageView) view.findViewById(R.id.indicator);
            view.setTag(this);
        }

        void bindData(ImageFolder data) {
            if(data == null){
                return;
            }
            name.setText(data.name);
            path.setText(data.path);
            if (data.images != null) {
                size.setText(String.format("%d%s", data.images.size(), mContext.getResources().getString(R.string.multi_image_unit)));
            }else{
                size.setText("*"+mContext.getResources().getString(R.string.multi_image_unit));
            }
            if (data.cover != null) {
                // 显示图片
                Glide.with(mContext)
                        .load(new File(data.cover.path))
                        .placeholder(R.drawable.mis_default_error)
                        .centerCrop()
                        .into(cover);

            }else{
                cover.setImageResource(R.drawable.mis_default_error);
            }
        }
    }

}
