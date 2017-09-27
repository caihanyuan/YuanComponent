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
        mMidCountScale = (mScaleScrollViewRange / mScaleMargin / 2) * mAccuracy + mMin;
        mTempScale = mMidCountScale;
    }

    @Override
    protected void onDrawLine(Canvas canvas, Paint paint) {
        canvas.drawLine(0, mRectHeight, mRectWidth, mRectHeight, paint);
    }

    @Override
    protected void onDrawScale(Canvas canvas, Paint paint) {
        paint.setTextSize(mRectHeight / 4);

        int factor = mAccuracy * 10;
        for (int i = 0, k = mMin; i <= mScaleNums; i++, k += mAccuracy) {
            if (k % factor == 0) { //整值
                canvas.drawLine(i * mScaleMargin, mRectHeight, i * mScaleMargin, mMaxScaleTop, paint);
                //整值文字
                canvas.drawText(String.valueOf(k), i * mScaleMargin, mTextTop, paint);
            } else {
                canvas.drawLine(i * mScaleMargin, mRectHeight, i * mScaleMargin, mNormalScaleTop, paint);
            }
        }

    }

    @Override
    protected void onDrawPointer(Canvas canvas, Paint paint) {
        //每一屏幕刻度的个数/2
        int countScale = mScaleScrollViewRange / mScaleMargin / 2;
        //根据滑动的距离，计算指针的位置【指针始终位于屏幕中间】
        int finalX = mScroller.getFinalX();
        //滑动的刻度
        int tmpCountScale = (int) Math.rint((double) finalX / (double) mScaleMargin); //四舍五入取整
        //总刻度
        mCountScale = (tmpCountScale + countScale) * mAccuracy + mMin;
        if (mScrollListener != null) { //回调方法
            mScrollListener.onScaleScroll(mCountScale);
        }

        if (mScroller.isFinished()) {
            canvas.drawLine(countScale * mScaleMargin + finalX, mRectHeight,
                    countScale * mScaleMargin + finalX, mPointerTop, paint);
        } else {
            float center = mScaleScrollViewRange / 2 + finalX;
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
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mScroller != null && !mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mScrollLastX = x;
                return true;
            case MotionEvent.ACTION_MOVE:
                int dataX = mScrollLastX - x;
                if (mCountScale - mTempScale < 0) { //向右边滑动
                    if (mCountScale <= mMin && dataX <= 0) //禁止继续向右滑动
                        return super.onTouchEvent(event);
                } else if (mCountScale - mTempScale > 0) { //向左边滑动
                    if (mCountScale >= mMax && dataX >= 0) //禁止继续向左滑动
                        return super.onTouchEvent(event);
                }
                smoothScrollBy(dataX, 0, 0);
                mScrollLastX = x;
                postInvalidate();
                mTempScale = mCountScale;
                return true;
            case MotionEvent.ACTION_UP:
                if (mCountScale < mMin) mCountScale = mMin;
                if (mCountScale > mMax) mCountScale = mMax;
                int finalX = ((mCountScale - mMidCountScale) / mAccuracy) * mScaleMargin;
                mScroller.setFinalX(finalX); //纠正指针位置
                postInvalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

}
