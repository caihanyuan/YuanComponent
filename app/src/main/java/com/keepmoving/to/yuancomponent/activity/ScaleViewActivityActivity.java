package com.keepmoving.to.yuancomponent.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.keepmoving.to.yuancomponent.R;
import com.keepmoving.to.yuancomponent.widget.linearscaleview.BaseScaleView;
import com.keepmoving.to.yuancomponent.widget.linearscaleview.HorizontalScaleScrollView;

public class ScaleViewActivityActivity extends Activity implements BaseScaleView.OnScrollListener {

    private HorizontalScaleScrollView horizontalScale;
    private TextView mCurrentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scale_view_activity);

        horizontalScale = (HorizontalScaleScrollView) findViewById(R.id.horizontalScale);
        horizontalScale.setOnScrollListener(this);
        mCurrentText = (TextView) findViewById(R.id.current_num_text);
    }

    @Override
    public void onScaleScroll(int scale) {
        mCurrentText.setText(String.valueOf(scale));
    }
}
