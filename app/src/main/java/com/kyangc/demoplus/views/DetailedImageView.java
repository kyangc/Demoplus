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
     * 在裁剪出用于一屏显示的Bitmap的时候，额外缓冲的比例。例如屏幕大小100*100，在0.2的缓冲比下，
     * 会缓冲120*120大小的图片。
     */
    public static final float BUFFER_RATIO = 3f;

    /**
     * 解码选项
     */
    private BitmapFactory.Options mDecodeOptions;

    /**
     * 解码器
     */
    private BitmapRegionDecoder mDecoder;

    /**
     * 在图片坐标系中。
     * 用于缓存下超出屏幕区域一定大小的区域。
     */
    private Rect mBufferRect;

    /**
     * 在图片坐标系中。
     * 在BufferRect中，用于映射到屏幕上的区域，用于判定是否触发Buffer以及辅助计算mSrcRect和mDestRect。
     */
    private Rect mDisplayRect;

    /**
     * 在屏幕坐标系中。
     * 指示绘制在屏幕上的、含有图像内容的区域。
     */
    private Rect mDestRect;

    /**
     * 在裁切好(Buffer后)的Bitmap坐标系中。
     * 用于标示在裁剪好的Bitmap中，有效的、需要显示到屏幕上的区域。
     */
    private Rect mSrcRect;

    /**
     * 在屏幕坐标系中。
     * 用于标识整个屏幕区域。
     */
    private Rect mScreenRect;

    /**
     * 图片坐标系中。
     * 用于标示图片。
     */
    private Rect mImageRect;

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
                        move(moveX, moveY, mBufferRect, mDisplayRect, mScreenRect,
                                mImageRect);
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
        //Init screen rect
        mScreenRect = initScreenRect(getMeasuredWidth(), getMeasuredHeight());

        //Init Display rect
        mDisplayRect = initDisplayRect(0, 0, mImageRect, mScreenRect, false, false);

        //Init buffer rect
        mBufferRect = initBufferRect(mDisplayRect, mImageRect, BUFFER_RATIO);

        //Init image rect
        mSrcRect = initSrcRect(mDisplayRect, mBufferRect, mImageRect);

        //Init dest rect
        mDestRect = initDestRect(mBufferRect, mDisplayRect, mSrcRect, mScreenRect);

        //Update sample size
        mDecodeOptions.inSampleSize = getSampleSize(mBufferRect, mDestRect);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRGB(0, 0, 0);
        if (mDecodeOptions.inBitmap == null) {
            decode(mDecoder, mDecodeOptions, mBufferRect);
        }
        canvas.drawBitmap(mDecodeOptions.inBitmap, mSrcRect, mDestRect, null);
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
            mImageRect = getImageSize(is);
            invalidate();
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
    private Rect getImageSize(@NonNull InputStream is) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, opt);
        return new Rect(0, 0, opt.outWidth, opt.outHeight);
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
    public int getSampleSize(
            @NonNull Rect sampleRect,
            @NonNull Rect displayRect) {
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
     * 获取当前裁剪区域在显示中的放缩倍数。
     * 如：600宽度的图片显示在1080宽度的手机屏幕上，放缩倍数为1080/600=1.8。
     *
     * @param cutRect    裁剪区域
     * @param screenRect 屏幕区域
     * @return 放缩倍数m
     */
    private double getScaledRatio(
            @NonNull Rect cutRect,
            @NonNull Rect screenRect) {
        return (double) screenRect.width() / (double) cutRect.width();
    }

    /**
     * 初始化屏幕属性的Rect
     *
     * @param screenWidth  屏幕宽度
     * @param screenHeight 屏幕高度
     * @return 屏幕属性的Rect
     */
    public Rect initScreenRect(
            int screenWidth,
            int screenHeight) {
        return new Rect(0, 0, screenWidth, screenHeight);
    }

    /**
     * 根据显示区域来初始化Buffer区域。
     *
     * @param displayRect 显示区域Rect
     * @param imageRect   图片区域Rect
     * @param bufferRatio Buffer的比例
     * @return 显示区域
     */
    public Rect initBufferRect(
            @NonNull Rect displayRect,
            @NonNull Rect imageRect,
            float bufferRatio) {
        Rect rect = new Rect();
        float dispWidth = displayRect.width();
        float dispHeight = displayRect.height();
        float bufferWidth = bufferRatio * dispWidth;
        float bufferHeight = bufferRatio * dispHeight;
        bufferWidth = bufferWidth >= 2 * imageRect.width() ? 2 * imageRect.width() : bufferWidth;
        bufferHeight = bufferHeight >= 2 * imageRect.height() ? 2 * imageRect.height()
                : bufferHeight;
        rect.left = displayRect.left - (int) (bufferWidth / 2f);
        rect.top = displayRect.top - (int) (bufferHeight / 2f);
        rect.right = displayRect.right + (int) (bufferWidth / 2f);
        rect.bottom = displayRect.bottom + (int) (bufferHeight / 2f);
        return rect;
    }

    /**
     * 根据现在的显示Rect更新BufferRect。
     *
     * @param bufferRect  现有的BufferRect
     * @param displayRect 显示的Rect
     * @return 更新后的显示Rect。
     */
    public boolean updateBufferRect(
            @NonNull Rect bufferRect,
            @NonNull Rect displayRect) {
        float extraWidth = bufferRect.width() - displayRect.width();
        float extraHeight = bufferRect.height() - displayRect.width();
        boolean isBufferAreaChanged = false;
        if (displayRect.left <= bufferRect.left) {
            bufferRect.offsetTo(displayRect.left - (int) (extraWidth / 2f), bufferRect.top);
            isBufferAreaChanged = true;
        }

        if (displayRect.top <= bufferRect.top) {
            bufferRect.offsetTo(bufferRect.left, displayRect.top - (int) (extraHeight / 2f));
            isBufferAreaChanged = true;
        }

        if (displayRect.right >= bufferRect.right) {
            bufferRect.offsetTo(displayRect.left - (int) (extraWidth / 2f), bufferRect.top);
            isBufferAreaChanged = true;
        }

        if (displayRect.bottom >= bufferRect.bottom) {
            bufferRect.offsetTo(bufferRect.left, displayRect.top - (int) (extraHeight / 2f));
            isBufferAreaChanged = true;
        }

        return isBufferAreaChanged;
    }

    /**
     * 初始化裁剪Rect。
     *
     * @param x            初始化显示的区域
     * @param y            初始化显示的区域
     * @param imageRect    图片rect
     * @param screenRect   屏幕rect
     * @param isDisplayAll 是否显示全部
     * @param isCenter     是否中心显示
     * @return 初始化之后的显示rect
     */
    public Rect initDisplayRect(
            int x,
            int y,
            @NonNull Rect imageRect,
            @NonNull Rect screenRect,
            boolean isDisplayAll,
            boolean isCenter) {
        Rect rect = new Rect();
        if (imageRect.height() == 0 || imageRect.width() == 0 || screenRect.width() == 0
                || screenRect.height() == 0) {
            return rect;
        }

        double imageAspect = (double) imageRect.height() / (double) imageRect.width();
        double displayAspect = (double) screenRect.height() / (double) screenRect.width();

        if (imageAspect > displayAspect) {
            //Long
            if (isDisplayAll) {
                //All
                int displayWidth = (int) ((double) imageRect.height() / displayAspect);
                rect.left = -(displayWidth / 2 - imageRect.width() / 2);
                rect.right = imageRect.width() / 2 + displayWidth / 2;
                rect.top = 0;
                rect.bottom = imageRect.height();
            } else {
                //Part
                int cutHeight = (int) ((double) imageRect.width() * displayAspect);
                if (isCenter) {
                    //Center
                    rect.left = 0;
                    rect.top = imageRect.height() / 2 - cutHeight / 2;
                } else {
                    //Start
                    rect.left = x;
                    rect.top = y;
                }
                rect.right = rect.left + imageRect.width();
                rect.bottom = rect.top + cutHeight;
            }
        } else {
            //Wide
            if (isDisplayAll) {
                //All
                int displayHeight = (int) ((double) imageRect.width() * displayAspect);
                rect.left = 0;
                rect.right = imageRect.width();
                rect.top = -(displayHeight / 2 - imageRect.height() / 2);
                rect.bottom = imageRect.height() / 2 + displayHeight / 2;
            } else {
                //Part
                int cutWidth = (int) ((double) imageRect.height() / displayAspect);
                if (isCenter) {
                    //Center
                    rect.left = imageRect.width() / 2 - cutWidth / 2;
                    rect.top = 0;
                } else {
                    //Start
                    rect.left = x;
                    rect.top = y;
                }
                rect.right = rect.left + cutWidth;
                rect.bottom = rect.top + imageRect.height();
            }
        }

        return rect;
    }

    /**
     * 更新移动后的裁剪Rect。
     *
     * @param displayRect 显示的区域，以图片为坐标轴。
     * @param offsetX     移动的X偏移量
     * @param offsetY     移动的Y偏移量
     * @param imageRect   图像Rect
     * @return 移动后的显示区域Rect
     */
    public Rect moveDisplayRect(
            @NonNull Rect displayRect,
            int offsetX,
            int offsetY,
            @NonNull Rect imageRect) {

        int displayWidth = displayRect.width();
        int displayHeight = displayRect.height();

        displayRect.offset(-offsetX, -offsetY);

        if (displayRect.left <= -displayWidth) {
            displayRect.left = -displayWidth + 1;
        }

        if (displayRect.top <= -displayHeight) {
            displayRect.top = -displayHeight + 1;
        }

        if (displayRect.left >= displayWidth) {
            displayRect.left = displayWidth - 1;
        }

        if (displayRect.top >= imageRect.height()) {
            displayRect.top = imageRect.height() - 1;
        }

        displayRect.right = displayRect.left + displayWidth;
        displayRect.bottom = displayRect.top + displayHeight;
        return displayRect;
    }

    /**
     * 初始化指示Bitmap中有效部分的Rect。
     *
     * @param displayRect 屏幕区域
     * @param bufferRect  缓存区域
     * @param imageRect   图片属性Rect
     * @return 初始化之后的SrcRect
     */
    private Rect initSrcRect(
            @NonNull Rect displayRect,
            @NonNull Rect bufferRect,
            @NonNull Rect imageRect) {
        Rect rect = new Rect();
        return updateSrcRect(rect, displayRect, bufferRect, imageRect);
    }

    /**
     * 更新指示Bitmap中有效部分的Rect。
     *
     * @param srcRect     需要更新的Rect
     * @param displayRect 屏幕区域
     * @param bufferRect  缓存区域
     * @param imageRect   图片属性Rect
     * @return 更新之后的SrcRect
     */
    private Rect updateSrcRect(
            @NonNull Rect srcRect,
            @NonNull Rect displayRect,
            @NonNull Rect bufferRect,
            @NonNull Rect imageRect) {
        float displayHeight = displayRect.height();
        float displayWidth = displayRect.width();
        int deltaX = displayRect.left - bufferRect.left;
        int deltaY = displayRect.top - bufferRect.top;

        //left
        if (displayRect.left >= 0) {
            //in image
            srcRect.left = deltaX;
        } else {
            //out of image
            srcRect.left = -displayRect.left + deltaX;
        }

        //top
        if (displayRect.top >= 0) {
            //in image
            srcRect.top = deltaY;
        } else {
            //out of image
            srcRect.top = -displayRect.top + deltaY;
        }

        //right
        if (displayRect.right <= imageRect.width()) {
            //in image
            srcRect.right = (int) displayWidth + deltaX;
        } else {
            //out of image
            srcRect.right = imageRect.width() - displayRect.left + deltaX;
        }

        //bottom
        if (displayRect.bottom <= imageRect.height()) {
            //in image
            srcRect.bottom = (int) displayHeight + deltaY;
        } else {
            //out of image
            srcRect.bottom = imageRect.height() - displayRect.top + deltaY;
        }

        return srcRect;
    }

    /**
     * 初始化用于显示的rect。
     * 该Rect对应于屏幕坐标系，代表了「在屏幕上的哪个位置需要作图」这一点。
     *
     * @param bufferRect  缓存的Rect
     * @param displayRect 显示区域Rect
     * @param srcRect     Bitmap中对应的部分Rect
     * @param screenRect  屏幕Rect
     * @return DestRect
     */
    private Rect initDestRect(
            @NonNull Rect bufferRect,
            @NonNull Rect displayRect,
            @NonNull Rect srcRect,
            @NonNull Rect screenRect) {
        Rect rect = new Rect();
        return updateDestRect(rect, bufferRect, displayRect, srcRect, screenRect);
    }

    /**
     * 更新有效显示区域的Rect。
     * 在该区域中会画上对应的Bitmap内容。
     *
     * @param destRect    需要更新的Rect
     * @param bufferRect  缓存的Rect
     * @param displayRect 显示区域Rect
     * @param srcRect     Bitmap中对应的部分Rect
     * @param screenRect  屏幕Rect
     * @return 更新后的DestRect
     */
    private Rect updateDestRect(
            @NonNull Rect destRect,
            @NonNull Rect bufferRect,
            @NonNull Rect displayRect,
            @NonNull Rect srcRect,
            @NonNull Rect screenRect) {

        Rect tempRect = new Rect(srcRect);
        tempRect.offset(bufferRect.left, bufferRect.top);

        float displayWidth = displayRect.width();
        float screenWidth = screenRect.width();
        float ratio = displayWidth / screenWidth;

        float deltaLeft = (tempRect.left - displayRect.left) / ratio;
        float deltaTop = (tempRect.top - displayRect.top) / ratio;
        float deltaRight = (displayRect.right - tempRect.right) / ratio;
        float deltaBottom = (displayRect.bottom - tempRect.bottom) / ratio;

        destRect.left = (int) deltaLeft;
        destRect.top = (int) deltaTop;
        destRect.right = screenRect.right - (int) deltaRight;
        destRect.bottom = screenRect.bottom - (int) deltaBottom;

        return destRect;
    }

    /**
     * 根据手指的移动来修改解码区域
     *
     * @param fingerDeltaX 手指移动的像素X
     * @param fingerDeltaY 手指移动的像素Y
     * @param bufferRect   缓存的区域
     * @param displayRect  显示区域在Buffer区域中的位置。
     * @param screenRect   表征屏幕属性的rect
     * @param imageRect    表征图片属性的rect
     * @return 移动指挥的displayRect
     */
    private Rect move(
            double fingerDeltaX,
            double fingerDeltaY,
            @NonNull Rect bufferRect,
            @NonNull Rect displayRect,
            @NonNull Rect screenRect,
            @NonNull Rect imageRect) {
        double scaleRatio = getScaledRatio(displayRect, screenRect);
        double displayDeltaX = fingerDeltaX / scaleRatio;
        double displayDeltaY = fingerDeltaY / scaleRatio;
        moveDisplayRect(displayRect, (int) displayDeltaX, (int) displayDeltaY, imageRect);
        if (updateBufferRect(bufferRect, displayRect)) {
            //Decode
            decode(mDecoder, mDecodeOptions, bufferRect);
        }
        updateSrcRect(mSrcRect, displayRect, bufferRect, imageRect);
        updateDestRect(mDestRect, bufferRect, displayRect, mSrcRect, screenRect);
        return displayRect;
    }

    /**
     * 使用给定的decoder按照option和rect进行区域解码。
     *
     * @param decoder    区域解码器
     * @param options    解码属性
     * @param bufferRect 解码区域
     */
    private void decode(
            @NonNull BitmapRegionDecoder decoder,
            @NonNull BitmapFactory.Options options,
            @NonNull Rect bufferRect) {
        long time = System.currentTimeMillis();
        if (options.inBitmap == null) {
            options.inBitmap = decoder.decodeRegion(bufferRect, options);
        } else {
            decoder.decodeRegion(bufferRect, options);
        }
        Timber.i("DECODE! Time = " + (System.currentTimeMillis() - time));
    }

    /**
     * 滑动控制器。
     */
    private class ScrollTimerTask extends TimerTask {

        public static final int POST_INTERVAL = 17;//1000/60

        private static final float ACCELERATION = 0.01f;

        private float mVelocityX, mVelocityY;

        public ScrollTimerTask(float velocityX, float velocityY) {
            mVelocityX = velocityX / 1000;
            mVelocityY = velocityY / 1000;
        }

        @Override
        public void run() {
            float deltaX = getDeltaMovement(mVelocityX, POST_INTERVAL, ACCELERATION);
            float deltaY = getDeltaMovement(mVelocityY, POST_INTERVAL, ACCELERATION);
            mVelocityX = getLaterSpeed(mVelocityX, POST_INTERVAL, ACCELERATION);
            mVelocityY = getLaterSpeed(mVelocityY, POST_INTERVAL, ACCELERATION);
            if (deltaX != 0 || deltaY != 0) {
                move(deltaX, deltaY, mBufferRect, mDisplayRect, mScreenRect,
                        mImageRect);
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
