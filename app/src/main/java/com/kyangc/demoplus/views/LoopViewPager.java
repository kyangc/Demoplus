package com.kyangc.demoplus.views;

import com.kyangc.developkit.helper.ScheduledTask;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Author:  kyangc
 * Email:   kyangc@gmail.com
 * Date:    2016-01-18
 * Project: Demoplus
 */
public class LoopViewPager extends ViewPager implements View.OnTouchListener {

    public static final String TAG = "LoopViewPager";

    /**
     * 是否自动滚动
     */
    private boolean mIsAutoScroll = false;

    /**
     * 是否正向滚动
     */
    private boolean mIsForward = true;

    /**
     * 开始滚动之前的延时
     */
    private long mDelay = 0;

    /**
     * 滚动之间的间隔
     */
    private long mScrollInterval = Integer.MAX_VALUE;

    /**
     * 滚动事件
     */
    private ScheduledTask mScheduledTask;

    /**
     * 绑定的Adapter
     */
    private LoopViewPagerAdapter mAdapter;

    /**
     * 是否正在滚动
     */
    private boolean mIsScrolling = false;

    public LoopViewPager(Context context) {
        this(context, null);
    }

    public LoopViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnTouchListener(this);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        if (adapter instanceof LoopViewPagerAdapter) {
            super.setAdapter(adapter);
            mAdapter = (LoopViewPagerAdapter) adapter;
        }
    }

    /**
     * 设置轮播的参数
     *
     * @param isAutoScroll   是否轮播
     * @param scrollInterval 轮播却换page间隔
     * @param delay          开始第一次切换时的间隔
     */
    public void setAutoScroll(boolean isAutoScroll, boolean isForward, long scrollInterval,
            long delay) {
        this.mIsAutoScroll = isAutoScroll;
        this.mScrollInterval = scrollInterval;
        this.mIsForward = isForward;
        this.mDelay = delay;
    }

    /**
     * 开始轮播。
     */
    public void startScroll() {
        mIsScrolling = true;
        if (mScheduledTask != null) {
            mScheduledTask.destroy();
            mScheduledTask = new ScheduledTask()
                    .setWorkingThread(ScheduledTask.WorkingHandler.WorkingOn.Main)
                    .setInterval(mScrollInterval)
                    .setDelayInMillis(mDelay)
                    .setTask(new ScheduledTask.Task() {
                        @Override
                        public void onExecute() {
                            if (getChildCount() > 1) {
                                scrollToItem(getCurrentItem() + ((mIsForward ? 1 : -1)));
                            }
                        }
                    });
            mScheduledTask.start();
            mIsAutoScroll = true;
        } else {
            mScheduledTask = new ScheduledTask()
                    .setWorkingThread(ScheduledTask.WorkingHandler.WorkingOn.Main)
                    .setInterval(mScrollInterval)
                    .setDelayInMillis(mDelay)
                    .setTask(new ScheduledTask.Task() {
                        @Override
                        public void onExecute() {
                            if (getChildCount() > 1) {
                                scrollToItem(getCurrentItem() + ((mIsForward ? 1 : -1)));
                            }
                        }
                    });
            mScheduledTask.start();
            mIsAutoScroll = true;
        }
    }

    /**
     * 停止轮播。
     */
    public void stopScroll() {
        if (mScheduledTask != null && mIsScrolling) {
            mScheduledTask.destroy();
            mScheduledTask = null;
            mIsScrolling = false;
            mIsAutoScroll = false;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN
                || action == MotionEvent.ACTION_MOVE) {
            stopScroll();
        } else if (action == MotionEvent.ACTION_UP) {
            if (mIsAutoScroll) {
                startScroll();
            }
        }
        return false;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setCurrentItem(mAdapter.getStartIndex());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopScroll();
        System.gc();
    }

    /**
     * 设置自动滑动的动画持续时间倍数。通过反射获取到ViewPager对象中的Scroller，并设置其为修改过的Scroller。
     *
     * @param durationFactor 动画的放映倍数。
     */
    public void setScrollSpeed(float durationFactor) {
        Class claz = getClass().getSuperclass();
        Field fScroller, fsInterpolator;

        try {
            fScroller = claz.getDeclaredField("mScroller");
            fsInterpolator = claz.getDeclaredField("sInterpolator");

            if (fScroller == null || fsInterpolator == null) {
                return;
            }

            fScroller.setAccessible(true);
            fsInterpolator.setAccessible(true);

            Interpolator interpolator = (Interpolator) fsInterpolator.get(this);
            ControlledScroller scroller = new ControlledScroller(getContext(), interpolator);
            scroller.setScrollDurationFactor(durationFactor);
            fScroller.set(this, scroller);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 滑动到指定的Page。通过反射获取到的scrollToItem方法，避免了直接使用setCurrentItem导致的动画消失的问题。
     *
     * @param i page编号
     */
    public void scrollToItem(int i) {
        Class claz = getClass().getSuperclass();

        Method mScrollToItem;
        Field fCurItem;
        try {
            mScrollToItem = claz.getDeclaredMethod("scrollToItem", int.class, boolean.class,
                    int.class, boolean.class);
            fCurItem = claz.getDeclaredField("mCurItem");

            if (mScrollToItem == null || fCurItem == null) {
                return;
            }

            mScrollToItem.setAccessible(true);
            fCurItem.setAccessible(true);

            mScrollToItem.invoke(this, i, true, 1, true);
            fCurItem.setInt(this, i);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addOnPageChangeListener(final OnPageChangeListener listener) {
        super.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                    int positionOffsetPixels) {
                listener.onPageScrolled(mAdapter.getRelevantDataPosition(position), positionOffset,
                        positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                listener.onPageSelected(mAdapter.getRelevantDataPosition(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                listener.onPageScrollStateChanged(state);
            }
        });
    }

    /**
     * 可控制动画持续时间倍数的Scroller
     */
    private static class ControlledScroller extends Scroller {

        private float scrollDurationFactor = 1.0f;

        public ControlledScroller(Context context) {
            super(context);
        }

        public ControlledScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public ControlledScroller(Context context, Interpolator interpolator, boolean flywheel) {
            super(context, interpolator, flywheel);
        }

        public void setScrollDurationFactor(float scrollDurationFactor) {
            this.scrollDurationFactor = scrollDurationFactor;
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, (int) (duration / scrollDurationFactor));
        }
    }

    public static abstract class LoopViewPagerAdapter<T> extends PagerAdapter {

        public static final String TAG = "LoopViewPagerAdapter";

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

        boolean mIsLoop = false;

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
            if (!mIsLoop)return 0;
            return getDataSize() <= 1 ? 0
                    : (FAKE_FACTOR / 2 > 1 ? FAKE_FACTOR / 2 * getDataSize() + 1 : 1);
        }

        @Override
        public int getCount() {
            if (!mIsLoop)return getDataSize();
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
            if (!mIsLoop) return;
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
            if (!mIsLoop) return itemPosition;
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
