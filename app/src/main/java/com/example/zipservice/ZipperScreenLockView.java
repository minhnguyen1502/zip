package com.example.zipservice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;


public class ZipperScreenLockView extends View {

    private Context mContext;
    private Paint mPaint;
    private Paint mPaintMask;
    private Bitmap mBitmapZipper;
    private Bitmap mBitmapZipperLeft;
    private Bitmap mBitmapZipperRight;
    private Bitmap mBitmapZipperBg;
    private Bitmap mBitmapZipperMask;
    private Bitmap mBitmapBg;
    private Bitmap mBitmapRow;

    private double mZipperWidth = 0.0;
    private double wSize = 0.0;
    private double hSize = 0.0;
    private double xTouch = 0.0;
    private double yTouch = 0.0;
    private boolean isTouch = false;
    private boolean isFirst = true;
    private double moveY = 0.0;
    private double wZipper = 0.0;
    private double wRow = 0.0;
    private double wZip = 0.0;
    private final boolean isShowBg = false;
    private double lastY = -1.0;
    private int maskHeight = 1;

    private Handler handler = new Handler();
    private final Runnable checkMovementRunnable = new Runnable() {
        @Override
        public void run() {
            if (isTouch) {
                if (Math.abs(lastY - yTouch) < 5) {
                    if (listener != null) listener.zipperCancel();
                } else {
                    lastY = yTouch;
                    if (listener != null) listener.zipperMoving();
                    Log.e("zipcheck", "playing sound");
                }
                handler.postDelayed(this, 50);
            }
        }
    };

    private ZipperScreenLockView.IZipperListener listener;

    private static final double ZIPPER_BG_WIDTH = 480.0;
    private static final double ZIPPER_BG_HEIGHT = 845.0;
    private static final double ZIPPER_MASK_WIDTH = 480.0;
    private static final double ZIPPER_MASK_HEIGHT = 1100.0;
    private static final double BG_WIDTH = 640.0;
    private static final double ROW_HEIGHT = 800.0;
    private static final double ROW_WIDTH = 100.0;
    private static final double ZIP_WIDTH = 90.0;
    private static final double BG_HEIGHT = 1136.0;
    private static final double ZIPPER_WIDTH = 32.0;
    private static final double ZIPPER_LEFT_WIDTH = 445.0;
    private static final double ZIPPER_LEFT_HEIGHT = 1100.0;
    private static final double ZIPPER_LEFT_WIDTH_NO_ZIPPER = 385.0;
    private static final double LEFT_ZIPPER_WIDTH = 35.0;
    private static final int INIT_HEIGHT_INT = 50;
    private static final double INIT_HEIGHT_DOUBLE = 50.0;
    private static final double ZIPPER_SCALE = 1.21;

