package com.keepmoving.to.yuancomponent.widget.circleprogress;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.keepmoving.to.yuancomponent.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 环形进度条, 带动画
 * Created by caihanyuan on 2017/9/25.
 */

public class CircleProgressView extends View {

    private int mProgressColor = Color.BLUE;
    private int mDefaultColor = Color.GRAY;
    private int mRingWidth = 25;
    private boolean mShowAnim = false;
    private int mDuration = 1000;

    private double mTmpProgress = 0;
    protected double mProgress;
    protected double mMaxProgress = -1;

    private RectF mCircleArea;
    private Paint mPaint;

    private ValueAnimator mAnimator;
    private TimeInterpolator mInterpolator;
    private TypeEvaluator mEvaluator;
    private AnimatorLifeListener mAnimatorLifeListener;
    private AnimatorUpdateListener mAnimatorUpdateListener;
    private List<ValueAnimator.AnimatorUpdateListener> mOuterUpdateListeners;

    public CircleProgressView(Context context) {
        this(context, null);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CircleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mShowAnim) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    startAnim();
                }
            }, 150);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        stopAnim();
        mOuterUpdateListeners.clear();
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Yuan_CircleProgressView);
        mProgressColor = typedArray.getColor(R.styleable.Yuan_CircleProgressView_set_color, mProgressColor);
        mDefaultColor = typedArray.getColor(R.styleable.Yuan_CircleProgressView_default_color, mDefaultColor);
        mRingWidth = typedArray.getDimensionPixelSize(R.styleable.Yuan_CircleProgressView_ring_width, mRingWidth);
        mShowAnim = typedArray.getBoolean(R.styleable.Yuan_CircleProgressView_show_anim, mShowAnim);
        mDuration = typedArray.getInt(R.styleable.Yuan_CircleProgressView_anim_duration, mDuration);
        typedArray.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mRingWidth);
        mPaint.setAntiAlias(true);
        mPaint.setColor(mProgressColor);

        mEvaluator = new DoubleEvaluator();
        mOuterUpdateListeners = new ArrayList<>();
        mAnimatorLifeListener = new AnimatorLifeListener();
        mAnimatorUpdateListener = new AnimatorUpdateListener();
        mInterpolator = new AccelerateDecelerateInterpolator();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        int width = layoutParams.width;
        int widthSpecSize = 0;
        switch (widthMode) {
            case MeasureSpec.UNSPECIFIED:
                if (width == ViewGroup.LayoutParams.MATCH_PARENT) {
                    widthSpecSize = 0;
                } else if (width == ViewGroup.LayoutParams.WRAP_CONTENT) {
                    widthSpecSize = 0;
                } else {
                    widthSpecSize = width;
                }
                break;
            case MeasureSpec.AT_MOST:
                if (width == ViewGroup.LayoutParams.MATCH_PARENT) {
                    widthSpecSize = widthSize;
                } else if (width == ViewGroup.LayoutParams.WRAP_CONTENT) {
                    widthSpecSize = widthSize;
                } else {
                    widthSpecSize = width;
                }
                break;
            case MeasureSpec.EXACTLY:
                if (width == ViewGroup.LayoutParams.MATCH_PARENT) {
                    widthSpecSize = widthSize;
                } else if (width == ViewGroup.LayoutParams.WRAP_CONTENT) {
                    widthSpecSize = widthSize;
                } else {
                    widthSpecSize = width;
                }
                break;
        }

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = layoutParams.height;
        int heightSpecSize = 0;
        switch (heightMode) {
            case MeasureSpec.UNSPECIFIED:
                if (height == ViewGroup.LayoutParams.MATCH_PARENT) {
                    heightSpecSize = 0;
                } else if (height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                    heightSpecSize = 0;
                } else {
                    heightSpecSize = height;
                }
                break;
            case MeasureSpec.AT_MOST:
                if (height == ViewGroup.LayoutParams.MATCH_PARENT) {
                    heightSpecSize = heightSize;
                } else if (height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                    heightSpecSize = heightSize;
                } else {
                    heightSpecSize = height;
                }
                break;
            case MeasureSpec.EXACTLY:
                if (height == ViewGroup.LayoutParams.MATCH_PARENT) {
                    heightSpecSize = heightSize;
                } else if (height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                    heightSpecSize = heightSize;
                } else {
                    heightSpecSize = height;
                }
                break;
        }

        widthSpecSize = Math.max(getSuggestedMinimumWidth(), widthSpecSize);
        heightSpecSize = Math.max(getSuggestedMinimumHeight(), heightSpecSize);

        setMeasuredDimension(widthSpecSize, heightSpecSize);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int halfRingWidth = mRingWidth / 2;
        mCircleArea = new RectF(halfRingWidth, halfRingWidth,
                getWidth() - halfRingWidth, getHeight() - halfRingWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int startAngle = 90;
        int progressAngle = (int) ((mTmpProgress * 360) / mMaxProgress);
        mPaint.setColor(mProgressColor);
        canvas.drawArc(mCircleArea, startAngle, progressAngle, false, mPaint);

        startAngle = startAngle + progressAngle;
        progressAngle = 360 - progressAngle;
        mPaint.setColor(mDefaultColor);
        canvas.drawArc(mCircleArea, startAngle, progressAngle, false, mPaint);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 是否显示动画
     *
     * @return
     */
    public boolean isShowAnim() {
        return mShowAnim;
    }

    /**
     * 设置当前可用数值
     *
     * @param progress
     */
    public void setCurrentProgress(double progress) {
        mProgress = progress;
        if (mShowAnim) {
            setDurationByProgress();
        } else {
            mTmpProgress = progress;
        }
        if (isShown()) {
            if (mShowAnim) {
                startAnim();
            } else {
                invalidate();
            }
        }
        // TODO: 2017/9/26 以后还有加上反向动画
    }


    /**
     * 设置当前最大数值
     *
     * @param maxProgress
     */
    public void setMaxProgress(double maxProgress) {
        mMaxProgress = maxProgress;
        if (mShowAnim) {
            setDurationByProgress();
        }
        if (isShown()) {
            if (mShowAnim) {
                startAnim();
            } else {
                invalidate();
            }
        }
        // TODO: 2017/9/26 以后还有加上反向动画
    }

    /**
     * 开始设置进度动画
     */
    private void startAnim() {
        if (mAnimator != null && (mAnimator.isStarted() || mAnimator.isRunning())) {
            mAnimator.cancel();
            mAnimator.removeAllListeners();
            mAnimator.removeAllUpdateListeners();
        }
        mAnimator = new ValueAnimator();
        mAnimator.setObjectValues(0.0d, mProgress);
        mAnimator.setEvaluator(mEvaluator);
        mAnimator.addUpdateListener(mAnimatorUpdateListener);
        mAnimator.addListener(mAnimatorLifeListener);
        mAnimator.setInterpolator(mInterpolator);
        mAnimator.setDuration(mDuration);
        mAnimator.start();
    }

    /**
     * 停止动画
     */
    public void stopAnim() {
        if (mAnimator != null && (mAnimator.isStarted() || mAnimator.isRunning())) {
            mAnimator.cancel();
            mAnimator.removeAllListeners();
            mAnimator.removeAllUpdateListeners();
            mAnimator = null;
        }
    }

    /**
     * 设置插值器
     *
     * @param interpolator
     */
    public void setInterpolator(TimeInterpolator interpolator) {
        mInterpolator = interpolator;
    }

    /**
     * 设置动画时长
     *
     * @param duration
     */
    public void setDuration(int duration) {
        mDuration = duration;
        setDurationByProgress();
    }

    /**
     * 设置是否显示动画
     *
     * @param anim
     */
    public void setShowAnim(boolean anim) {
        mShowAnim = anim;
    }

    /**
     * 增加数值变化的监听器
     *
     * @param updateListener
     */
    public void addAnimatorUpdateListener(ValueAnimator.AnimatorUpdateListener updateListener) {
        mOuterUpdateListeners.add(updateListener);
    }

    /**
     * 删除数值变化监听器
     *
     * @param updateListener
     */
    public void removeAnimatorUpdateListener(ValueAnimator.AnimatorUpdateListener updateListener) {
        mOuterUpdateListeners.remove(updateListener);
    }

    /**
     * 根据进度比设置动画时长
     */
    protected void setDurationByProgress() {
        if (mMaxProgress != -1) {
            mDuration = (int) (mProgress * mDuration / mMaxProgress);
        }
    }

    /**
     * 进度数值估值器
     */
    class DoubleEvaluator implements TypeEvaluator<Double> {
        @Override
        public Double evaluate(float fraction, Double startValue, Double endValue) {
            return startValue + (endValue - startValue) * fraction;
        }
    }

    /**
     * 动画生命周期监听器
     */
    class AnimatorLifeListener extends AnimatorListenerAdapter {
        @Override
        public void onAnimationCancel(Animator animation) {
            mShowAnim = false;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mShowAnim = false;
        }
    }

    /**
     * 动画数值更新监听器
     */
    class AnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            mTmpProgress = (double) animation.getAnimatedValue();
            invalidate();

            for (ValueAnimator.AnimatorUpdateListener updateListener : mOuterUpdateListeners) {
                updateListener.onAnimationUpdate(animation);
            }
        }
    }

}
