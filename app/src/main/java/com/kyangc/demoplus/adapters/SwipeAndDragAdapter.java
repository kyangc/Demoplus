package com.kyangc.demoplus.adapters;

import com.kyangc.demoplus.R;
import com.kyangc.demoplus.internal.ItemTouchHelperAdapter;
import com.kyangc.demoplus.internal.ItemTouchHelperViewHolder;
import com.kyangc.demoplus.internal.OnStartDragListener;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by chengkangyang on 九月.11.2015
 */
public class SwipeAndDragAdapter extends RecyclerView.Adapter<SwipeAndDragAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    private List<String> mItems = new ArrayList<>();

    private OnStartDragListener mDragStartListener;

    private Context context;

    public SwipeAndDragAdapter(Context context, List<String> mItems) {
        this.mItems = mItems;
        this.context = context;
    }

    public SwipeAndDragAdapter setmDragStartListener(OnStartDragListener mDragStartListener) {
        this.mDragStartListener = mDragStartListener;
        return this;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reorder_list, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        holder.tvTitle.setText(mItems.get(position));
        holder.ivReorder.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mItems, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder
            implements ItemTouchHelperViewHolder {

        @Bind(R.id.cvItem)
        RelativeLayout rlContainer;

        @Bind(R.id.tvTitle)
        TextView tvTitle;

        @Bind(R.id.ivReorder)
        ImageView ivReorder;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(Color.WHITE);
        }
    }
}
