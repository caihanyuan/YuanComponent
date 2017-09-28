package com.keepmoving.to.yuancomponent.widget.linearscaleview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;

import com.keepmoving.to.yuancomponent.R;


/**
 * Created by caihanyuan on 2017/9/27.
 * <p>
 * 可滑动刻度尺基类
 */
public abstract class BaseScaleView extends View {
    public static final String TAG = BaseScaleView.class.getSimpleName();
    protected static final int MIN_VELOCITY = 500; //触发快速滑动的最小速度
    protected static final int MAX_VELOCITY = Integer.MAX_VALUE; //最大滑动速度
    protected static final int MIN_DISTANCE = 85; //触发快速滑动的最小距离

    protected int mMax; //最大刻度
    protected int mMin; // 最小刻度
    protected int mOuterMin; //不可选择的最小刻度，在最小刻度外
    protected int mOuterMax; //不可选择的最大刻度，在最大刻度外
    protected int mMaxScroll; //最大刻度位置
    protected int mMinScroll; //最小刻度位置
    protected int mCountScale; //滑动的总刻度
    protected int mDefalutColor; //刻度尺颜色
    protected int mPointerColor; //指针颜色
    protected int mAccuracy; //刻度尺精度值
    protected int mScaleNums; //总的刻度个数
    protected int mTextMargin; //刻度文字和刻度之间的距离
    protected float mOffset; //第一个刻度开始的位置
    protected int mStartOverRange; //左边可超过滑动的最大范围
    protected int mEndOverRange; //右边可超过滑动的最大范围

    protected int mScaleScrollViewRange;

    protected int mScaleMargin; //刻度间距
    protected int mScaleHeight; //刻度线的高度
    protected int mScaleMaxHeight; //整刻度线高度
    protected int mUnderLineSize; //刻度底线长度

    protected int mRectWidth; //总宽度
    protected int mRectHeight; //总高度

    protected int mTempScale; // 用于判断滑动方向
    protected int mMidCountScale; //中间刻度

    protected OverScroller mScroller; //滑动辅助类
    protected Paint mPaint;

    protected boolean mIsDown; //是否按下
    protected boolean mIsOver; //是否滑动超出边界

    protected OnScrollListener mScrollListener;
    protected GestureDetector mGestureDetector;

    public interface OnScrollListener {
        void onScaleScroll(int scale);

        void onOverScrolled(boolean isStart);
    }

    public BaseScaleView(Context context) {
        super(context);
        init(null);
    }

    public BaseScaleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public BaseScaleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BaseScaleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    protected void init(AttributeSet attrs) {
        // 获取自定义属性
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.YUAN_LinearScaleView);
        mMin = ta.getInteger(R.styleable.YUAN_LinearScaleView_scale_view_min, 0);
        mMax = ta.getInteger(R.styleable.YUAN_LinearScaleView_scale_view_max, 200);
        mOuterMin = ta.getInteger(R.styleable.YUAN_LinearScaleView_scale_view_outer_min, mMin);
        mOuterMax = ta.getInteger(R.styleable.YUAN_LinearScaleView_scale_view_outer_max, mMax);
        mMax = ta.getInteger(R.styleable.YUAN_LinearScaleView_scale_view_max, 200);
        mAccuracy = ta.getInteger(R.styleable.YUAN_LinearScaleView_scale_view_accuracy, 1);
        mScaleMargin = ta.getDimensionPixelOffset(R.styleable.YUAN_LinearScaleView_scale_view_margin, 15);
        mScaleHeight = ta.getDimensionPixelOffset(R.styleable.YUAN_LinearScaleView_scale_view_height, 20);
        mDefalutColor = ta.getColor(R.styleable.YUAN_LinearScaleView_scale_view_defalut_color, Color.GRAY);
        mPointerColor = ta.getColor(R.styleable.YUAN_LinearScaleView_scale_view_pointer_color, Color.RED);
        mTextMargin = ta.getDimensionPixelSize(R.styleable.YUAN_LinearScaleView_scale_view_text_margin,
                dip2px(getContext(), 8));

        int defaultStartRange = (mMin - mOuterMin) / mAccuracy * mScaleMargin;
        mStartOverRange = ta.getDimensionPixelSize(R.styleable.YUAN_LinearScaleView_scale_view_start_over_range,
                defaultStartRange);

        int defaultEndRange = (mOuterMax - mMax) / mAccuracy * mScaleMargin;
        defaultStartRange = defaultEndRange == 0 ? defaultStartRange : defaultEndRange;
        mEndOverRange = ta.getDimensionPixelOffset(R.styleable.YUAN_LinearScaleView_scale_view_end_over_range,
                defaultEndRange);

        ta.recycle();

        mScroller = new OverScroller(getContext());

        mPaint = new Paint();
        // 抗锯齿
        mPaint.setAntiAlias(true);
        // 设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
        mPaint.setDither(true);
        // 空心
        mPaint.setStyle(Paint.Style.STROKE);
        // 文字居中
        mPaint.setTextAlign(Paint.Align.CENTER);

        //手势相关初始化
        GestureListener guestureListener = new GestureListener();
        mGestureDetector = new GestureDetector(getContext(), guestureListener);

        initVar();
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(mDefalutColor);
        onDrawLine(canvas, mPaint);
        onDrawScale(canvas, mPaint); //画刻度

