package com.kyangc.demoplus.adapters;

import com.kyangc.demoplus.views.SquareImageView;
import com.kyangc.developkit.helper.GalleryHelper;
import com.kyangc.developkit.image.internal.ILocalImageLoader;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by chengkangyang on 十月.14.2015
 */
public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context mContext;

    List<ILocalImageLoader.LocalImageEntity> mData;

    LayoutInflater mInflater;

    public GalleryAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(new SquareImageView(mContext));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        GalleryHelper.getInstance()
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
        return mData.get(position);
    }

    public void setData(List<ILocalImageLoader.LocalImageEntity> data) {
        mData = data;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        SquareImageView photo;

        public ItemViewHolder(SquareImageView itemView) {
            super(itemView);
            photo = itemView;
        }
    }
}
