package com.kyangc.demoplus.adapters;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Author:  kyangc
 * Email:   kyangc@gmail.com
 * Date:    2016-01-18
 * Project: Demoplus
 */
public abstract class LoopViewPagerAdapter<T> extends PagerAdapter {

    public static final String TAG = "LoopViewPagerAdapter";

    /**
     * 伪造的item数量。
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

    public LoopViewPagerAdapter(ViewPager viewPager, List<T> dataSet) {
        mViewPager = viewPager;
        mDataSet = dataSet;
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
        return getDataSize() <= 1 ? 0
                : (FAKE_FACTOR / 2 > 1 ? FAKE_FACTOR / 2 * getDataSize() + 1 : 1);
    }

    @Override
    public int getCount() {
        return getDataSize() <= 1 ? getDataSize() : FAKE_FACTOR * getDataSize() + 2;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = getView(getRelevantDataPosition(position), getDataAt(position));
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
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

    /**
     * 在Adapter中绑定View的方法，重载该方法完成page的实例化。
     *
     * @param data 实例化View所需要的数据
     * @return 实例化之后的View
     */
    public abstract View getView(int position, T data);

    /**
     * 获取到对应位置上的数据序号。
     *
     * @param itemPosition page位置
     * @return 数据在数据表中的位置。
     */
    public int getRelevantDataPosition(int itemPosition) {
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
