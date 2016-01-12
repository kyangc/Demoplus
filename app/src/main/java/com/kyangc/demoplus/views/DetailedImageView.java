package com.kyangc.demoplus.views;

import com.kyangc.developkit.gesture.MoveGestureDetector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import timber.log.Timber;

/**
 * Created by chengkangyang on 16/1/8.
 */
public class DetailedImageView extends View {

    /**
     * 解码选项
     */
    private BitmapFactory.Options mDecodeOptions;

    /**
     * 解码器
     */
    private BitmapRegionDecoder mDecoder;

    /**
     * 图片的宽度和高度
     */
    private int mImageWidth, mImageHeight;

    /**
     * 屏幕显示区域的宽度和高度
     */
    private int mScreenWidth, mScreenHeight;

    /**
     * 裁剪的Rect区域
     */
    private Rect mCutRect;

    /**
     * 显示的Rect区域
     */
    private Rect mDisplayRect;

    /**
     * 用于标示Image的区域。用于绘制越界时的图像。
     */
    private Rect mImageRect;

    /**
     * 屏幕RECT
     */
    private Rect mScreenRect;

    /**
     * 手势检测器
     */
    private MoveGestureDetector mDetector;

    /**
     * 触点ID
     */
    private int mTouchPoint;

    /**
     * 滑动的TimerTask
     */
    private ScrollTimerTask mScrollTimerTask;

    /**
     * 控制滑动的Timer
     */
    private Timer mTimer;

    /**
     * 滑动速度检测器
     */
    private VelocityTracker mVelocityTracker;

    /**
     * 可检测的最大速度
     */
    private int mMaxVelocity;

    /**
     * 构造函数
     */
    public DetailedImageView(Context context) {
        super(context);
        init();
    }

    public DetailedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DetailedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化方法
     */
    public void init() {
        mDetector = new MoveGestureDetector(getContext(),
                new MoveGestureDetector.SimpleOnMoveGestureListener() {
                    @Override
                    public boolean onMove(MoveGestureDetector detector) {
                        double moveX = detector.getFocusDelta().x;
                        double moveY = detector.getFocusDelta().y;
                        moveCutRegion(moveX, moveY, mCutRect, mScreenRect);
                        invalidate();
                        return true;
                    }
                });

        mVelocityTracker = VelocityTracker.obtain();
        mMaxVelocity = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity();
    }

    /**
     * 销毁方法。
     */
    public void destroy() {
        if (mDecodeOptions != null && mDecodeOptions.inBitmap != null && !mDecodeOptions.inBitmap
                .isRecycled()) {
            mDecodeOptions.inBitmap.recycle();
            mDecodeOptions.inBitmap = null;
            mDecodeOptions = null;
        }

        if (mDecoder != null && !mDecoder.isRecycled()) {
            mDecoder.recycle();
            mDecoder = null;
        }

        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }

        System.gc();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //Get display size
        mScreenWidth = getMeasuredWidth();
        mScreenHeight = getMeasuredHeight();

        //Init Display rect
        mDisplayRect = getInitDisplayRect(mScreenHeight, mScreenWidth);
        mScreenRect = getInitDisplayRect(mScreenHeight, mScreenWidth);

        //Init Cut rect
        mCutRect = getInitCutRect(0, 0, mImageHeight, mImageWidth,
                mScreenHeight, mScreenWidth, false, false);

        //Init image rect
        mImageRect = getInitImageRect(mCutRect, mImageWidth, mImageHeight);