        mPaint.setColor(mPointerColor);
        onDrawPointer(canvas, mPaint); //画指针
    }

    protected void initVar() {
        mScaleNums = ((mMax - mMin) / mAccuracy);
    }

    /**
     * 画底部线条
     *
     * @param canvas
     * @param paint
     */
    protected abstract void onDrawLine(Canvas canvas, Paint paint);

    /**
     * 画刻度线
     *
     * @param canvas
     * @param paint
     */
    protected abstract void onDrawScale(Canvas canvas, Paint paint);

    /**
     * 画中心指针
     *
     * @param canvas
     * @param paint
     */
    protected abstract void onDrawPointer(Canvas canvas, Paint paint);

    /**
     * 滑动到指定刻度
     *
     * @param val
     */
    public abstract void scrollToScale(int val);

    public void setCurScale(int val) {
        if (val >= mMin && val <= mMax) {
            scrollToScale(val);
            postInvalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mGestureDetector.onTouchEvent(event)) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                mIsDown = false;
                if (mIsOver) {
//                    onOverScroll(mScroller.getCurrX(), mScroller.getCurrY());
                }
                return onUp(event);
            }
            return super.onTouchEvent(event);
        }

        return true;
    }

    /**
     * 重写OverScrollBy方法，让其支持头尾超出边界不同
     *
     * @param deltaX
     * @param deltaY
     * @param scrollX
     * @param scrollY
     * @param scrollRangeX
     * @param scrollRangeY
     * @param maxStartOverScrollX
     * @param maxEndOverScorllX
     * @param maxStartOverScrollY
     * @param maxEndOverScrollY
     * @param isTouchEvent
     * @return
     */
    protected boolean overScrollBy(int deltaX, int deltaY,
                                   int scrollX, int scrollY,
                                   int scrollRangeX, int scrollRangeY,
                                   int maxStartOverScrollX, int maxEndOverScorllX,
                                   int maxStartOverScrollY, int maxEndOverScrollY,
                                   boolean isTouchEvent) {
        final int overScrollMode = getOverScrollMode();
        final boolean canScrollHorizontal =
                computeHorizontalScrollRange() > computeHorizontalScrollExtent();
        final boolean canScrollVertical =
                computeVerticalScrollRange() > computeVerticalScrollExtent();
        final boolean overScrollHorizontal = overScrollMode == OVER_SCROLL_ALWAYS ||
                (overScrollMode == OVER_SCROLL_IF_CONTENT_SCROLLS && canScrollHorizontal);
        final boolean overScrollVertical = overScrollMode == OVER_SCROLL_ALWAYS ||
                (overScrollMode == OVER_SCROLL_IF_CONTENT_SCROLLS && canScrollVertical);

        int newScrollX = scrollX + deltaX;
        if (!overScrollHorizontal) {
            maxStartOverScrollX = 0;
            maxEndOverScorllX = 0;
        }

        int newScrollY = scrollY + deltaY;
        if (!overScrollVertical) {
            maxStartOverScrollY = 0;
            maxEndOverScrollY = 0;
        }

        // Clamp values if at the limits and record
        final int left = -maxStartOverScrollX;
        final int right = maxEndOverScorllX + scrollRangeX;
        final int top = -maxStartOverScrollY;
        final int bottom = maxEndOverScrollY + scrollRangeY;

        boolean clampedX = false;
        if (newScrollX > right) {
            newScrollX = right;
            clampedX = true;
        } else if (newScrollX < left) {
            newScrollX = left;
            clampedX = true;
        }

        boolean clampedY = false;
        if (newScrollY > bottom) {
            newScrollY = bottom;
            clampedY = true;
        } else if (newScrollY < top) {
            newScrollY = top;
            clampedY = true;
        }

        onOverScrolled(newScrollX, newScrollY, clampedX, clampedY);

        return clampedX || clampedY;
    }

    /**
     * 使用Scroller时需重写
     */
    @Override
    public void computeScroll() {
        // 判断Scroller是否执行完毕
        if (mScroller.computeScrollOffset()) {
            int oldX = getScrollX();
            int oldY = getScrollY();
            int currentX = mScroller.getCurrX();
            int currentY = mScroller.getCurrY();
            int scrollRange = mMaxScroll - mMinScroll;
            overScrollBy(currentX - oldX, currentY - oldY, oldX, oldY, scrollRange, scrollRange,
                    mStartOverRange, mEndOverRange, mStartOverRange, mStartOverRange, false);

            // 通过重绘来不断调用computeScroll
            invalidate();
        }
    }

    public void smoothScrollBy(int dx, int dy) {
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy, 0);
    }

    public void smoothScrollBy(int dx, int dy, int duration) {
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy, duration);
    }

    public void smoothScrollTo(int fx, int fy) {
        int dx = fx - mScroller.getFinalX();
        int dy = fy - mScroller.getFinalY();
        smoothScrollBy(dx, dy);
    }

    /**
     * 获取合适的滑动距离，边界控制
     *
     * @param distance
     * @return
     */
    protected int getCanScrollDistance(int distance) {
        return distance;
    }

    /**
     * 设置回调监听
     *
     * @param listener
     */
    public void setOnScrollListener(OnScrollListener listener) {
        this.mScrollListener = listener;
    }

    /**
     * 触摸按下
     *
     * @param e
     * @return
     */
    protected boolean onDown(MotionEvent e) {
        return false;
    }

    /**
     * 触摸抬起
     *
     * @param e
     * @return
     */
    protected boolean onUp(MotionEvent e) {
        return false;
    }

    /**
     * 快速滑动
     *
     * @param e1
     * @param e2
     * @param velocityX
     * @param velocityY
     */
    protected boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    /**
     * 普通滑动
     *
     * @param e1
     * @param e2
     * @param distanceX
     * @param distanceY
     * @return
     */
    protected boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    /**
     * 滑动超出边界
     */
    protected void onOverScroll(float currentX, float currentY) {

    }

    /**
     * 手势监听器
     */
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            mIsDown = true;
            return BaseScaleView.this.onDown(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            mIsDown = false;
            return BaseScaleView.this.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return BaseScaleView.this.onScroll(e1, e2, distanceX, distanceY);
        }

    }
}
