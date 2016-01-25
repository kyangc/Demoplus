package com.kyangc.demoplus.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Author:  kyangc
 * Email:   kyangc@gmail.com
 * Date:    2016-01-25
 * Project: nayutas
 */
public class InfiniteViewPager extends ViewPager {

    public static final String TAG = "InfiniteViewPager";

    /**
     * 绑定的Adapter
     */
    private InfiniteViewPagerAdapter mAdapter;

    public InfiniteViewPager(Context context) {
        super(context);
    }

    public InfiniteViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        if (adapter instanceof InfiniteViewPagerAdapter) {
            mAdapter = (InfiniteViewPagerAdapter) adapter;
            super.setAdapter(adapter);
        }
    }

    /**
     * 设置Adapter，决定是否需要循环滚动。
     *
     * @param adapter InfiniteViewPagerAdapter
     * @param isLoop  是否需要循环滚动
     */
    public void setAdapter(@NonNull InfiniteViewPagerAdapter adapter, boolean isLoop) {
        adapter.setIsLoop(isLoop);
        setAdapter(adapter);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setCurrentItem(mAdapter.getStartIndex());
    }

    public static abstract class InfiniteViewPagerAdapter<T> extends PagerAdapter {

        public static final String TAG = "InfiniteViewPagerAdapter";

        /**
         * 伪造的item数量倍数。
         */
        public static final int FAKE_FACTOR = 10;

        /**
         * 绑定的ViewPager实例
         */
        ViewPager mViewPager;

        /**
         * 数据实例
         */
        List<T> mDataSet;

        /**
         * View实例
         */
        SparseArray<View> mViewList = new SparseArray<>();

        /**
         * 是否循环滚动，默认可以
         */
        boolean mIsLoop = true;

        public InfiniteViewPagerAdapter(ViewPager viewPager, List<T> dataSet) {
            mViewPager = viewPager;
            mDataSet = dataSet;
        }

        public void setIsLoop(boolean isLoop) {
            mIsLoop = isLoop;
        }

        /**
         * 绑定数据源。
         */
        public void setDataSet(@NonNull List<T> dataSet) {
            mDataSet = dataSet;
        }

        /**
         * 获取到对应位置上的数据。
         *
         * @param position page位
         * @return 该page位上的数据
         */
        public T getDataAt(int position) {
            return mDataSet == null ? null
                    : mDataSet.get(getRelevantDataPosition(position));
        }

        /**
         * 获取数据量的数量
         *
         * @return 数据数量
         */
        public int getDataSize() {
            return mDataSet == null ? 0 : mDataSet.size();
        }

        /**
         * 获取ViewPager起始的页码。
         *
         * @return 起始页码。
         */
        public int getStartIndex() {
            if (!mIsLoop) {
                return 0;
            }
            return getDataSize() <= 1 ? 0
                    : (FAKE_FACTOR / 2 > 1 ? FAKE_FACTOR / 2 * getDataSize() + 1 : 1);
        }

        @Override
        public int getCount() {
            if (!mIsLoop) {
                return getDataSize();
            }
            return getDataSize() <= 1 ? getDataSize() : FAKE_FACTOR * getDataSize() + 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = inflateView(getRelevantDataPosition(position), getDataAt(position));
            updateView(view, getRelevantDataPosition(position), getDataAt(position));
            container.addView(view);
            mViewList.put(position, view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            mViewList.remove(position);
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            if (!mIsLoop) {
                return;
            }
            int currentPos = mViewPager.getCurrentItem();
            if (currentPos == 0) {
                if (getDataSize() > 1) {
                    mViewPager.setCurrentItem(FAKE_FACTOR * getDataSize(), false);
                }
            } else if (currentPos == FAKE_FACTOR * getDataSize() + 1) {
                if (FAKE_FACTOR / 2 > 1) {
                    mViewPager.setCurrentItem(FAKE_FACTOR * getDataSize() / 2 + 1, false);
                } else {
                    mViewPager.setCurrentItem(1, false);
                }
            }
        }

        @Override
        public void notifyDataSetChanged() {
            int key;
            for (int i = 0; i < mViewList.size(); i++) {
                key = mViewList.keyAt(i);
                View view = mViewList.get(key);
                updateView(view, getRelevantDataPosition(key), getDataAt(key));
            }
            super.notifyDataSetChanged();
        }

        /**
         * 在Adapter中绑定View的方法，重载该方法完成page的初始化。
         *
         * @param data 实例化View所需要的数据
         * @return 实例化之后的View
         */
        public abstract View inflateView(int position, T data);

        /**
         * 更新View、填充内容用的方法。
         *
         * @param view     需要更新的View
         * @param position 该View的位置 - 对应的数据位置
         * @param data     对应的数据
         */
        public abstract void updateView(View view, int position, T data);

        /**
         * 获取到对应位置上的数据序号。
         *
         * @param itemPosition page位置
         * @return 数据在数据表中的位置。
         */
        public int getRelevantDataPosition(int itemPosition) {
            if (!mIsLoop) {
                return itemPosition;
            }
            if (getDataSize() == 0) {
                return 0;
            }

            if (itemPosition == 0) {
                return getDataSize() - 1;
            } else {
                if (itemPosition == FAKE_FACTOR * getDataSize() + 1) {
                    return 0;
                } else {
                    return (itemPosition - 1) % getDataSize();
                }
            }
        }
    }
}
