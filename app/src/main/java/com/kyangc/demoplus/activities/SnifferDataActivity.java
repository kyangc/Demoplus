package com.kyangc.demoplus.activities;

import com.kyangc.demoplus.R;
import com.kyangc.demoplus.adapters.SnifferDataAdapter;
import com.kyangc.demoplus.pcap.EthernetFrameList;
import com.kyangc.demoplus.pcap.LibpcapParser;
import com.kyangc.demoplus.pcap.data.Packet;
import com.kyangc.demoplus.pcap.header.EthernetHeader;
import com.kyangc.developkit.utils.T;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

public class SnifferDataActivity extends AppCompatActivity {

    public static final String INTENT_KEY_FILE_NAME = "file_name";

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.rvResult)
    RecyclerView rvResult;

    @Bind(R.id.slResult)
    SwipeRefreshLayout slResult;

    @Bind(R.id.rlProgress)
    RelativeLayout rlProgress;

    @Bind(R.id.tvLoadingHint)
    TextView tvHint;

    SnifferDataActivity context;

    String filePath = null;

    SnifferDataAdapter adapter;

    File pcapFile;

    public static void start(Context context, String filePath) {
        Intent i = new Intent(context, SnifferDataActivity.class);
        i.putExtra(INTENT_KEY_FILE_NAME, filePath);
        context.startActivity(i);
    }

    private static AsyncTask getUpdateListTask(final SnifferDataActivity activity) {
        return new AsyncTask() {

            WeakReference<SnifferDataActivity> context = new WeakReference<>(activity);

            @Override
            protected Object doInBackground(Object[] params) {
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                context.get().slResult.setRefreshing(true);
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                context.get().slResult.setRefreshing(false);
            }
        };
    }

    private static AsyncTask getParseDataTask(final SnifferDataActivity activity) {
        return new AsyncTask() {

            WeakReference<SnifferDataActivity> context = new WeakReference<>(activity);

            @Override
            protected Object doInBackground(Object[] params) {
                File file = new File(context.get().filePath);
                LibpcapParser parser = null;
                try {
                    parser = new LibpcapParser(file);
                    return parser.parse().getAll(new EthernetFrameList.Filter() {
                        @Override
                        public boolean shouldUse(Packet<EthernetHeader> packet) {
                            return true;
                        }
                    });
                } catch (InvocationTargetException | InstantiationException | IOException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                context.get().displayProgress(true, "Parsing data");
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                EthernetFrameList packets = (EthernetFrameList) o;
                context.get().displayProgress(false, null);
                if (o == null) {
                    T.showShort(context.get(), "Error occurred");
                } else {
                    for (Packet<EthernetHeader> packet : packets) {
                        Timber.i(packet.toString());
                    }
                }
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sniffer_data);
        ButterKnife.bind(this);
        initData();
        initWidgets();

        parseData();
    }

    private void initWidgets() {
        //Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Refresh
        slResult.setColorSchemeResources(R.color.blue);
        slResult.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateList();
            }
        });

        //List
        adapter = new SnifferDataAdapter(context);
        rvResult.setLayoutManager(new LinearLayoutManager(context));
        rvResult.setAdapter(adapter);
    }

    private void initData() {
        context = this;
        filePath = getIntent().getStringExtra(INTENT_KEY_FILE_NAME);
        if (filePath != null) {
            pcapFile = new File(filePath);
        }

        if (pcapFile == null || !pcapFile.exists()) {
            finish();
            T.showShort(context, "File doesn't exist!");
        }
    }

    private void displayProgress(boolean isShown, String hint) {
        rlProgress.setVisibility(isShown ? View.VISIBLE : View.GONE);
        tvHint.setText(hint);
    }

    private void updateList() {
        getUpdateListTask(context).execute();
    }

    private void parseData() {
        getParseDataTask(context).execute();
    }
}
