package com.keepmoving.to.yuancomponent.activity;

import android.app.Activity;
import android.os.Bundle;

import com.keepmoving.to.yuancomponent.R;
import com.keepmoving.to.yuancomponent.widget.circleprogress.CircleProgressContainer;


public class CircleProgressActivity extends Activity {

    CircleProgressContainer mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.circle_progress_activity);

        mContainer = (CircleProgressContainer) findViewById(R.id.progress_container);
        mContainer.setMaxProgress(300000);
        mContainer.setCurrentProgress(220000);
    }

}
