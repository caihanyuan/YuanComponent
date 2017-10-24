package com.keepmoving.to.yuancomponent.widget.bezier;

import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.keepmoving.to.yuancomponent.R;

/**
 * 贝塞尔曲线-气泡拖拽
 * <p/>
 * 二条2阶贝塞尔曲线
 */
public class BubbleDragView extends View {

    private int mRaduis;//气泡的半径
    private int mMaxDistance;//拉断的距离
    private int mDuration; //放手动画时间

    private double mDistance;//记录拉动的距离
    private double mRaduisTmp;//临时的半径
    private double mDistanceBackup;

    private PointF mP1, mP2;//圆心
    private PointF mPA, mPB, mPC, mPD, mPX;//控制点
    private PointF mP2Backup;
    private Paint mPaint;
    private Path mPath;

    private ValueAnimator mAnimator;

    public BubbleDragView(Context context) {
        this(context, null);
    }

    public BubbleDragView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleDragView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BubbleDragView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    //初始化圆心的位置
    private void init(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.YUAN_BubbleDragView);

        mRaduis = typedArray.getDimensionPixelSize(R.styleable.YUAN_BubbleDragView_radius, 40);
        mMaxDistance = typedArray.getDimensionPixelOffset(R.styleable.YUAN_BubbleDragView_max_distance, 400);
        int color = typedArray.getColor(R.styleable.YUAN_BubbleDragView_bubble_color, 0xffff0000);
        mDuration = typedArray.getInt(R.styleable.YUAN_BubbleDragView_duration, 100);

        typedArray.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(color);
        mPaint.setStrokeWidth(4);
        mPaint.setStyle(Paint.Style.FILL);

        mPath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpec = MeasureSpec.makeMeasureSpec(mRaduis * 2, MeasureSpec.AT_MOST);
        int heightSpec = MeasureSpec.makeMeasureSpec(mRaduis * 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int centerX = left + getWidth() / 2;
        int centerY = top + getHeight() / 2;

        mP1 = new PointF(centerX, centerY);
        mP2 = new PointF(centerX, centerY);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnim();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                mP2.x = event.getX();
                mP2.y = event.getY();
                mDistance = PointUtil.getDistance(mP1, mP2);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (mDistance < mMaxDistance) {
                    mP2Backup = new PointF(mP2.x, mP2.y);
                    mDistanceBackup = mDistance;
                    startAnim();
                }
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDistance < mMaxDistance) {
            calculatePoint();
            drawBefore(canvas);
        } else {
            drawAfter(canvas);
        }
    }

    //计算控制点的位置
    private void calculatePoint() {
        double raduisScale = 1 - mDistance / mMaxDistance;
        mRaduisTmp = mRaduis * raduisScale;

        mPA = PointUtil.getRotatePoint(mP1, mP2, mRaduisTmp, Math.PI / 2);
        mPB = PointUtil.getRotatePoint(mP1, mP2, mRaduisTmp, -Math.PI / 2);
        mPC = PointUtil.getRotatePoint(mP2, mP1, mRaduis, -Math.PI / 2);
        mPD = PointUtil.getRotatePoint(mP2, mP1, mRaduis, Math.PI / 2);
        mPX = PointUtil.getCenterPoint(mP1, mP2);
    }

    //拉断之后的绘制
    private void drawAfter(Canvas canvas) {
        canvas.drawCircle(mP2.x, mP2.y, mRaduis, mPaint);
    }

    //拉断之前的绘制
    private void drawBefore(Canvas canvas) {
        //圆形
        canvas.drawCircle(mP1.x, mP1.y, (float) mRaduisTmp, mPaint);
        canvas.drawCircle(mP2.x, mP2.y, mRaduis, mPaint);

        mPath.rewind();
        //贝塞尔曲线
        mPath.moveTo(mPA.x, mPA.y);
        mPath.quadTo(mPX.x, mPX.y, mPD.x, mPD.y);
        mPath.lineTo(mPC.x, mPC.y);
        mPath.quadTo(mPX.x, mPX.y, mPB.x, mPB.y);
        mPath.lineTo(mPA.x, mPA.y);
        mPath.close();
        canvas.drawPath(mPath, mPaint);
    }


    //放开之后，如果没有拉断，就恢复初始状态
    private void startAnim() {
        stopAnim();
        final FloatEvaluator evaluator = new FloatEvaluator();
        mAnimator = ValueAnimator.ofFloat(0, 100);
        mAnimator.setInterpolator(new OvershootInterpolator());
        mAnimator.setDuration(mDuration);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                mP2.x = evaluator.evaluate(fraction, mP2Backup.x, mP1.x);
                mP2.y = evaluator.evaluate(fraction, mP2Backup.y, mP1.y);
                mDistance = evaluator.evaluate(fraction, mDistanceBackup, 0);
                invalidate();
            }

        });
        mAnimator.start();
    }

    private void stopAnim() {
        if (mAnimator != null) {
            mAnimator.cancel();
            mAnimator = null;
        }
    }
}