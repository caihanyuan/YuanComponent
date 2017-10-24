package com.keepmoving.to.yuancomponent.widget.bezier;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * 生成贝塞尔曲线-De Casteljau算法
 * <p/>
 */
public class BezierGenerater2 extends View {

    private Paint paint;
    private int centerX, centerY;
    private List<PointF> points;
    private float fraction;
    private List<PointF> destPoints;

    public BezierGenerater2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
    }

    //初始化数据点和控制点的位置
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;
        points = new ArrayList<>();
        points.add(new PointF(centerX - 150, centerY));
        points.add(new PointF(centerX - 50, centerY - 300));
        points.add(new PointF(centerX + 50, centerY + 300));
        points.add(new PointF(centerX + 150, centerY));
        destPoints = new ArrayList<>();
        destPoints.add(points.get(0));
        startAnim();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //静态的
        drawPoint(canvas, points, Color.BLACK);
        drawLine(canvas, points, Color.GRAY);
        //动态的
        drawPath(canvas, destPoints);
    }

    // 绘制数据点
    private void drawPoint(Canvas canvas, List<PointF> data, int color) {
        paint.setColor(color);
        paint.setStrokeWidth(20);
        for (int i = 0; i < data.size(); i++) {
            PointF pointF = data.get(i);
            canvas.drawPoint(pointF.x, pointF.y, paint);
        }
    }

    //绘制基准线
    private void drawLine(Canvas canvas, List<PointF> data, int color) {
        paint.setColor(color);
        paint.setStrokeWidth(4);
        for (int i = 0; i < data.size() - 1; i++) {
            PointF start = data.get(i);
            PointF end = data.get(i + 1);
            canvas.drawLine(start.x, start.y, end.x, end.y, paint);
        }
    }

    //绘制路径
    private void drawPath(Canvas canvas, List<PointF> data) {
        Path path = new Path();
        PointF start = data.get(0);
        path.moveTo(start.x, start.y);
        for (int i = 1; i < data.size() - 1; i++) {
            PointF point = data.get(i);
            path.lineTo(point.x, point.y);
        }
        paint.setColor(Color.RED);
        paint.setStrokeWidth(4);
        canvas.drawPath(path, paint);
    }

    private void startAnim() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 100);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(5000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                fraction = animation.getAnimatedFraction();
                PointF pointF = deCasteljau(fraction, points);
                destPoints.add(pointF);
                invalidate();
            }
        });
        animator.start();
    }

    //深复制
    private List<PointF> copyData(List<PointF> points) {
        List<PointF> data = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            PointF point = points.get(i);
            data.add(new PointF(point.x, point.y));
        }
        return data;
    }

    //De Casteljau算法
    public PointF deCasteljau(float fraction, List<PointF> points) {
        List<PointF> data = copyData(points);
        final int n = data.size();
        for (int i = 1; i <= n; i++) {
            for (int j = 0; j < n - i; j++) {
                data.get(j).x = (1 - fraction) * data.get(j).x + fraction * data.get(j + 1).x;
                data.get(j).y = (1 - fraction) * data.get(j).y + fraction * data.get(j + 1).y;
            }
        }
        return data.get(0);
    }

}