        //Update sample size
        mDecodeOptions.inSampleSize = getSampleSize(mCutRect, mDisplayRect);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        decode(mDecoder, mDecodeOptions, mCutRect);
        canvas.drawRGB(0, 0, 0);
        updateImageRect(mImageRect, mCutRect, mImageWidth, mImageHeight);
        updateDisplayRect(mDisplayRect, mImageRect, mCutRect, mScreenHeight);
        canvas.drawBitmap(mDecodeOptions.inBitmap, mImageRect, mDisplayRect, null);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        destroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);
        mVelocityTracker.addMovement(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mScrollTimerTask != null) {
                    mScrollTimerTask.cancel();
                }

                if (mTimer != null) {
                    mTimer.cancel();
                    mTimer.purge();
                }

                mTouchPoint = event.getPointerId(0);
                break;

            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                float mVelocityX = mVelocityTracker.getXVelocity(mTouchPoint);
                float mVelocityY = mVelocityTracker.getYVelocity(mTouchPoint);
                mVelocityTracker.clear();

                mScrollTimerTask = new ScrollTimerTask(mVelocityX, mVelocityY);
                mTimer = new Timer();
                mTimer.schedule(mScrollTimerTask, 0, ScrollTimerTask.POST_INTERVAL);
                break;

            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.clear();
                break;

            default:
                break;
        }
        return true;
    }

    /**
     * 将输入流显示在view上。
     *
     * @param is 输入流
     */
    public void setInputStream(@NonNull InputStream is) {
        try {
            //Prepare decoder
            if (mDecoder != null && !mDecoder.isRecycled()) {
                mDecoder.recycle();
            }
            mDecoder = BitmapRegionDecoder.newInstance(is, false);
            mDecodeOptions = getDecodeOptions();
            getImageSize(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 获取图片的大小信息。
     *
     * @param is 文件流。
     */
    private void getImageSize(@NonNull InputStream is) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, opt);
        mImageWidth = opt.outWidth;
        mImageHeight = opt.outHeight;
    }

    /**
     * 初始化解码时的options
     *
     * @return options。
     */
    private BitmapFactory.Options getDecodeOptions() {
        if (mDecodeOptions != null && mDecodeOptions.inBitmap != null && !mDecodeOptions.inBitmap
                .isRecycled()) {
            mDecodeOptions.inBitmap.recycle();
            mDecodeOptions.inBitmap = null;
        }

        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = false;
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inMutable = false;
        return opt;
    }

    /**
     * 获取采样尺寸数值。
     *
     * @param sampleRect  截取的Rect
     * @param displayRect 显示的Rect
     * @return 采样数值
     */
    public int getSampleSize(@NonNull Rect sampleRect, @NonNull Rect displayRect) {
        int sampleLength = sampleRect.right - sampleRect.left;
        int displayLength = displayRect.right - displayRect.left;

        if (sampleLength <= 0 || displayLength <= 0) {
            return 1;
        }

        if (sampleLength <= displayLength) {
            //截取部分的像素少于显示区域的像素
            return 1;
        } else {
            //截取部分的像素比显示区域的像素高，需要设置更高的采样尺寸以减少内存消耗
            double ratio = (double) sampleLength / (double) displayLength;
            return (int) ratio;
        }
    }

    /**
     * 初始化显示的rect
     *
     * @param screenHeight 屏幕高度
     * @param screenWidth  屏幕宽度
     */
    private Rect getInitDisplayRect(int screenHeight, int screenWidth) {
        Rect displayRect = new Rect();
        displayRect.top = 0;
        displayRect.left = 0;
        displayRect.bottom = screenHeight;
        displayRect.right = screenWidth;
        return displayRect;
    }

    /**
     * 更新显示的区域Rect
     *
     * @param displayRect 原有的显示区域Rect
     * @param imageRect   bitmap中有效区域
     * @param cutRect     裁剪Rect
     * @return 更新后的显示区域Rect
     */
    private Rect updateDisplayRect(@NonNull Rect displayRect, @NonNull Rect imageRect,
            @NonNull Rect cutRect, int screenHeight) {
        double cutHeight = cutRect.height();
        double ratio = (double) screenHeight / cutHeight;
        displayRect.top = (int) (ratio * (double) imageRect.top);
        displayRect.left = (int) (ratio * (double) imageRect.left);
        displayRect.right = (int) (ratio * (double) imageRect.right);
        displayRect.bottom = (int) (ratio * (double) imageRect.bottom);
        return displayRect;
    }

    /**
     * 初始化裁剪Rect。
     *
     * @param imageHeight  图片高度
     * @param imageWidth   图片宽度
     * @param screenWidth  屏幕宽度
     * @param screenHeight 屏幕高度
     * @return 裁剪的rect
     */
    public Rect getInitCutRect(int x, int y, int imageHeight, int imageWidth, int screenHeight,
            int screenWidth, boolean isDisplayAll, boolean isCenter) {
        Rect cutRect = new Rect();
        if (imageHeight == 0 || imageWidth == 0 || screenWidth == 0 || screenHeight == 0) {
            return cutRect;
        }

        double imageAspect = (double) imageHeight / (double) imageWidth;
        double displayAspect = (double) screenHeight / (double) screenWidth;

        if (imageAspect > displayAspect) {
            //Long
            if (isDisplayAll) {
                //All
                int cutWidth = (int) ((double) imageHeight / displayAspect);
                cutRect.left = -(cutWidth / 2 - imageWidth / 2);
                cutRect.right = imageWidth / 2 + cutWidth / 2;
                cutRect.top = 0;
                cutRect.bottom = imageHeight;
            } else {
                //Part
                int cutHeight = (int) ((double) imageWidth * displayAspect);
                if (isCenter) {
                    //Center
                    cutRect.left = 0;
                    cutRect.top = imageHeight / 2 - cutHeight / 2;
                } else {
                    //Start
                    cutRect.left = x;
                    cutRect.top = y;
                }
                cutRect.right = cutRect.left + imageWidth;
                cutRect.bottom = cutRect.top + cutHeight;
            }
        } else {
            //Wide
            if (isDisplayAll) {
                //All
                int cutHeight = (int) ((double) imageWidth * displayAspect);
                cutRect.left = 0;
                cutRect.right = imageWidth;
                cutRect.top = -(cutHeight / 2 - imageHeight / 2);
                cutRect.bottom = imageHeight / 2 + cutHeight / 2;
            } else {
                //Part
                int cutWidth = (int) ((double) imageHeight / displayAspect);
                if (isCenter) {
                    //Center
                    cutRect.left = imageWidth / 2 - cutWidth / 2;
                    cutRect.top = 0;
                } else {
                    //Start
                    cutRect.left = x;
                    cutRect.top = y;
                }
                cutRect.right = cutRect.left + cutWidth;
                cutRect.bottom = cutRect.top + imageHeight;
            }
        }

        return cutRect;
    }

    /**
     * 更新移动后的裁剪Rect。
     *
     * @param cutRect  裁剪Rect
     * @param offsetX  X轴偏移量
     * @param offsetY  Y轴偏移量
     * @param cutWidth 裁剪宽度
     * @return 更新过的Rect
     */
    public Rect updateCutRect(@NonNull Rect cutRect, int offsetX, int offsetY, int cutWidth,
            int screenHeight, int screenWidth, int imageHeight) {

        double ratio = (double) screenHeight / (double) screenWidth;
        int cutHeight = (int) (cutWidth * ratio);

        cutRect.offset(-offsetX, -offsetY);

        if (cutRect.left <= -cutWidth) {
            cutRect.left = -cutWidth + 1;
        }

        if (cutRect.top <= -cutHeight) {
            cutRect.top = -cutHeight + 1;
        }

        if (cutRect.left >= cutWidth) {
            cutRect.left = cutWidth - 1;
        }

        if (cutRect.top >= imageHeight) {
            cutRect.top = imageHeight - 1;
        }

        cutRect.right = cutRect.left + cutWidth;
        cutRect.bottom = cutRect.top + cutHeight;
        return cutRect;
    }

    /**
     * 初始化image区域的Rect，其大多数情况下与displayRect相同，但是当显示过界图像时会用到
     *
     * @param cutRect     裁剪图像的Rect
     * @param imageWidth  图像的宽度
     * @param imageHeight 图像的高度
     * @return 包含有效图像信息的区域
     */
    private Rect getInitImageRect(@NonNull Rect cutRect, int imageWidth, int imageHeight) {
        Rect rect = new Rect();
        return updateImageRect(rect, cutRect, imageWidth, imageHeight);
    }

    /**
     * 更新图像区域Rect
     *
     * @param imageRect   指示有效图像区域的Rect
     * @param cutRect     裁剪的Rect
     * @param imageWidth  图像宽度
     * @param imageHeight 图像高度
     * @return 更新之后的Rect
     */
    private Rect updateImageRect(@NonNull Rect imageRect, @NonNull Rect cutRect, int imageWidth,
            int imageHeight) {
        double cutHeight = cutRect.height();
        double cutWidth = cutRect.width();

        //left
        if (cutRect.left >= 0) {
            //in image
            imageRect.left = 0;
        } else {
            //out of image
            imageRect.left = -cutRect.left;
        }

        //top
        if (cutRect.top >= 0) {
            //in image
            imageRect.top = 0;
        } else {
            //out of image
            imageRect.top = -cutRect.top;
        }

        //right
        if (cutRect.right <= imageWidth) {
            //in image
            imageRect.right = (int) cutWidth;
        } else {
            //out of image
            imageRect.right = imageWidth - cutRect.left;
        }

        //bottom
        if (cutRect.bottom <= imageHeight) {
            //in image
            imageRect.bottom = (int) cutHeight;
        } else {
            //out of image
            imageRect.bottom = imageHeight - cutRect.top;
        }

        return imageRect;
    }

    /**
     * 根据手指的移动来修改解码区域
     *
     * @param fingerDeltaX 手指移动的距离X坐标
     * @param fingerDeltaY 手指移动的距离Y坐标
     * @param cutRect      裁剪区域
     * @param screenRect   屏幕区域
     */
    private Rect moveCutRegion(double fingerDeltaX, double fingerDeltaY, Rect cutRect,
            @NonNull Rect screenRect) {
        double scaleRatio = getScaledRatio(cutRect, screenRect);
        double cutDeltaX = fingerDeltaX / scaleRatio;
        double cutDeltaY = fingerDeltaY / scaleRatio;
        return updateCutRect(cutRect, (int) cutDeltaX, (int) cutDeltaY, cutRect.width(),
                mScreenHeight, mScreenWidth, mImageHeight);
    }

    /**
     * 根据缩放后的情况修改解码区域
     *
     * @param pivotX      放缩锚点X坐标
     * @param pivotY      放缩锚点Y坐标
     * @param scaleRatio  放缩幅度
     * @param cutRect     裁剪区域
     * @param displayRect 显示区域
     */
    private Rect scaleCutRegion(int pivotX, int pivotY, double scaleRatio, @NonNull Rect cutRect,
            @NonNull Rect displayRect) {
        return updateCutRect(cutRect, 0, 0, 0, mScreenHeight, mScreenWidth, mImageHeight);
    }

    /**
     * 使用给定的decoder按照option和rect进行区域解码。
     *
     * @param decoder 解码器
     * @param options 解码选项
     * @param cutRect 解码区域
     */
    private void decode(@NonNull BitmapRegionDecoder decoder,
            @NonNull BitmapFactory.Options options, @NonNull Rect cutRect) {
        if (options.inBitmap == null) {
            options.inBitmap = decoder.decodeRegion(cutRect, options);
        } else {
            decoder.decodeRegion(cutRect, options);
        }
    }

    /**
     * 获取当前裁剪区域在显示中的放缩倍数。
     * 如：600宽度的图片显示在1080宽度的手机屏幕上，放缩倍数为1080/600=1.8。
     *
     * @param cutRect    裁剪区域
     * @param screenRect 屏幕区域
     * @return 放缩倍数
     */
    private double getScaledRatio(@NonNull Rect cutRect, @NonNull Rect screenRect) {
        return (double) screenRect.width() / (double) cutRect.width();
    }

    private class ScrollTimerTask extends TimerTask {

        public static final int POST_INTERVAL = 17;//1000/60

        private static final float ACCELERATION = 0.01f;

        private float mVelocityX, mVelocityY;

        public ScrollTimerTask(float velocityX, float velocityY) {
            mVelocityX = velocityX/1000;
            mVelocityY = velocityY/1000;
        }

        @Override
        public void run() {
            float deltaX = getDeltaMovement(mVelocityX, POST_INTERVAL, ACCELERATION);
            float deltaY = getDeltaMovement(mVelocityY, POST_INTERVAL, ACCELERATION);
            Timber.i(deltaX + "," + deltaY);
            mVelocityX = getLaterSpeed(mVelocityX, POST_INTERVAL, ACCELERATION);
            mVelocityY = getLaterSpeed(mVelocityY, POST_INTERVAL, ACCELERATION);
            if (deltaX != 0 || deltaY != 0) {
                moveCutRegion(deltaX, deltaY, mCutRect, mScreenRect);
                postInvalidate();
            } else {
                mScrollTimerTask.cancel();
                mTimer.cancel();
                mTimer.purge();
            }
        }

        /**
         * 获取匀减速运动的距离。单位为像素。正直向下(右)，负值向上(左)。
         *
         * @param v0 初始速度
         * @param t  持续时间
         * @param a  减速度（绝对值）
         * @return 移动距离
         */
        private float getDeltaMovement(float v0, float t, float a) {
            float absV0 = Math.abs(v0);
            float flag = v0 >= 0 ? 1 : -1;

            if (absV0 / a <= t) {
                t = absV0 / a;
            }

            return (absV0 * t - a * (float) Math.pow(t, 2) / 2f) * flag;
        }

        /**
         * 获取在减速度为a时，经过t时刻后的速度。单位：像素/毫秒
         *
         * @param v0 初速度
         * @param t  时间
         * @param a  加速度（绝对值）
         * @return 经过T时间之后的速度。
         */
        private float getLaterSpeed(float v0, float t, float a) {
            float absV0 = Math.abs(v0);
            int flag = v0 >= 0 ? 1 : -1;

            if (absV0 / a <= t) {
                return 0;
            }

            return (absV0 - a * t) * flag;
        }
    }
}
