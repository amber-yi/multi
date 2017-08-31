package com.amber.multi.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amber.multi.BrowseImageActivity;
import com.amber.multi.R;
import com.amber.multi.utils.GlideRoundImage;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by luosiyi on 2017/8/31.
 */

public class PhotoAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<String> list;

    public PhotoAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item, parent, false);
        return new ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.photo_item_iv)
        ImageView ivPhoto;

        @BindView(R.id.tv_photo_path)
        TextView tvPath;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }

        public void setData(final int position) {
            Glide.with(context).load(list.get(position))
                    .transform(new GlideRoundImage(context))
                    .error(R.mipmap.placeholder).into(ivPhoto);
            tvPath.setText(list.get(position));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BrowseImageActivity.browseImage((Activity) context,(ArrayList<String>)list,position);
                }
            });
        }
    }


}

