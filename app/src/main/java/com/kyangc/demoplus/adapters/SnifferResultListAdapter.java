package com.kyangc.demoplus.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kyangc.demoplus.R;
import com.kyangc.demoplus.entities.SnifferDataEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by chengkangyang on 八月.06.2015
 */
public class SnifferResultListAdapter extends RecyclerView.Adapter<SnifferResultListAdapter.ItemViewHolder> {

    Context context;
    List<SnifferDataEntity> dataSet;
    LayoutInflater inflater;
    OnClickListener onClickListener;

    public SnifferResultListAdapter(Context context) {
        this.context = context;
        dataSet = new ArrayList<>();
        inflater = LayoutInflater.from(context);
    }

    public SnifferResultListAdapter setDataSet(List<SnifferDataEntity> dataSet) {
        this.dataSet = dataSet;
        return this;
    }

    public SnifferResultListAdapter setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        return this;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(inflater.inflate(R.layout.item_pcap_list, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, final int position) {
        final SnifferDataEntity entity = getEntityAt(position);
        holder.tvFileName.setText(entity.fileName);
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null)
                    onClickListener.onDeleteClick(entity.filePath);
            }
        });
        holder.ivEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null)
                    onClickListener.onEmailClick(entity.fileName, entity.filePath);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet == null ? 0 : dataSet.size();
    }

    public SnifferDataEntity getEntityAt(int position) {
        if (position < 0 || position >= getItemCount()) return null;
        return dataSet.get(position);
    }

    public interface OnClickListener {
        void onEmailClick(String fileName, String filePath);

        void onDeleteClick(String filePath);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tvFilename)
        TextView tvFileName;
        @Bind(R.id.ivEmail)
        ImageView ivEmail;
        @Bind(R.id.ivDelete)
        ImageView ivDelete;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
