package com.kyangc.demoplus.activities;

import com.kyangc.demoplus.R;
import com.kyangc.demoplus.views.NestedViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NestedActivity extends AppCompatActivity {

    @Bind(R.id.rvList)
    RecyclerView mRecyclerView;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.srlNested)
    SwipeRefreshLayout mSrlNested;

    TestAdapter mTestAdapter;

    public static void start(Context context) {
        Intent i = new Intent(context, NestedActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nested);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mTestAdapter = new TestAdapter(this);
        mTestAdapter.setHeader(new NestedViewPager(this));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mTestAdapter);

        mSrlNested.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSrlNested.setRefreshing(false);
            }
        });
    }

    public static class TestAdapter extends RecyclerView.Adapter {

        private static final int ITEM_TYPE_ITEM = 0;

        private static final int ITEM_TYPE_HEADER = 1;

        private View header;

        private List data;

        private LayoutInflater mLayoutInflater;

        private Context mContext;

        public TestAdapter(Context context) {
            mContext = context;
            mLayoutInflater = LayoutInflater.from(context);
            data = new ArrayList();
            for (int i = 0; i < 100; i++) {
                data.add(i);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case ITEM_TYPE_ITEM:
                    return new ItemViewHolder(
                            mLayoutInflater.inflate(R.layout.item_nest_adapter, parent, false));
                case ITEM_TYPE_HEADER:
                    return new HeaderViewHolder(header);
                default:
                    return null;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ItemViewHolder) {
                ((ItemViewHolder) holder).mTextView.setText(position + "");
            }
        }

        @Override
        public int getItemCount() {
            return header == null ? (data == null ? 0 : data.size())
                    : (data == null ? 0 : data.size()) + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                if (header != null) {
                    return ITEM_TYPE_HEADER;
                } else {
                    return ITEM_TYPE_ITEM;
                }
            } else {
                return ITEM_TYPE_ITEM;
            }
        }

        public TestAdapter setHeader(View header) {
            this.header = header;
            return this;
        }

        public static class ItemViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.tvContent)
            TextView mTextView;

            public ItemViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

        public static class HeaderViewHolder extends RecyclerView.ViewHolder {

            public HeaderViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
