package com.kyangc.demoplus.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kyangc.demoplus.R;
import com.kyangc.demoplus.entities.HttpMethodEntity;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by chengkangyang on 七月.30.2015
 */
public class HttpMethodListAdapter extends RecyclerView.Adapter<HttpMethodListAdapter.ItemViewHolder> {

    OnItemClickListener listener;
    ArrayList<HttpMethodEntity> list;
    Context context;
    LayoutInflater inflater;

    public HttpMethodListAdapter(Context context, ArrayList<HttpMethodEntity> list) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
    }

    public HttpMethodListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(inflater.inflate(R.layout.item_http_list, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, final int position) {
        final HttpMethodEntity entity = list.get(position);
        holder.tvName.setText(entity.name);
        holder.rlContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.OnItemClick(getItemAtPosition(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list == null) return 0;
        return list.size();
    }

    public HttpMethodEntity getItemAtPosition(int position) {
        if (position >= getItemCount() || position < 0) {
            return null;
        } else {
            return list.get(position);
        }
    }

    public HttpMethodListAdapter setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
        return this;
    }

    public HttpMethodListAdapter setDataSet(ArrayList<HttpMethodEntity> list) {
        this.list = list;
        return this;
    }

    public HttpMethodListAdapter setContext(Context context) {
        this.context = context;
        return this;
    }

    public interface OnItemClickListener {
        void OnItemClick(HttpMethodEntity entity);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tvName)
        TextView tvName;
        @Bind(R.id.rlContainer)
        RelativeLayout rlContainer;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
