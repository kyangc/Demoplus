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
import android.view.View;

import java.io.IOException;
import java.io.InputStream;

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
     * 手势检测器
     */
    private MoveGestureDetector mDetector;

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
                        moveCutRegion(moveX, moveY, mCutRect, mDisplayRect);
                        invalidate();
                        return true;
                    }
                });
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

        //Init Cut rect
        mCutRect = getInitCutRect(0, 0, mImageHeight, mImageWidth,
                mScreenHeight, mScreenWidth, false, false);

        //Update sample size
        mDecodeOptions.inSampleSize = getSampleSize(mCutRect, mDisplayRect);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        decode(mDecoder, mDecodeOptions, mCutRect);
        canvas.drawBitmap(mDecodeOptions.inBitmap, mDisplayRect, mDisplayRect, null);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        destroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);
        return true;
    }

    /**
     * 将输入流显示在view上。
     *
     * @param is 输入流
     */
    public void setInputStream(InputStream is) {
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
                if (is != null) {
                    is.close();
                }
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
     * 更新移动后的裁剪Rect。
     *
     * @param cutRect     裁剪Rect
     * @param offsetX     X轴偏移量
     * @param offsetY     Y轴偏移量
     * @param cutWidth    裁剪宽度
     * @param displayRect 显示Rect
     * @return 更新过的Rect
     */
    public Rect updateCutRect(@NonNull Rect cutRect, int offsetX, int offsetY, int cutWidth,
            @NonNull Rect displayRect) {
        cutRect.offset(-offsetX, -offsetY);

        double dispWidth = displayRect.width();
        double dispHeight = displayRect.height();

        if (dispHeight == 0) {
            return cutRect;
        }

        double ratio = dispHeight / dispWidth;
        double cutHeight = cutWidth * ratio;

        cutRect.right = cutRect.left + cutWidth;
        cutRect.bottom = cutRect.top + (int) cutHeight;

        return cutRect;
    }

    /**
     * 根据手指的移动来修改解码区域
     *
     * @param fingerDeltaX 手指移动的距离X坐标
     * @param fingerDeltaY 手指移动的距离Y坐标
     * @param cutRect      裁剪区域
     * @param displayRect  显示区域
     */
    private Rect moveCutRegion(double fingerDeltaX, double fingerDeltaY, Rect cutRect,
            Rect displayRect) {
        double scaleRatio = getScaledRatio(cutRect, displayRect);
        double cutDeltaX = fingerDeltaX / scaleRatio;
        double cutDeltaY = fingerDeltaY / scaleRatio;
        return updateCutRect(cutRect, (int) cutDeltaX, (int) cutDeltaY, cutRect.width(),
                displayRect);
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
        return updateCutRect(cutRect, 0, 0, 0, displayRect);// TODO: 16/1/11 UPDATE
    }

    /**
     * 使用给定的decoder按照option和rect进行区域解码。
     *
     * @param decoder 解码器
     * @param options 解码选项
     * @param cutRect 解码区域
     */
    private void decode(BitmapRegionDecoder decoder, BitmapFactory.Options options, Rect cutRect) {
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
     * @param cutRect     裁剪区域
     * @param displayRect 显示区域
     * @return 放缩倍数
     */
    private double getScaledRatio(@NonNull Rect cutRect, @NonNull Rect displayRect) {
        return (double) displayRect.width() / (double) cutRect.width();
    }
}
