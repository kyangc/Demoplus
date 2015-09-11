package com.kyangc.demoplus.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.kyangc.demoplus.R;
import com.kyangc.demoplus.adapters.SwipeAndDragAdapter;
import com.kyangc.demoplus.internal.OnStartDragListener;
import com.kyangc.demoplus.internal.SimpleItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SwipeListItemActivity extends AppCompatActivity {

    public static final String TAG = "SwipeListItemActivity";

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.rvList)
    RecyclerView rvList;

    private ItemTouchHelper mItemTouchHelper;
    private List<String> dataSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_list_item);
        ButterKnife.bind(this);
        initData();
        initToolbar();
        initList();
    }

    private void initData() {
        dataSet = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            dataSet.add(i + "");
        }
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initList() {
        SwipeAndDragAdapter adapter = new SwipeAndDragAdapter(this, dataSet);
        adapter.setmDragStartListener(new OnStartDragListener() {
            @Override
            public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
                mItemTouchHelper.startDrag(viewHolder);
            }
        });

        rvList.setHasFixedSize(true);
        rvList.setAdapter(adapter);
        rvList.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rvList);
    }
}
