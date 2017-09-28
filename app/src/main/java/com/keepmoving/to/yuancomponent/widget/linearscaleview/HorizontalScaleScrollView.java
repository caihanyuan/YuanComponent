package com.keepmoving.to.yuancomponent.widget.linearscaleview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by caihanyuan on 2017/9/27.
 * <p>
 * 水平滚动刻度尺
 */
public class HorizontalScaleScrollView extends BaseScaleView {

    private int mTextTop;
    private int mNormalScaleTop;
    private int mMaxScaleTop;
    private int mPointerTop;

    public HorizontalScaleScrollView(Context context) {
        super(context);
    }

    public HorizontalScaleScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalScaleScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public HorizontalScaleScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void initVar() {
        super.initVar();
        mRectWidth = mScaleNums * mScaleMargin;
        mRectHeight = mScaleHeight * 8;
        mScaleMaxHeight = mScaleHeight * 2;

        mMinScroll = 0;
        mMaxScroll = mRectWidth;

        mTextTop = mRectHeight - mScaleMaxHeight - mTextMargin;
        mNormalScaleTop = mRectHeight - mScaleHeight;
        mMaxScaleTop = mRectHeight - mScaleMaxHeight;
        mPointerTop = mRectHeight - mScaleMaxHeight - mScaleHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.makeMeasureSpec(mRectHeight, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, height);
        mScaleScrollViewRange = getMeasuredWidth();
        mOffset = ((float) mScaleScrollViewRange) / 2;
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
        canvas.drawLine(0, mRectHeight, mUnderLineSize, mRectHeight, paint);
    }

    @Override
    protected void onDrawScale(Canvas canvas, Paint paint) {
        paint.setTextSize(mRectHeight / 4);

        int factor = mAccuracy * 10;
        for (int i = 0, k = mMin; i <= mScaleNums; i++, k += mAccuracy) {
            float xPosition = mOffset + i * mScaleMargin;
            if (k % factor == 0) { //整值
                canvas.drawLine(xPosition, mRectHeight, xPosition, mMaxScaleTop, paint);
                //整值文字
                canvas.drawText(String.valueOf(k), xPosition, mTextTop, paint);
            } else {
                canvas.drawLine(xPosition, mRectHeight, xPosition, mNormalScaleTop, paint);
            }
        }

        //画范围外刻度
        if (mMin - mOuterMin > 0) {
            int outerNums = (mMin - mOuterMin) / mAccuracy;
            for (int i = 1, k = mMin - mAccuracy; i <= outerNums; i++, k -= mAccuracy) {
                float xPosition = mOffset - i * mScaleMargin;
                if (k % factor == 0) { //整值
                    canvas.drawLine(xPosition, mRectHeight, xPosition, mMaxScaleTop, paint);
                    //整值文字
                    canvas.drawText(String.valueOf(k), xPosition, mTextTop, paint);
                } else {
                    canvas.drawLine(xPosition, mRectHeight, xPosition, mNormalScaleTop, paint);
                }
            }
        }
        if (mOuterMax - mMax > 0) {
            int outerNums = (mOuterMax - mMax) / mAccuracy;
            float startPosition = mOffset + mRectWidth;
            for (int i = 1, k = mMax + mAccuracy; i <= outerNums; i++, k += mAccuracy) {
                float xPosition = startPosition + i * mScaleMargin;
                if (k % factor == 0) { //整值
                    canvas.drawLine(xPosition, mRectHeight, xPosition, mMaxScaleTop, paint);
                    //整值文字
                    canvas.drawText(String.valueOf(k), xPosition, mTextTop, paint);
                } else {
                    canvas.drawLine(xPosition, mRectHeight, xPosition, mNormalScaleTop, paint);
                }
            }
        }
    }

    @Override
    protected void onDrawPointer(Canvas canvas, Paint paint) {
        int offestScale = 0;
        //根据滑动的距离，计算指针的位置【指针始终位于屏幕中间】
        int currentX = getScrollX();
        //滑动的刻度
        int tmpCountScale = (int) Math.rint((double) currentX / (double) mScaleMargin); //四舍五入取整
        //总刻度
        mCountScale = (tmpCountScale + offestScale) * mAccuracy + mMin;
        if (mScrollListener != null) { //回调方法
            mScrollListener.onScaleScroll(mCountScale);
        }

        if (!mIsDown && mScroller.isFinished()) {
            int finalX = ((mCountScale - mMidCountScale) / mAccuracy) * mScaleMargin;
            float center = mOffset + finalX;
            canvas.drawLine(center, mRectHeight, center, mPointerTop, paint);
        } else {
            float center = mOffset + currentX;
            canvas.drawLine(center, mRectHeight, center, mPointerTop, paint);
        }
    }

    @Override
    public void scrollToScale(int val) {
        if (val < mMin || val > mMax) {
            return;
        }
        int dx = (val - mCountScale) / mAccuracy * mScaleMargin;
        smoothScrollBy(dx, 0);
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
            int scrollX = getScrollX();
            boolean isStart = scrollX < mMinScroll;
            mScrollListener.onOverScrolled(isStart);
            mScroller.springBack(scrollX, 0, mMinScroll, mMaxScroll, 0, 0);
        }
        postInvalidate();
        return true;
    }

    @Override
    protected boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        int dataX = (int) distanceX;
        getCanScrollDistance(dataX);
        smoothScrollBy(dataX, 0);
        postInvalidate();
        mTempScale = mCountScale;
        return false;
    }

    @Override
    protected boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (velocityX > MAX_VELOCITY) {
            velocityX = MAX_VELOCITY;
        }
        float distanceX = Math.abs(e1.getX() - e2.getX());
        if (distanceX >= MIN_DISTANCE && Math.abs(velocityX) >= MIN_VELOCITY) {
            mScroller.fling(getScrollX(), 0, -(int) velocityX, 0, 0, mRectWidth, 0, 0);
            postInvalidate();
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected int getCanScrollDistance(int distance) {
        int finalPostion = getScrollX() + distance;
        if (finalPostion < mMinScroll) {
            distance = mMinScroll - getScrollX();
            mIsOver = true;
        } else if (finalPostion > mMaxScroll) {
            distance = mMaxScroll - getScrollX();
            mIsOver = true;
        } else {
            mIsOver = false;
        }

        return distance;
    }
}
