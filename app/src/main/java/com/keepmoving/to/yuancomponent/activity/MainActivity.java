package com.keepmoving.to.yuancomponent.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.keepmoving.to.yuancomponent.R;

public class MainActivity extends Activity {


    private RecyclerView mainContainer;
    private ActivitysAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mAdapter = new ActivitysAdapter();

        mainContainer = (RecyclerView) findViewById(R.id.main_container);
        mainContainer.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mainContainer.setAdapter(mAdapter);
    }


    class ActivitysAdapter extends RecyclerView.Adapter<ItemHolder> {

        Class<? extends Activity>[] mActivities = new Class[]{
                CircleProgressActivity.class,
                ScaleViewActivity.class,
                PolygonActivity.class
        };

        String[] mDemoTips = new String[]{
                "圆形进度条",
                "刻度尺",
                "圆角多边形"
        };

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView textView = (TextView) getLayoutInflater().inflate(R.layout.main_item, parent, false);
            return new ItemHolder(textView);
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, int position) {
            holder.bind(mActivities[position], mDemoTips[position]);
        }

        @Override
        public int getItemCount() {
            return mActivities.length;
        }
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        public ItemHolder(View itemView) {
            super(itemView);
        }

        public void bind(final Class<? extends Activity> activityClass, final String tip) {
            ((TextView) itemView).setText(tip);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, activityClass);
                    startActivity(intent);
                }
            });
        }
    }
}
