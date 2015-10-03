package com.kyangc.demoplus.adapters;

import com.kyangc.demoplus.R;
import com.kyangc.demoplus.entities.HttpResultEntity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by chengkangyang on 七月.30.2015
 */
public class HttpResultListAdapter
        extends RecyclerView.Adapter<HttpResultListAdapter.ItemViewHolder> {

    ArrayList<HttpResultEntity> dataSet;

    Context context;

    LayoutInflater inflater;

    public HttpResultListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public HttpResultListAdapter setDataSet(ArrayList<HttpResultEntity> dataSet) {
        this.dataSet = dataSet;
        return this;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == -1) {
            return null;
        }
        return new ItemViewHolder(inflater.inflate(R.layout.item_http_result_list, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case HttpResultEntity.ITEM_TYPE_CODE:
                holder.ivMark
                        .setImageDrawable(context.getResources().getDrawable(R.drawable.mark_code));
                holder.tvHead.setText("Status Code:");
                holder.tvBody.setText(getEntityAt(position).content);
                break;
            case HttpResultEntity.ITEM_TYPE_HEADER:
                holder.ivMark.setImageDrawable(
                        context.getResources().getDrawable(R.drawable.mark_header));
                holder.tvHead.setText("Header:");
                holder.tvBody.setText(getEntityAt(position).content);
                break;
            case HttpResultEntity.ITEM_TYPE_RESPONSE:
                holder.ivMark
                        .setImageDrawable(context.getResources().getDrawable(R.drawable.mark_body));
                holder.tvHead.setText("Response:");
                holder.tvBody.setText(getEntityAt(position).content);
                break;
            case HttpResultEntity.ITEM_TYPE_ERROR:
                holder.ivMark.setImageDrawable(
                        context.getResources().getDrawable(R.drawable.mark_error));
                holder.tvHead.setText("Error:");
                holder.tvBody.setText(getEntityAt(position).content);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return getEntityAt(position) == null ? -1 : getEntityAt(position).type;
    }

    @Override
    public int getItemCount() {
        return dataSet == null ? 0 : dataSet.size();
    }

    public HttpResultEntity getEntityAt(int position) {
        if (position < 0 || position >= getItemCount()) {
            return null;
        }
        return dataSet.get(position);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.ivMark)
        ImageView ivMark;

        @Bind(R.id.tvType)
        TextView tvHead;

        @Bind(R.id.tvBody)
        TextView tvBody;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
