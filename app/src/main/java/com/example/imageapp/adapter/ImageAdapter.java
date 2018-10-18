package com.example.imageapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.imageapp.model.ImageBean;
import com.example.imageapp.R;
import com.example.imageapp.view.SquareImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bohan Li on 14/09/2017.
 */

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private List<ImageBean> mImages;

    public ImageAdapter(Context context) {
        mContext = context;
        mImages = new ArrayList<>();
    }

    @Override
    public int getCount() {

        return mImages == null ? 0 : mImages.size();
    }

    @Override
    public Object getItem(int position) {
        return mImages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_lv_images, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        File file = new File(mImages.get(position).getPath());

        Picasso.with(mContext).load(file).resize(300, 300).centerCrop().into(holder.iv_image);

        return convertView;
    }

    public static class ViewHolder {
        public SquareImageView iv_image;

        public ViewHolder(View rootView) {
            this.iv_image = (SquareImageView) rootView.findViewById(R.id.iv_image);
        }

    }

    public void addAllImages(List<ImageBean> images) {
        mImages.clear();
        if (images != null && !images.isEmpty()) {
            mImages.addAll(images);
        }

        notifyDataSetChanged();
    }
}
