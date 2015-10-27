package com.kyangc.demoplus.adapters;

import com.kyangc.developkit.image.internal.ILocalImageLoader;
import com.kyangc.developkit.image.impl.ImageLoaderImpl;
import com.kyangc.demoplus.views.GalleryItemView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.TreeMap;

/**
 * Created by chengkangyang on 十月.14.2015
 */
public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context mContext;

    TreeMap<Long, ILocalImageLoader.LocalImageEntity> mData;

    LayoutInflater mInflater;

    public GalleryAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(new GalleryItemView(mContext));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ImageLoaderImpl.getInstance()
                .loadLocalImage(((ItemViewHolder) holder).photo, getDataAt(position));
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public ILocalImageLoader.LocalImageEntity getDataAt(int position) {
        if (position >= getItemCount() || position < 0) {
            return null;
        }
        return mData.get(mData.keySet().toArray()[position]);
    }

    public void setData(TreeMap<Long, ILocalImageLoader.LocalImageEntity> data) {
        mData = data;
    }

    public TreeMap<Long, ILocalImageLoader.LocalImageEntity> getData() {
        return mData;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        GalleryItemView photo;

        public ItemViewHolder(GalleryItemView itemView) {
            super(itemView);
            photo = itemView;
        }
    }
}
