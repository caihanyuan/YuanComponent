package com.keepmoving.to.yuancomponent.activity.bazier;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.keepmoving.to.yuancomponent.R;

public class BazierTestActivity extends AppCompatActivity {

    private RecyclerView mainContainer;
    private ActivitysAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bazier_test);

        mAdapter = new ActivitysAdapter();

        mainContainer = (RecyclerView) findViewById(R.id.main_container);
        mainContainer.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mainContainer.setAdapter(mAdapter);
    }

    class ActivitysAdapter extends RecyclerView.Adapter<ItemHolder> {

        Class<? extends Activity>[] mActivities = new Class[]{
                GenerateBazierIActivity.class,
                GenerateBazierIIActivity.class,
                BazierCircleActivity.class,
                BazierBubbleActivity.class
        };

        String[] mDemoTips = new String[]{
                "迭代法-贝塞尔曲线",
                "Casteljau算法-贝塞尔曲线",
                "贝塞尔曲线拟圆",
                "贝塞尔拖拽气泡"
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
                    Intent intent = new Intent(BazierTestActivity.this, activityClass);
                    startActivity(intent);
                }
            });
        }
    }
}
