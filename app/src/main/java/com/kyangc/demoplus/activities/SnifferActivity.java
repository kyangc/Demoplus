package com.kyangc.demoplus.activities;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kyangc.demoplus.R;
import com.kyangc.demoplus.adapters.SnifferResultListAdapter;
import com.kyangc.demoplus.app.DemoApp;
import com.kyangc.demoplus.entities.SnifferDataEntity;
import com.kyangc.demoplus.services.SnifferService;
import com.kyangc.demoplus.utils.EmailUtils;
import com.kyangc.demoplus.utils.FilesUtils;
import com.kyangc.demoplus.utils.L;
import com.kyangc.demoplus.utils.PcapUtils;
import com.stericson.RootShell.RootShell;
import com.stericson.RootShell.exceptions.RootDeniedException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.FileUtils;
import fr.bmartel.pcapdecoder.PcapDecoder;

public class SnifferActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "SnifferActivity";

    public static final int ROOT_STATUS_ROOTED = 0;
    public static final int ROOT_STATUS_ROOTING = 1;
    public static final int ROOT_STATUS_UNROOTED = 2;
    public static final String DATA_STORAGE_DIR = Environment.getExternalStorageDirectory() + "/pcaps/";

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.rvResult)
    RecyclerView rvResult;
    @Bind(R.id.rlProgress)
    RelativeLayout rlProgress;
    @Bind(R.id.ivRootMarker)
    ImageView ivRootMarker;
    @Bind(R.id.tvRoot)
    TextView tvRootStatus;
    @Bind(R.id.tvStatus)
    TextView tvRunningStatus;
    @Bind(R.id.progress)
    ProgressBar runningProgress;
    @Bind(R.id.buttonRun)
    Button buttonRun;
    @Bind(R.id.tvProgressHint)
    TextView tvHint;
    @Bind(R.id.slResult)
    SwipeRefreshLayout slResult;

    boolean isRooted = false;
    boolean isRootRunning = false;
    SnifferActivity context;
    SnifferResultListAdapter adapter;
    List<SnifferDataEntity> displayList;
    SnifferService.SnifferBinder snifferBinder;
    ServiceConnection serviceConnection;

    public static void start(Context context) {
        Intent i = new Intent(context, SnifferActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sniffer);

        //inject views
        ButterKnife.bind(this);

        //Init data
        initData();

        //Init widgets
        initWidgets();

        //Check root
        checkRoot();

        //Update list
        updateList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceConnection != null) unbindService(serviceConnection);
    }

    private void initService() {

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                L.i("Service connected!");
                snifferBinder = (SnifferService.SnifferBinder) service;
                snifferBinder.setListener(getOnTaskFinishListener());

                //get service status
                if (snifferBinder.isSnifferRunning()) {
                    displayButtonStatus(true, R.string.upper_stop);
                    displayRunningProgress(true);
                    displayRunningStatus(R.string.tcpdump_is_running);
                } else {
                    displayButtonStatus(true, R.string.upper_start);
                    displayRunningProgress(false);
                    displayRunningStatus(R.string.tcpdump_is_ready);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                L.i("Service disconnected!");
                snifferBinder = null;
            }
        };

        Intent i = new Intent(this, SnifferService.class);
        startService(i);
        bindService(i, serviceConnection, BIND_AUTO_CREATE);
    }

    private void initData() {
        context = this;
        displayList = new ArrayList<>();
    }

    private void initWidgets() {
        //toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //List
        rvResult.setLayoutManager(new LinearLayoutManager(context));
        adapter = new SnifferResultListAdapter(context);
        adapter.setDataSet(displayList);
        rvResult.setAdapter(adapter);
        adapter.setOnClickListener(getListItemOnClickListener());

        //Button
        buttonRun.setOnClickListener(this);

        //Progress
        rlProgress.setOnClickListener(this);

        //Swipe refresh
        slResult.setColorSchemeResources(R.color.blue);
        slResult.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateList();
            }
        });
    }

    private void displayButtonStatus(boolean isEnable, int resid) {
        buttonRun.setEnabled(isEnable);
        buttonRun.setText(resid);
    }

    private void displayRunningProgress(boolean isDisplay) {
        runningProgress.setVisibility(isDisplay ? View.VISIBLE : View.GONE);
    }

    private void displayProgress(boolean isDisplay, String hint) {
        rlProgress.setVisibility(isDisplay ? View.VISIBLE : View.GONE);
        tvHint.setText(hint);
    }

    private void displayRunningStatus(String status) {
        tvRunningStatus.setText(status);
    }

    private void displayRunningStatus(int resId) {
        tvRunningStatus.setText(resId);
    }

    private void displayRootStatus(int rootStatus) {
        switch (rootStatus) {
            case ROOT_STATUS_ROOTED:
                ivRootMarker.setImageDrawable(getResources().getDrawable(R.drawable.mark_body));
                tvRootStatus.setText(R.string.rooted);
                buttonRun.setEnabled(true);
                displayRunningStatus(getString(R.string.tcpdump_is_ready));
                break;
            case ROOT_STATUS_ROOTING:
                ivRootMarker.setImageDrawable(getResources().getDrawable(R.drawable.mark_header));
                tvRootStatus.setText(R.string.rooting);
                buttonRun.setEnabled(false);
                displayRunningStatus(getString(R.string.running_root_process));
                break;
            case ROOT_STATUS_UNROOTED:
                ivRootMarker.setImageDrawable(getResources().getDrawable(R.drawable.mark_error));
                tvRootStatus.setText(R.string.un_root);
                buttonRun.setEnabled(true);
                displayRunningStatus(getString(R.string.wair_for_root));
                break;
            default:
                break;
        }
    }

    private void checkRoot() {
        getCheckRootTask().execute();
    }

    private void checkRestoredData() {
        getCheckDataTask().execute();
    }

    private void updateList() {
        getUpdateListTask().execute();
    }

    private SnifferResultListAdapter.OnClickListener getListItemOnClickListener() {
        return new SnifferResultListAdapter.OnClickListener() {
            @Override
            public void onEmailClick(String fileName, String filePath) {
                File file = new File(filePath);
                if (file.exists()) {
                    EmailUtils.sendEmailWithAttach(
                            context,
                            DemoApp.EMAIL,
                            "New sniffer data:" + fileName,
                            "New sniffer data:" + fileName,
                            file);
                }
            }

            @Override
            public void onDeleteClick(final String filePath) {
                new AlertDialog
                        .Builder(context)
                        .setMessage("Sure to delete?")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                L.i(filePath);
                                if (FileUtils.deleteFile(filePath)) {
                                    int location = -1;
                                    for (location = 0; location < displayList.size(); location++) {
                                        if (displayList.get(location).filePath.equals(filePath))
                                            break;
                                    }
                                    if (location != -1) {
                                        displayList.remove(location);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        })
                        .show();
            }
        };
    }

    private SnifferService.OnTaskFinishListener getOnTaskFinishListener() {
        return new SnifferService.OnTaskFinishListener() {
            @Override
            public void onCommandRunning(String line) {
                L.i(line);
            }

            @Override
            public void onSnifferPrepared(int statusCode, String errorMsg) {
                L.i("Tcpdump prepared");
            }

            @Override
            public void onSnifferStarted(int statusCode, String errorMsg) {
                L.i("Tcpdump started, status = " + statusCode);
                displayButtonStatus(true, R.string.upper_stop);
                displayRunningProgress(true);
                displayRunningStatus(R.string.tcpdump_is_running);
            }

            @Override
            public void onSnifferStopped(int statusCode, String errorMsg) {
                L.i("Tcpdump stopped, status = " + statusCode);
                displayButtonStatus(true, R.string.upper_start);
                displayRunningProgress(false);
                displayRunningStatus(R.string.tcpdump_is_ready);
                updateList();
            }
        };
    }

    private AsyncTask getCheckRootTask() {
        return new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                return RootShell.isAccessGiven();
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                displayProgress(true, getString(R.string.requiring_root_access));
                displayRootStatus(ROOT_STATUS_ROOTING);
                isRootRunning = true;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                isRooted = (boolean) o;
                isRootRunning = false;
                displayProgress(false, getString(R.string.requiring_root_access));
                displayRootStatus(isRooted ? ROOT_STATUS_ROOTED : ROOT_STATUS_UNROOTED);
                if (isRooted) initService();
            }
        };
    }

    private AsyncTask getCheckDataTask() {
        return new AsyncTask() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                displayProgress(true, getString(R.string.check_restored_data));
            }

            @Override
            protected Object doInBackground(Object[] params) {
                if (snifferBinder.getPcapFilePath() == null) {
                    return null;
                }
                PcapDecoder pcapNgDecoder = null;
                try {
                    pcapNgDecoder = new PcapDecoder(FilesUtils.toByteArray(snifferBinder.getPcapFilePath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                pcapNgDecoder.decode();
                PcapUtils.getSnifferResultList(pcapNgDecoder);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                displayProgress(false, getString(R.string.check_restored_data));
            }
        };
    }

    private AsyncTask getUpdateListTask() {
        return new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                displayList.clear();
                List<File> paths = FilesUtils.getListFiles(new File(DATA_STORAGE_DIR));
                for (File file : paths) {
                    displayList.add(new SnifferDataEntity(file.getAbsolutePath(), file.getName()));
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                adapter.notifyDataSetChanged();
                slResult.setRefreshing(false);
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlProgress:
                break;
            case R.id.buttonRun:
                if (isRooted) {
                    if (snifferBinder.isSnifferRunning()) {
                        snifferBinder.stop();
                    } else {
                        try {
                            snifferBinder.start(SnifferService.TCPDUMP_RUNNING_TYPE_STORAGE);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (RootDeniedException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (!isRootRunning) {
                        checkRoot();
                    }
                }
                break;
            default:
                break;
        }
    }
}