    public ZipperScreenLockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
        initView(context);
    }

    public ZipperScreenLockView(Context context) {
        super(context);
        mContext = context;
        init();
        initView(context);
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setFilterBitmap(true);
        mPaint.setDither(true);
        mPaintMask = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintMask.setAntiAlias(true);
        mPaintMask.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
    }

    public void setCompleteListener(ZipperScreenLockView.IZipperListener listener) {
        this.listener = listener;
    }

    public interface IZipperListener {
        void zipperSuccess();

        void zipperMoving();

        //        void zipperPlayZipperSound();
        void zipperCancel();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initView(Context context) {
        mBitmapZipper = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.zipper_1)).getBitmap();
        mBitmapZipperLeft = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.row_left_1)).getBitmap();
        mBitmapZipperRight = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.row_right_1)).getBitmap();
        mBitmapZipperBg = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.bg)).getBitmap();
        mBitmapZipperMask = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.bg_mask2__2___1_)).getBitmap();
        mBitmapRow = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.row_1__1_)).getBitmap();
        mBitmapBg = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.img_default)).getBitmap();
    }

    /*-------------------------Set data for zipper---------------------------------------------------------------*/
    @SuppressLint("UseCompatLoadingForDrawables")
    public void setBitmapZipper(int resource) {
//        mBitmapZipper = ((BitmapDrawable) mContext.getResources().getDrawable(resource)).getBitmap();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        mBitmapZipper = BitmapFactory.decodeResource(mContext.getResources(), resource, options);
        isFirst = true;
        invalidate();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void setBitmapRow(int resource) {
        mBitmapRow = ((BitmapDrawable) mContext.getResources().getDrawable(resource)).getBitmap();
        isFirst = true;
        invalidate();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void setBitmapZipperLeft(int resource) {
        mBitmapZipperLeft = ((BitmapDrawable) mContext.getResources().getDrawable(resource)).getBitmap();
        isFirst = true;
        invalidate();
    }

    public void setZipperWidth(double zipperWidth) {
        mZipperWidth = zipperWidth;
        isFirst = true;
        invalidate();
    }

    public void setMaskHeight(int height) {
        maskHeight = height;
        isFirst = true;
        invalidate();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void setBitmapZipperRight(int resource) {
        mBitmapZipperRight = ((BitmapDrawable) mContext.getResources().getDrawable(resource)).getBitmap();
        isFirst = true;
        invalidate();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void setBitmapZipperBg(int resource) {
        mBitmapZipperBg = ((BitmapDrawable) mContext.getResources().getDrawable(resource)).getBitmap();
        isFirst = true;
        invalidate();
    }

    public void setBitmapZipperBg(Bitmap bitmap) {
        mBitmapZipperBg = bitmap;
        isFirst = true;
        invalidate();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void setBg(int resource) {
        mBitmapBg = ((BitmapDrawable) mContext.getResources().getDrawable(resource)).getBitmap();
        isFirst = true;
        invalidate();
    }
    /*--------------------------------------------------------------------------------------------------------------*/

    private Bitmap setBitmapSize(Bitmap bitmap, double w, double h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = (float) (w / width);
        float scaleHeight = (float) (h / height);
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }

    @Override
    @SuppressLint("DrawAllocation")
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (isFirst) {
            wSize = getWidth();
            hSize = getHeight();
            mBitmapZipperBg = setBitmapSize(mBitmapZipperBg, wSize, wSize * ZIPPER_BG_HEIGHT / ZIPPER_BG_WIDTH);
            mBitmapZipperMask = setBitmapSize(mBitmapZipperMask, wSize, wSize * ZIPPER_MASK_HEIGHT / ZIPPER_MASK_WIDTH);
            mBitmapBg = setBitmapSize(mBitmapBg, wSize, wSize * BG_HEIGHT / BG_WIDTH);
            wZipper = wSize * ZIPPER_WIDTH / ZIPPER_BG_WIDTH;
            wRow = hSize * ROW_HEIGHT / BG_HEIGHT;
            wZip = wRow * ROW_WIDTH / ROW_HEIGHT;
            if (mZipperWidth != 0) {
                wZip = wZip * mBitmapZipper.getWidth() / mZipperWidth;
            }
            mBitmapZipper = setBitmapSize(mBitmapZipper, wZip, wZip * mBitmapZipper.getHeight() / mBitmapZipper.getWidth());
            mBitmapRow = setBitmapSize(mBitmapRow, wRow * ROW_WIDTH / ROW_HEIGHT, wRow);
            mBitmapZipperLeft = setBitmapSize(
                    mBitmapZipperLeft,
                    wZipper * ZIPPER_LEFT_WIDTH / LEFT_ZIPPER_WIDTH,
                    wZipper * ZIPPER_LEFT_HEIGHT / LEFT_ZIPPER_WIDTH
            );
            mBitmapZipperRight = setBitmapSize(
                    mBitmapZipperRight,
                    wZipper * ZIPPER_LEFT_WIDTH / LEFT_ZIPPER_WIDTH,
                    wZipper * ZIPPER_LEFT_HEIGHT / LEFT_ZIPPER_WIDTH
            );
            isFirst = false;
        }
        if (isTouch) {
            if ((yTouch - moveY) > 0) {
                if (isShowBg) {
                    Rect mSrcRectBg = new Rect(0, 0, mBitmapBg.getWidth(), mBitmapBg.getHeight());
                    Rect mDestRectBg = new Rect(0, 0, (int) wSize, (int) hSize);
                    canvas.drawBitmap(mBitmapBg, mSrcRectBg, mDestRectBg, mPaint);
                }

                int i = 0;
                if (isShowBg) {
                    i = canvas.saveLayer(0f, 0f, (float) wSize, (float) hSize, mPaint, Canvas.ALL_SAVE_FLAG);
                }

                Rect mSrcRectZipperBg = new Rect(0, 0, mBitmapZipperBg.getWidth(), mBitmapZipperBg.getHeight());
                Rect mDestRectZipperBg = new Rect(0, 0, (int) wSize, (int) hSize);
                canvas.drawBitmap(mBitmapZipperBg, mSrcRectZipperBg, mDestRectZipperBg, mPaint);

                Rect mSrcRectRow = new Rect(0, 0, mBitmapRow.getWidth(), mBitmapRow.getHeight());
                Rect mDestRectRow = new Rect((int) wSize / 2 - mBitmapRow.getWidth() / 2, 0, (int) wSize / 2 + mBitmapRow.getWidth() / 2, (int) hSize);
                canvas.drawBitmap(mBitmapRow, mSrcRectRow, mDestRectRow, mPaint);

                Rect mSrcRectZipperMask = new Rect(0, 0, mBitmapZipperMask.getWidth(), mBitmapZipperMask.getHeight());
                Rect mDestRectZipperMask = new Rect(0, INIT_HEIGHT_INT * maskHeight - mBitmapZipperMask.getHeight() + (int) (yTouch - moveY),
                        mBitmapZipperMask.getWidth(), INIT_HEIGHT_INT * maskHeight + (int) (yTouch - moveY));
                canvas.drawBitmap(mBitmapZipperMask, mSrcRectZipperMask, mDestRectZipperMask, mPaintMask);

                Rect mSrcRectZipperLeft = new Rect(0, 0, mBitmapZipperLeft.getWidth(), (int) (INIT_HEIGHT_INT * maskHeight + yTouch - moveY));
                Rect mDestRectZipperLeft = new Rect(
                        (int) (wSize / 2 - (INIT_HEIGHT_DOUBLE * mBitmapZipperLeft.getWidth() / mBitmapZipperLeft.getHeight()
                                + ZIPPER_SCALE * wZipper) - (yTouch - moveY) * ZIPPER_LEFT_WIDTH_NO_ZIPPER / ZIPPER_LEFT_HEIGHT),
                        0,
                        (int) (wSize / 2 - (INIT_HEIGHT_DOUBLE * mBitmapZipperLeft.getWidth() / mBitmapZipperLeft.getHeight()
                                + ZIPPER_SCALE * wZipper) + mBitmapZipperLeft.getWidth()
                                - (yTouch - moveY) * ZIPPER_LEFT_WIDTH_NO_ZIPPER / ZIPPER_LEFT_HEIGHT),
                        (int) (INIT_HEIGHT_INT * maskHeight + yTouch - moveY));
                canvas.drawBitmap(mBitmapZipperLeft, mSrcRectZipperLeft, mDestRectZipperLeft, mPaint);

                Rect mSrcRectZipperRight = new Rect(0, 0, mBitmapZipperRight.getWidth(), (int) (INIT_HEIGHT_INT * maskHeight + yTouch - moveY));
                Rect mDestRectZipperRight = new Rect(
                        (int) (wSize / 2 + (INIT_HEIGHT_DOUBLE * mBitmapZipperRight.getWidth() / mBitmapZipperRight.getHeight()
                                + ZIPPER_SCALE * wZipper) - mBitmapZipperRight.getWidth()
                                + (yTouch - moveY) * ZIPPER_LEFT_WIDTH_NO_ZIPPER / ZIPPER_LEFT_HEIGHT),
                        0,
                        (int) (wSize / 2 + (INIT_HEIGHT_DOUBLE * mBitmapZipperRight.getWidth() / mBitmapZipperRight.getHeight()
                                + ZIPPER_SCALE * wZipper) + (yTouch - moveY) * ZIPPER_LEFT_WIDTH_NO_ZIPPER / ZIPPER_LEFT_HEIGHT),
                        (int) (INIT_HEIGHT_INT * maskHeight + yTouch - moveY));
                canvas.drawBitmap(mBitmapZipperRight, mSrcRectZipperRight, mDestRectZipperRight, mPaint);

                canvas.drawBitmap(mBitmapZipper, (float) (wSize / 2 - mBitmapZipper.getWidth() / 2), (float) (yTouch - moveY), mPaint);

                if (isShowBg) canvas.restoreToCount(i);
            } else {
                initState(canvas);
            }
        } else {
            initState(canvas);
        }
    }

    private void initState(Canvas canvas) {
        Rect mSrcRectZipperBg = new Rect(0, 0, mBitmapZipperBg.getWidth(), mBitmapZipperBg.getHeight());
        Rect mDestRectZipperBg = new Rect(0, 0, (int) wSize, (int) hSize);
        canvas.drawBitmap(mBitmapZipperBg, mSrcRectZipperBg, mDestRectZipperBg, mPaint);

        Rect mSrcRectRow = new Rect(0, 0, mBitmapRow.getWidth(), mBitmapRow.getHeight());
        Rect mDestRectRow = new Rect((int) wSize / 2 - mBitmapRow.getWidth() / 2, 0, (int) wSize / 2 + mBitmapRow.getWidth() / 2, (int) hSize);
        canvas.drawBitmap(mBitmapRow, mSrcRectRow, mDestRectRow, mPaint);

        Rect mSrcRectZipperMask = new Rect(0, 0, mBitmapZipperMask.getWidth(), mBitmapZipperMask.getHeight());
        Rect mDestRectZipperMask = new Rect(0, INIT_HEIGHT_INT * maskHeight - mBitmapZipperMask.getHeight(),
                mBitmapZipperMask.getWidth(), INIT_HEIGHT_INT * maskHeight);
        canvas.drawBitmap(mBitmapZipperMask, mSrcRectZipperMask, mDestRectZipperMask, mPaintMask);

        Rect mSrcRectZipperLeft = new Rect(0, 0, mBitmapZipperLeft.getWidth(), INIT_HEIGHT_INT * maskHeight);
        Rect mDestRectZipperLeft = new Rect(
                (int) (wSize / 2 - (INIT_HEIGHT_DOUBLE * mBitmapZipperLeft.getWidth() / mBitmapZipperLeft.getHeight() + ZIPPER_SCALE * wZipper)),
                0,
                (int) (wSize / 2 - (INIT_HEIGHT_DOUBLE * mBitmapZipperLeft.getWidth() / mBitmapZipperLeft.getHeight() + ZIPPER_SCALE * wZipper) + mBitmapZipperLeft.getWidth()),
                INIT_HEIGHT_INT * maskHeight);
        canvas.drawBitmap(mBitmapZipperLeft, mSrcRectZipperLeft, mDestRectZipperLeft, mPaint);

        Rect mSrcRectZipperRight = new Rect(0, 0, mBitmapZipperRight.getWidth(), INIT_HEIGHT_INT * maskHeight);
        Rect mDestRectZipperRight = new Rect(
                (int) (wSize / 2 + (INIT_HEIGHT_DOUBLE * mBitmapZipperRight.getWidth() / mBitmapZipperRight.getHeight() + ZIPPER_SCALE * wZipper) - mBitmapZipperRight.getWidth()),
                0,
                (int) (wSize / 2 + (INIT_HEIGHT_DOUBLE * mBitmapZipperRight.getWidth() / mBitmapZipperRight.getHeight() + ZIPPER_SCALE * wZipper)),
                INIT_HEIGHT_INT * maskHeight);
        canvas.drawBitmap(mBitmapZipperRight, mSrcRectZipperRight, mDestRectZipperRight, mPaint);

        canvas.drawBitmap(mBitmapZipper, (float) (wSize / 2 - mBitmapZipper.getWidth() / 2), 0f, mPaint);
    }

    private void startMovementCheck() {
        handler.post(checkMovementRunnable);
    }

    private void stopMovementCheck() {
        lastY = 0;
        handler.removeCallbacks(checkMovementRunnable);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        xTouch = event.getX();
        yTouch = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (xTouch > (wSize / 2 - (double) mBitmapZipper.getWidth() / 2) && xTouch < (wSize / 2 + (double) mBitmapZipper.getWidth() / 2)
                        && yTouch > 0 && yTouch < mBitmapZipper.getHeight()) {
                    isTouch = true;
                    moveY = yTouch;
                    lastY = yTouch;
                    if (listener != null) listener.zipperMoving();
                    startMovementCheck();
                }
                if (isTouch) {
                    invalidate();
                }

                break;

            case MotionEvent.ACTION_MOVE:
                if (isTouch) {
                    invalidate();
                    if (event.getY() > getHeight() - 80)
                        if (listener != null) listener.zipperSuccess();
                }
                break;

            case MotionEvent.ACTION_UP:
                isTouch = false;
                stopMovementCheck();
                if (listener != null) listener.zipperCancel();
                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopMovementCheck();
    }
}