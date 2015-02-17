package com.ocddevelopers.androidwearables.wearonlyapps;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WearableListView;
import android.widget.Toast;

public class AnimatedListViewActivity extends Activity {
    private String[] mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wlistview);

        mItems = new String[] { "item0", "item1", "item2", "item3", "item4" };
        WearableListView wearableListView = (WearableListView) findViewById(R.id.list);
        wearableListView.setAdapter(new WearableAdapter(this, mItems));
        wearableListView.setClickListener(mClickListener);
    }

    private WearableListView.ClickListener mClickListener = new WearableListView.ClickListener() {
        @Override
        public void onClick(WearableListView.ViewHolder viewHolder) {
            int position = viewHolder.getPosition();
            Toast.makeText(AnimatedListViewActivity.this, "Tapped on " + position,
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onTopEmptyRegionClick() {

        }
    };
}
