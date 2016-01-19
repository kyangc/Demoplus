package com.kyangc.demoplus.views;

import com.kyangc.demoplus.adapters.LoopViewPagerAdapter;
import com.kyangc.developkit.helper.ScheduledTask;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
    public void setAutoScroll(boolean isAutoScroll, long scrollInterval, long delay) {
        this.mIsAutoScroll = isAutoScroll;
        this.mScrollInterval = scrollInterval;
        this.mDelay = delay;
    }

    /**
     * 开始轮播。
     */
    public void startScroll() {
        mIsScrolling = true;
        if (mScheduledTask != null) {
            mScheduledTask.setInterval(mScrollInterval).setDelayInMillis(mDelay).start();
        } else {
            mScheduledTask = new ScheduledTask()
                    .setWorkingThread(ScheduledTask.WorkingHandler.WorkingOn.Main)
                    .setInterval(mScrollInterval)
                    .setDelayInMillis(mDelay)
                    .setTask(new ScheduledTask.Task() {
                        @Override
                        public void onExecute() {
                            if (getChildCount() > 1) {
                                scrollToItem(getCurrentItem() + 1);
                            }
                        }
                    });
            mScheduledTask.start();
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
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN
                || action == MotionEvent.ACTION_MOVE) {
            stopScroll();
        } else if (action == MotionEvent.ACTION_UP) {
            startScroll();
        }
        return false;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setCurrentItem(mAdapter.getStartIndex());
        if (mIsAutoScroll) {
            startScroll();
        }
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
}
