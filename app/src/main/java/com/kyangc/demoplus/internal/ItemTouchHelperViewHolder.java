package com.kyangc.demoplus.internal;

public interface ItemTouchHelperViewHolder {

    /**
     * 当Item被选中的时候回调
     */
    void onItemSelected();


    /**
     * 当Item完成选中之后回调
     */
    void onItemClear();
}
