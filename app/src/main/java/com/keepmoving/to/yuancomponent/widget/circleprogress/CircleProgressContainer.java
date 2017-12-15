package com.keepmoving.to.yuancomponent.widget.circleprogress;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.keepmoving.to.yuancomponent.R;
import com.keepmoving.to.yuancomponent.utils.StringUtils;

/**
 * Created by caihanyuan on 2017/9/25.
 */

public class CircleProgressContainer extends FrameLayout implements ValueAnimator.AnimatorUpdateListener {

    private static final String TAG = CircleProgressContainer.class.getSimpleName();

    private CircleProgressView mProgressView;
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
        initView(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CircleProgressContainer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.circle_progress_container, this, true);

        mProgressView = (CircleProgressView) findViewById(R.id.progress_view);
        mCurrentProgressText = (TextView) findViewById(R.id.current_progress_text);
        mMaxProgressText = (TextView) findViewById(R.id.total_progress_text);

        mProgressView.addAnimatorUpdateListener(this);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        double progress = (double) animation.getAnimatedValue();
        setCurrentProgressText(progress);
    }

    /**
     * 设置当前可用数值
     *
     * @param progress
     */
    public void setCurrentProgress(double progress) {
        if (!mProgressView.isShowAnim()) {
            setCurrentProgressText(progress);
        } else {
            setCurrentProgressText(0);
        }
        mProgressView.setCurrentProgress(progress);
    }

    /**
     * 设置当前最大数值
     *
     * @param maxProgress
     */
    public void setMaxProgress(double maxProgress) {
        mProgressView.setMaxProgress(maxProgress);
        setMaxProgressText(maxProgress);
    }

    /**
     * 设置可用数值提示文本
     */
    protected void setCurrentProgressText(double progress) {
        String tip = StringUtils.splitMoney(progress, 0);
        mCurrentProgressText.setText(tip);
    }

    /**
     * 设置最大数值提示文本
     */
    protected void setMaxProgressText(double maxProgress) {
        String tip = getContext().getString(R.string.circle_progress_total_title);
        tip = String.format(tip, StringUtils.splitMoney(maxProgress, 0));
        mMaxProgressText.setText(tip);
    }
}
