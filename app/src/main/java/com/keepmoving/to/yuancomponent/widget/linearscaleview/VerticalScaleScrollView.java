package com.keepmoving.to.yuancomponent.widget.linearscaleview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by caihanyuan on 2017/9/27.
 * <p>
 * 垂直滑动刻度尺
 */
public class VerticalScaleScrollView extends BaseScaleView {

    public VerticalScaleScrollView(Context context) {
        super(context);
    }

    public VerticalScaleScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalScaleScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VerticalScaleScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void initVar() {
        super.initVar();
        mRectHeight = mScaleNums * mScaleMargin;
        mRectWidth = mScaleHeight * 8;
        mScaleMaxHeight = mScaleHeight * 2;

        mMinScroll = 0;
        mMaxScroll = mRectHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mScaleScrollViewRange = getMeasuredHeight();
        mOffset = (float) mScaleScrollViewRange / 2;
        mMidCountScale = mMin;
        mTempScale = mMidCountScale;

        if (mStartOverRange + mEndOverRange > mScaleScrollViewRange) {
            mUnderLineSize = mStartOverRange + mEndOverRange + mRectWidth;
        } else {
            mUnderLineSize = mScaleScrollViewRange + mRectWidth;
        }
    }

    @Override
    protected void onDrawLine(Canvas canvas, Paint paint) {
        canvas.drawLine(0, 0, 0, mUnderLineSize, paint);
    }

    @Override
    protected void onDrawScale(Canvas canvas, Paint paint) {
        paint.setTextSize(mRectWidth / 4);

        int factor = mAccuracy * 10;
        for (int i = 0, k = mMin; i <= mScaleNums; i++, k += mAccuracy) {
            float yPosition = mOffset + i * mScaleMargin;
            if (i % factor == 0) { //整值
                canvas.drawLine(0, yPosition, mScaleMaxHeight, yPosition, paint);
                //整值文字
                canvas.drawText(String.valueOf(k), mScaleMaxHeight + 40, yPosition + paint.getTextSize() / 3, paint);
            } else {
                canvas.drawLine(0, yPosition, mScaleHeight, yPosition, paint);
            }
        }

        //画范围外刻度
        if (mMin - mOuterMin > 0) {
            int outerNums = (mMin - mOuterMin) / mAccuracy;
            for (int i = 1, k = mMin - mAccuracy; i <= outerNums; i++, k -= mAccuracy) {
                float yPosition = mOffset - i * mScaleMargin;
                if (k % factor == 0) { //整值
                    canvas.drawLine(0, yPosition, mScaleMaxHeight, yPosition, paint);
                    //整值文字
                    canvas.drawText(String.valueOf(k), mScaleMaxHeight + 40, yPosition + paint.getTextSize() / 3, paint);
                } else {
                    canvas.drawLine(0, yPosition, mScaleHeight, yPosition, paint);
                }
            }
        }
    }

    @Override
    protected void onDrawPointer(Canvas canvas, Paint paint) {

        paint.setColor(Color.RED);

        //每一屏幕刻度的个数/2
        int offestScale = 0;
        //根据滑动的距离，计算指针的位置【指针始终位于屏幕中间】
        int currentY = getScrollY();
        //滑动的刻度
        int tmpCountScale = (int) Math.rint((double) currentY / (double) mScaleMargin); //四舍五入取整
        //总刻度
        mCountScale = (tmpCountScale + offestScale) * mAccuracy + mMin;
        if (mScrollListener != null) { //回调方法
            mScrollListener.onScaleScroll(mCountScale);
        }


        if (!mIsDown && mScroller.isFinished()) {
            int finalY = ((mCountScale - mMidCountScale) / mAccuracy) * mScaleMargin;
            float center = mOffset + finalY;
            canvas.drawLine(0, center, mScaleMaxHeight + mScaleHeight, center, paint);
        } else {
            float center = mOffset + currentY;
            canvas.drawLine(0, center, mScaleMaxHeight + mScaleHeight, center, paint);
        }
    }

    @Override
    public void scrollToScale(int val) {
        if (val < mMin || val > mMax) {
            return;
        }
        int dy = (val - mCountScale) / mAccuracy * mScaleMargin;
        smoothScrollBy(0, dy);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        int oldX = getScrollX();
        int oldY = getScrollY();
        scrollTo(scrollX, scrollY);
        onScrollChanged(scrollX, scrollY, oldX, oldY);
    }

    @Override
    protected boolean onDown(MotionEvent e) {
        if (mScroller != null && !mScroller.isFinished()) {
            mScroller.forceFinished(true);
            postInvalidate();
        }
        return true;
    }

    @Override
    protected boolean onUp(MotionEvent e) {
        if (mIsOver) {
            int scrollY = getScrollY();
            boolean isStart = scrollY < mMinScroll;
            mScrollListener.onOverScrolled(isStart);
            mScroller.springBack(0, scrollY, 0, 0, mMinScroll, mMaxScroll);
        }
        postInvalidate();
        return true;
    }

    @Override
    protected boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        int dataY = (int) distanceY;
        getCanScrollDistance(dataY);
        smoothScrollBy(0, dataY);
        postInvalidate();
        mTempScale = mCountScale;
        return false;
    }

    @Override
    protected boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (velocityY > MAX_VELOCITY) {
            velocityY = MAX_VELOCITY;
        }
        float distanceY = Math.abs(e1.getY() - e2.getY());
        if (distanceY >= MIN_DISTANCE && Math.abs(velocityY) >= MIN_VELOCITY) {
            mScroller.fling(0, getScrollY(), 0, -(int) velocityY, 0, 0, 0, mRectHeight);
            postInvalidate();
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected int getCanScrollDistance(int distance) {
        int finalPostion = getScrollY() + distance;
        if (finalPostion < mMinScroll) {
            distance = mMinScroll - getScrollY();
            mIsOver = true;
        } else if (finalPostion > mMaxScroll) {
            distance = mMaxScroll - getScrollY();
            mIsOver = true;
        } else {
            mIsOver = false;
        }
        return distance;
    }
}
