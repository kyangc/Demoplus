package com.kyangc.demoplus.fragments;

import com.kyangc.demoplus.R;
import com.kyangc.demoplus.activities.HttpClientActivity;
import com.kyangc.demoplus.adapters.HttpMethodListAdapter;
import com.kyangc.demoplus.entities.HttpMethodEntity;
import com.kyangc.demoplus.fragments.base.BaseFragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * Created by chengkangyang on 七月.29.2015
 * <p/>
 * Http client fragment
 */
public class HttpClientFragment extends BaseFragment {

    public static String TAG = HttpClientFragment.class.getSimpleName();

    public static final String TITLE = "Http Client Demo";

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    HttpMethodListAdapter adapter;

    ArrayList<HttpMethodEntity> dataSet;

    public HttpClientFragment() {
    }

    public static HttpClientFragment newInstance() {
        return new HttpClientFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = setView(inflater, R.layout.fragment_http_client, container);
        initViews();
        return view;
    }

    @Override
    public void initData() {
        super.initData();

        dataSet = new ArrayList<>();
        dataSet.add(new HttpMethodEntity("Get", HttpMethodEntity.GET));
        dataSet.add(new HttpMethodEntity("Post", HttpMethodEntity.POST));
        dataSet.add(new HttpMethodEntity("Put", HttpMethodEntity.PUT));
        dataSet.add(new HttpMethodEntity("Patch", HttpMethodEntity.PATCH));
        dataSet.add(new HttpMethodEntity("Delete", HttpMethodEntity.DELETE));
    }

    @Override
    public void initAdapter() {
        super.initAdapter();

        adapter = new HttpMethodListAdapter(mActivity, dataSet)
                .setOnItemClickListener(new HttpMethodListAdapter.OnItemClickListener() {
                    @Override
                    public void OnItemClick(HttpMethodEntity entity) {
                        if (entity != null) {
                            HttpClientActivity.start(mActivity, entity.method, entity.name);
                        }
                    }
                });
    }

    @Override
    public void initViews() {
        super.initViews();

        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setAdapter(adapter);
    }
}
