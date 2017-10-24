package com.keepmoving.to.yuancomponent.widget.bezier;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by caihanyuan on 2017/10/24.
 * <p>
 * 贝塞尔曲线画圆
 */

public class BezierCircle extends View {

    int mRadius = 100;
    float mDistance = 100 * 0.55228f;

    Paint mPaint;
    Path mPath;

    PointF[] mDataPoint = new PointF[4];
    PointF[] mControlPoint = new PointF[8];

    int mCenterX;
    int mCenterY;

    public BezierCircle(Context context) {
        super(context);
    }

    public BezierCircle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BezierCircle(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BezierCircle(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);

        mPath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mCenterX = getMeasuredWidth() / 2;
        mCenterY = getMeasuredHeight() / 2;

        PointF pointF = new PointF(mCenterX, mCenterY - mRadius);
        mDataPoint[0] = pointF;
        pointF = new PointF(mCenterX + mRadius, mCenterY);
        mDataPoint[1] = pointF;
        pointF = new PointF(mCenterX, mCenterY + mRadius);
        mDataPoint[2] = pointF;
        pointF = new PointF(mCenterX - mRadius, mCenterY);
        mDataPoint[3] = pointF;

        pointF = new PointF(mCenterX + mDistance, mCenterY - mRadius);
        mControlPoint[0] = pointF;
        pointF = new PointF(mCenterX + mRadius, mCenterY - mDistance);
        mControlPoint[1] = pointF;
        pointF = new PointF(mCenterX + mRadius, mCenterY + mDistance);
        mControlPoint[2] = pointF;
        pointF = new PointF(mCenterX + mDistance, mCenterY + mRadius);
        mControlPoint[3] = pointF;
        pointF = new PointF(mCenterX - mDistance, mCenterY + mRadius);
        mControlPoint[4] = pointF;
        pointF = new PointF(mCenterX - mRadius, mCenterY + mDistance);
        mControlPoint[5] = pointF;
        pointF = new PointF(mCenterX - mRadius, mCenterY - mDistance);
        mControlPoint[6] = pointF;
        pointF = new PointF(mCenterX - mDistance, mCenterY - mRadius);
        mControlPoint[7] = pointF;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPath.moveTo(mDataPoint[0].x, mDataPoint[0].y);
        mPath.cubicTo(mControlPoint[0].x, mControlPoint[0].y, mControlPoint[1].x, mControlPoint[1].y, mDataPoint[1].x, mDataPoint[1].y);
        mPath.cubicTo(mControlPoint[2].x, mControlPoint[2].y, mControlPoint[3].x, mControlPoint[3].y, mDataPoint[2].x, mDataPoint[2].y);
        mPath.cubicTo(mControlPoint[4].x, mControlPoint[4].y, mControlPoint[5].x, mControlPoint[5].y, mDataPoint[3].x, mDataPoint[3].y);
        mPath.cubicTo(mControlPoint[6].x, mControlPoint[6].y, mControlPoint[7].x, mControlPoint[7].y, mDataPoint[0].x, mDataPoint[0].y);
        mPath.close();

        canvas.drawPath(mPath, mPaint);
    }
}
