package com.example.zipservice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ZipperScreenLockView extends View {
    private Context mContext;
    private Paint mPaint = null;
    private Paint mPaintMask = null;
    private Bitmap mBitmapZipper = null;
    private Bitmap mBitmapZipperLeft = null;
    private Bitmap mBitmapZipperRight = null;
    private Bitmap mBitmapZipperBg = null;
    private Bitmap mBitmapZipperMask = null;
    private Bitmap mBitmapBg = null;
    private double wSize = 0.0;
    private double hSize = 0.0;
    private double xTouch = 0.0;
    private double yTouch = 0.0;
    private boolean isTouch = false;
    private boolean isFirst = true;
    private double moveY = 0.0;
    private double wZipper = 0.0;
    private final boolean isShowBg = true;
    private IZipperListener listener = null;

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

    public void setCompleteListener(IZipperListener listener) {
        this.listener = listener;
    }

    public interface IZipperListener {
        void zipperSuccess();
        void zipperMoving();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initView(Context context) {
        mBitmapZipper = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.zip)).getBitmap();
        mBitmapZipperLeft = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.left)).getBitmap();
        mBitmapZipperRight = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.right)).getBitmap();
        mBitmapZipperBg = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.bg)).getBitmap();
        mBitmapZipperMask = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.bg)).getBitmap();
        mBitmapBg = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.bg)).getBitmap();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void setBitmapZipper(int resource) {
        mBitmapZipper = ((BitmapDrawable) mContext.getResources().getDrawable(resource)).getBitmap();
        isFirst = true;
        invalidate();
    }

    public void setBitmapZipperLeft(int resource) {
        mBitmapZipperLeft = ((BitmapDrawable) mContext.getResources().getDrawable(resource)).getBitmap();
        isFirst = true;
        invalidate();
    }

    public void setBitmapZipperRight(int resource) {
        mBitmapZipperRight = ((BitmapDrawable) mContext.getResources().getDrawable(resource)).getBitmap();
        isFirst = true;
        invalidate();
    }

    public void setBitmapZipperBg(int resource) {
        mBitmapZipperBg = ((BitmapDrawable) mContext.getResources().getDrawable(resource)).getBitmap();
        isFirst = true;
        invalidate();
    }

    public void setBitmapZipperMark(int resource) {
        mBitmapZipperMask = ((BitmapDrawable) mContext.getResources().getDrawable(resource)).getBitmap();
        isFirst = true;
        invalidate();
    }

    public void setBg(int resource) {
        mBitmapBg = ((BitmapDrawable) mContext.getResources().getDrawable(resource)).getBitmap();
        isFirst = true;
        invalidate();
    }

    private Bitmap setBitmapSize(Bitmap bitmap, double w, double h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        double newWidth = w;
        float scaleWidth = (float) (newWidth / width);
        float scaleHeight = (float) (h / height);
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isFirst) {
            wSize = (double) getWidth();
            hSize = (double) getHeight();

            mBitmapZipperBg = setBitmapSize(mBitmapZipperBg, wSize, wSize * ZIPPER_BG_HEIGHT / ZIPPER_BG_WIDTH);
            mBitmapZipperMask = setBitmapSize(mBitmapZipperMask, wSize, wSize * ZIPPER_MASK_HEIGHT / ZIPPER_MASK_WIDTH);
            mBitmapBg = setBitmapSize(mBitmapBg, wSize, wSize * BG_HEIGHT / BG_WIDTH);

            wZipper = wSize * ZIPPER_WIDTH / ZIPPER_BG_WIDTH;

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

                Rect mSrcRectZipperMask = new Rect(0, 0, mBitmapZipperMask.getWidth(), mBitmapZipperMask.getHeight());
                Rect mDestRectZipperMask = new Rect(
                        0,
                        INIT_HEIGHT_INT - mBitmapZipperMask.getHeight() + (int) (yTouch - moveY),
                        mBitmapZipperMask.getWidth(),
                        INIT_HEIGHT_INT + (int) (yTouch - moveY)
                );
                canvas.drawBitmap(mBitmapZipperMask, mSrcRectZipperMask, mDestRectZipperMask, mPaintMask);

                Rect mSrcRectZipperLeft = new Rect(0, 0, mBitmapZipperLeft.getWidth(), (int) (INIT_HEIGHT_INT + yTouch - moveY));
                Rect mDestRectZipperLeft = new Rect(
                        (int) (wSize / 2 - (INIT_HEIGHT_DOUBLE * mBitmapZipperLeft.getWidth() / mBitmapZipperLeft.getHeight() + ZIPPER_SCALE * wZipper) - (yTouch - moveY) * ZIPPER_LEFT_WIDTH_NO_ZIPPER / ZIPPER_LEFT_HEIGHT),
                        0,
                        (int) (wSize / 2 - (INIT_HEIGHT_DOUBLE * mBitmapZipperLeft.getWidth() / mBitmapZipperLeft.getHeight() + ZIPPER_SCALE * wZipper) + mBitmapZipperLeft.getWidth() - (yTouch - moveY) * ZIPPER_LEFT_WIDTH_NO_ZIPPER / ZIPPER_LEFT_HEIGHT),
                        (int) (INIT_HEIGHT_INT + yTouch - moveY)
                );
                canvas.drawBitmap(mBitmapZipperLeft, mSrcRectZipperLeft, mDestRectZipperLeft, mPaint);

                Rect mSrcRectZipperRight = new Rect(0, 0, mBitmapZipperRight.getWidth(), (int) (INIT_HEIGHT_INT + yTouch - moveY));
                Rect mDestRectZipperRight = new Rect(
                        (int) (wSize / 2 + (INIT_HEIGHT_DOUBLE * mBitmapZipperRight.getWidth() / mBitmapZipperRight.getHeight() + ZIPPER_SCALE * wZipper) - mBitmapZipperRight.getWidth() + (yTouch - moveY) * ZIPPER_LEFT_WIDTH_NO_ZIPPER / ZIPPER_LEFT_HEIGHT),
                        0,
                        (int) (wSize / 2 + (INIT_HEIGHT_DOUBLE * mBitmapZipperRight.getWidth() / mBitmapZipperRight.getHeight() + ZIPPER_SCALE * wZipper) + (yTouch - moveY) * ZIPPER_LEFT_WIDTH_NO_ZIPPER / ZIPPER_LEFT_HEIGHT),
                        (int) (INIT_HEIGHT_INT + yTouch - moveY)
                );
                canvas.drawBitmap(mBitmapZipperRight, mSrcRectZipperRight, mDestRectZipperRight, mPaint);

                canvas.drawBitmap(mBitmapZipper,
                        (float) (wSize / 2 - mBitmapZipper.getWidth() / 2),
                        (float) (yTouch - moveY),
                        mPaint
                );

                if (isShowBg) {
                    canvas.restoreToCount(i);
                }
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

        Rect mSrcRectZipperMask = new Rect(0, 0, mBitmapZipperMask.getWidth(), mBitmapZipperMask.getHeight());
        Rect mDestRectZipperMask = new Rect(
                0,
                INIT_HEIGHT_INT - mBitmapZipperMask.getHeight(),
                mBitmapZipperMask.getWidth(),
                INIT_HEIGHT_INT
        );
        canvas.drawBitmap(mBitmapZipperMask, mSrcRectZipperMask, mDestRectZipperMask, mPaintMask);

        Rect mSrcRectZipperLeft = new Rect(0, 0, mBitmapZipperLeft.getWidth(), INIT_HEIGHT_INT);
        Rect mDestRectZipperLeft = new Rect(
                (int) (wSize / 2 - (INIT_HEIGHT_DOUBLE * mBitmapZipperLeft.getWidth() / mBitmapZipperLeft.getHeight() + ZIPPER_SCALE * wZipper)),
                0,
                (int) (wSize / 2 - (INIT_HEIGHT_DOUBLE * mBitmapZipperLeft.getWidth() / mBitmapZipperLeft.getHeight() + ZIPPER_SCALE * wZipper) + mBitmapZipperLeft.getWidth()),
                INIT_HEIGHT_INT
        );
        canvas.drawBitmap(mBitmapZipperLeft, mSrcRectZipperLeft, mDestRectZipperLeft, mPaint);

        Rect mSrcRectZipperRight = new Rect(0, 0, mBitmapZipperRight.getWidth(), INIT_HEIGHT_INT);
        Rect mDestRectZipperRight = new Rect(
                (int) (wSize / 2 + (INIT_HEIGHT_DOUBLE * mBitmapZipperRight.getWidth() / mBitmapZipperRight.getHeight() + ZIPPER_SCALE * wZipper) - mBitmapZipperRight.getWidth()),
                0,
                (int) (wSize / 2 + (INIT_HEIGHT_DOUBLE * mBitmapZipperRight.getWidth() / mBitmapZipperRight.getHeight() + ZIPPER_SCALE * wZipper)),
                INIT_HEIGHT_INT
        );
        canvas.drawBitmap(mBitmapZipperRight, mSrcRectZipperRight, mDestRectZipperRight, mPaint);

        canvas.drawBitmap(mBitmapZipper, (float) (wSize / 2 - mBitmapZipper.getWidth() / 2), 0f, mPaint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        xTouch = event.getX();
        yTouch = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if ((xTouch > (wSize / 2 - mBitmapZipper.getWidth() / 2)) && (xTouch < (wSize / 2 + mBitmapZipper.getWidth() / 2)) && (yTouch > 0) && (yTouch < mBitmapZipper.getHeight())) {
                    isTouch = true;
                    moveY = yTouch;
                }
                if (isTouch) {
                    invalidate();
                }
                if (listener != null) listener.zipperMoving();
                break;

            case MotionEvent.ACTION_MOVE:
                if (isTouch) {
                    invalidate();

                    if (event.getY() > getHeight() - 80) {
                        if (listener != null) listener.zipperSuccess();
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                isTouch = false;
                invalidate();
                break;
        }
        return true;
    }

    private static final double ZIPPER_BG_WIDTH = 480.0;
    private static final double ZIPPER_BG_HEIGHT = 845.0;
    private static final double ZIPPER_MASK_WIDTH = 480.0;
    private static final double ZIPPER_MASK_HEIGHT = 1100.0;
    private static final double BG_WIDTH = 640.0;
    private static final double BG_HEIGHT = 1136.0;
    private static final double ZIPPER_WIDTH = 38.0;
    private static final double ZIPPER_LEFT_WIDTH = 458.0;
    private static final double ZIPPER_LEFT_HEIGHT = 1100.0;
    private static final double ZIPPER_LEFT_WIDTH_NO_ZIPPER = 401.0;
    private static final double LEFT_ZIPPER_WIDTH = 35.0;
    private static final int INIT_HEIGHT_INT = 50;
    private static final double INIT_HEIGHT_DOUBLE = 50.0;
    private static final double ZIPPER_SCALE = 1.32;
} 