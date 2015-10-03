package com.kyangc.demoplus.fragments;

import com.kyangc.demoplus.R;
import com.kyangc.demoplus.activities.HttpClientActivity;
import com.kyangc.demoplus.adapters.HttpMethodListAdapter;
import com.kyangc.demoplus.entities.HttpMethodEntity;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by chengkangyang on 七月.29.2015
 * <p/>
 * Http client fragment
 */
public class HttpClientFragment extends Fragment {

    /**
     * TAG
     */
    public static final String TAG = "HttpClientFragment";

    public static final String TITLE = "Http Client Demo";

    /**
     * Widgets
     */
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    /**
     * Data
     */
    Context context;

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
        context = getActivity();
        initData();
        initAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_http_client, container, false);
        ButterKnife.bind(this, view);
        initList();
        return view;
    }

    private void initData() {
        dataSet = new ArrayList<>();
        dataSet.add(new HttpMethodEntity("Get", HttpMethodEntity.GET));
        dataSet.add(new HttpMethodEntity("Post", HttpMethodEntity.POST));
        dataSet.add(new HttpMethodEntity("Put", HttpMethodEntity.PUT));
        dataSet.add(new HttpMethodEntity("Patch", HttpMethodEntity.PATCH));
        dataSet.add(new HttpMethodEntity("Delete", HttpMethodEntity.DELETE));
    }

    private void initAdapter() {
        adapter = new HttpMethodListAdapter(context, dataSet)
                .setOnItemClickListener(new HttpMethodListAdapter.OnItemClickListener() {
                    @Override
                    public void OnItemClick(HttpMethodEntity entity) {
                        if (entity != null) {
                            HttpClientActivity.start(context, entity.method, entity.name);
                        }
                    }
                });
    }

    private void initList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
    }
}
