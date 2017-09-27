package com.keepmoving.to.yuancomponent.widget.linearscaleview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Scroller;

import com.keepmoving.to.yuancomponent.R;


/**
 * Created by caihanyuan on 2017/9/27.
 * <p>
 * 可滑动刻度尺基类
 */
public abstract class BaseScaleView extends View {

    protected int mMax; //最大刻度
    protected int mMin; // 最小刻度
    protected int mCountScale; //滑动的总刻度
    protected int mDefalutColor; //刻度尺颜色
    protected int mPointerColor; //指针颜色
    protected int mAccuracy; //刻度尺精度值
    protected int mScaleNums; //总的刻度个数
    protected int mTextMargin; //刻度文字和刻度之间的距离

    protected int mScaleScrollViewRange;

    protected int mScaleMargin; //刻度间距
    protected int mScaleHeight; //刻度线的高度
    protected int mScaleMaxHeight; //整刻度线高度

    protected int mRectWidth; //总宽度
    protected int mRectHeight; //总高度

    protected Scroller mScroller;
    protected int mScrollLastX;
    protected Paint mPaint;

    protected int mTempScale; // 用于判断滑动方向
    protected int mMidCountScale; //中间刻度

    protected OnScrollListener mScrollListener;

    public interface OnScrollListener {
        void onScaleScroll(int scale);
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
        mAccuracy = ta.getInteger(R.styleable.YUAN_LinearScaleView_scale_view_accuracy, 1);
        mScaleMargin = ta.getDimensionPixelOffset(R.styleable.YUAN_LinearScaleView_scale_view_margin, 15);
        mScaleHeight = ta.getDimensionPixelOffset(R.styleable.YUAN_LinearScaleView_scale_view_height, 20);
        mDefalutColor = ta.getColor(R.styleable.YUAN_LinearScaleView_scale_view_defalut_color, Color.GRAY);
        mPointerColor = ta.getColor(R.styleable.YUAN_LinearScaleView_scale_view_defalut_color, Color.RED);
        mTextMargin = ta.getDimensionPixelSize(R.styleable.YUAN_LinearScaleView_scale_view_text_margin,
                dip2px(getContext(), 8));
        ta.recycle();

        mScroller = new Scroller(getContext());

        mPaint = new Paint();
        // 抗锯齿
        mPaint.setAntiAlias(true);
        // 设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
        mPaint.setDither(true);
        // 空心
        mPaint.setStyle(Paint.Style.STROKE);
        // 文字居中
        mPaint.setTextAlign(Paint.Align.CENTER);

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

    // 画线
    protected abstract void onDrawLine(Canvas canvas, Paint paint);

    // 画刻度
    protected abstract void onDrawScale(Canvas canvas, Paint paint);

    // 画指针
    protected abstract void onDrawPointer(Canvas canvas, Paint paint);

    // 滑动到指定刻度
    public abstract void scrollToScale(int val);

    public void setCurScale(int val) {
        if (val >= mMin && val <= mMax) {
            scrollToScale(val);
            postInvalidate();
        }
    }

    /**
     * 使用Scroller时需重写
     */
    @Override
    public void computeScroll() {
        // 判断Scroller是否执行完毕
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            // 通过重绘来不断调用computeScroll
            invalidate();
        }
    }

    public void smoothScrollBy(int dx, int dy) {
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy);
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
     * 设置回调监听
     *
     * @param listener
     */
    public void setOnScrollListener(OnScrollListener listener) {
        this.mScrollListener = listener;
    }
}
