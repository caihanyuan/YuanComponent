package com.keepmoving.to.yuancomponent.widget.bezier;

import android.graphics.PointF;

/**
 * 点计算工具类
 * <p/>
 * 作者：余天然 on 16/6/15 下午2:33
 */
public class PointUtil {

    //获取旋转后的点
    public static PointF getRotatePoint(PointF p1, PointF p2, double raduis, double radians) {
        double oldRadians = getPointDegree(p1, p2);
        double newRadians = oldRadians + radians;
        float x = (float) (raduis * Math.cos(newRadians));
        float y = (float) (raduis * Math.sin(newRadians));
        return new PointF(p1.x + x, p1.y + y);
    }

    //获取中间的点
    public static PointF getCenterPoint(PointF p1, PointF p2) {
        float x = (p1.x + p2.x) / 2;
        float y = (p1.y + p2.y) / 2;
        return new PointF(x, y);
    }

    //获取两点的角度-返回的是弧度制
    public static double getPointDegree(PointF p1, PointF p2) {
        double scale = (p2.y - p1.y) / (p2.x - p1.x);
        return Math.atan(scale);
    }

    //获取两点的距离
    public static double getDistance(PointF start, PointF end) {
        return Math.sqrt((end.y - start.y) * (end.y - start.y) + (end.x - start.x) * (end.x - start.x));
    }
}