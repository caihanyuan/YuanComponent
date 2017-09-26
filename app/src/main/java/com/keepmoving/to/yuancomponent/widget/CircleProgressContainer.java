package com.keepmoving.to.yuancomponent.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by caihanyuan on 2017/9/25.
 */

public class CircleProgressContainer extends ViewGroup {

    private static final String TAG = CircleProgressContainer.class.getSimpleName();

    private int mProgressColor = Color.BLUE;
    private int mDefaultColor = Color.GRAY;

    protected double mProgress;
    protected double mMaxProgress = -1;

    private TextView mTitleText;
    private TextView mCurrentProgressText;
    private TextView mMaxProgressText;

    public CircleProgressContainer(Context context) {
        this(context, null);
    }

    public CircleProgressContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CircleProgressContainer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }


    /**
     * 设置当前可用数值, 默认没有动画
     *
     * @param progress
     */
    public void setCurrentProgress(double progress) {
        setCurrentProgress(progress, false);
    }

    /**
     * 设置当前可用数值
     *
     * @param progress
     * @param anim     是否需要显示动画
     */
    public void setCurrentProgress(double progress, boolean anim) {
        mProgress = progress;
        setCurrentProgressText();
    }

    /**
     * 设置当前最大数值, 默认没有动画
     *
     * @param maxProgress
     */
    public void setMaxProgress(double maxProgress) {
        setMaxProgress(maxProgress, false);
    }

    /**
     * 设置当前最大数值
     *
     * @param maxProgress
     * @param anim
     */
    public void setMaxProgress(double maxProgress, boolean anim) {
        mMaxProgress = maxProgress;
        setMaxProgressText();
    }


    /**
     * 设置头部提示文字
     *
     * @param titleText
     */
    public void setTitleText(CharSequence titleText) {

    }

    /**
     * 设置头部提示文字
     *
     * @param resId
     */
    public void setTitleText(int resId) {

    }


    /**
     * 设置可用数值提示文本
     */
    protected void setCurrentProgressText() {

    }

    /**
     * 设置最大数值提示文本
     */
    protected void setMaxProgressText() {

    }
}
