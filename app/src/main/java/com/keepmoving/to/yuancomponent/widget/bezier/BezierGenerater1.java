package com.keepmoving.to.yuancomponent.widget.bezier;

import android.animation.FloatEvaluator;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 生成贝塞尔曲线-迭代法
 * <p/>
 */
public class BezierGenerater1 extends View {

    private Paint paint;
    private int centerX, centerY;
    private List<PointF> points;
    private FloatEvaluator evaluator;
    private float fraction;
    private Map<Integer, Integer> colors;
    private List<PointF> destPoints;

    public BezierGenerater1(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        evaluator = new FloatEvaluator();
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
        colors = new HashMap<>();
        for (int i = 0; i < points.size(); i++) {
            colors.put(i, getRanColor());
        }
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
        List<PointF> subData = getSubData(points, fraction);
        drawData(canvas, subData, fraction);
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

    //迭代绘制集合
    private void drawData(Canvas canvas, List<PointF> data, float fraction) {
        if (data.size() == 1) {
            drawPoint(canvas, data, Color.BLACK);
            destPoints.add(data.get(0));
            drawPath(canvas, destPoints);
        } else {
            drawLine(canvas, data, colors.get(data.size() - 1));
            //迭代
            List<PointF> subData = getSubData(data, fraction);
            drawData(canvas, subData, fraction);
        }
    }

    private void startAnim() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 100);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(5000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                fraction = animation.getAnimatedFraction();
                invalidate();
            }

        });
        animator.start();
    }

    //生成随机颜色
    private int getRanColor() {
        return 0xff000000 | new Random().nextInt(0x00ffffff);
    }

    //获取子数据源
    private List<PointF> getSubData(List<PointF> data, float fraction) {
        List<PointF> subData = new ArrayList<>();
        for (int i = 0; i < data.size() - 1; i++) {
            PointF start = data.get(i);
            PointF end = data.get(i + 1);
            float x = evaluator.evaluate(fraction, start.x, end.x);
            float y = evaluator.evaluate(fraction, start.y, end.y);
            subData.add(new PointF(x, y));
        }
        return subData;
    }
}