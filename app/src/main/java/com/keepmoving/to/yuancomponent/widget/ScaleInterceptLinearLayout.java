package com.keepmoving.to.yuancomponent.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.keepmoving.to.yuancomponent.R;
import com.keepmoving.to.yuancomponent.widget.linearscaleview.BaseScaleView;

/**
 * Created by caihanyuan on 2017/10/8.
 */

public class ScaleInterceptLinearLayout extends LinearLayout {
    BaseScaleView mScaleView;

    public ScaleInterceptLinearLayout(Context context) {
        super(context);
    }

    public ScaleInterceptLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ScaleInterceptLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ScaleInterceptLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mScaleView == null) {
            mScaleView = (BaseScaleView) findViewById(R.id.horizontalScale);
        }
        return mScaleView.onTouchEvent(event);
    }
}
