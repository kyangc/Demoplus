package com.kyangc.demoplus.internal;

import android.support.v7.widget.RecyclerView;

public interface ItemTouchHelperAdapter {

    /**
     * 当item被拖动的时候触发回调
     *
     * @param fromPosition 开始拖动的位置
     * @param toPosition   拖动结束的位置
     * @return 如果到了新的位置则返回True
     */
    boolean onItemMove(int fromPosition, int toPosition);


    /**
     * 当item被滑动删除的时候触发回调。注意：在调整数据之后需要调用{@link RecyclerView.Adapter#notifyItemRemoved(int)}来反映这次删除
     *
     * @param position The position of the item dismissed.
     */
    void onItemDismiss(int position);
}
