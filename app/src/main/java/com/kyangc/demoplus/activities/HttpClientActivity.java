package com.kyangc.demoplus.activities;

import com.kyangc.demoplus.R;
import com.kyangc.developkit.base.BaseActivity;
import com.kyangc.demoplus.adapters.HttpResultListAdapter;
import com.kyangc.demoplus.bus.event.HttpRequestEvent;
import com.kyangc.demoplus.entities.HttpMethodEntity;
import com.kyangc.demoplus.entities.HttpResultEntity;
import com.kyangc.demoplus.network.Constants;
import com.kyangc.demoplus.settings.SettingManager;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;

/**
 * todo 自定义头部
 * <p/>
 * todo 可输入要发送的包体
 */
public class HttpClientActivity extends BaseActivity {

    public static final String TITLE = "title";

    public static final String TYPE = "type";

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.inputLayout)
    TextInputLayout inputLayout;

    EditText editText;

    @Bind(R.id.buttonRun)
    Button btRun;

    @Bind(R.id.progress)
    ProgressBar progressBar;

    @Bind(R.id.switchForHttps)
    Switch aSwitch;

    @Bind(R.id.rvResult)
    RecyclerView rvResult;

    HttpClientActivity context;

    ArrayList<HttpResultEntity> dataSet;

    HttpResultListAdapter adapter;

    boolean isHttps = true;

    String title;

    int type;

    public static void start(Context context, int type, String title) {
        Intent i = new Intent(context, HttpClientActivity.class);
        i.putExtra(TYPE, type);
        i.putExtra(TITLE, title);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http_client);

        initData();
        initAdapters();
        initViews();
    }

    @Override
    public void initData() {
        context = this;
        title = getIntent().getStringExtra(TITLE);
        type = getIntent().getIntExtra(TYPE, -1);
        dataSet = new ArrayList<>();
        isHttps = SettingManager.getIsHttpsFirst(this);
    }

    @Override
    public void initViews() {
        super.initViews();

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
        toolbar.setTitle(title);

        //EditText
        inputLayout.setHint("Url:");
        editText = inputLayout.getEditText();
        editText.setText(getUrl());

        //Switch
        aSwitch.setChecked(isHttps);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isHttps = isChecked;
                editText.setText(getUrl());
            }
        });

        //progress
        progressBar.setVisibility(View.GONE);

        //Button
        btRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postEvent();
            }
        });

        //List
        rvResult.setLayoutManager(new LinearLayoutManager(context));
        rvResult.setAdapter(adapter);
    }

    @Override
    public void initAdapters() {
        super.initAdapters();

        adapter = new HttpResultListAdapter(context);
        adapter.setDataSet(dataSet);
    }

    private ResponseHandlerInterface getResponseHandler() {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                clearList();
                enableWidgets(false);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                displayResult(statusCode, headers, responseBody, null);
                enableWidgets(true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                    Throwable error) {
                displayResult(statusCode, headers, responseBody, error);
                enableWidgets(true);
            }

            @Override
            public void onRetry(int retryNo) {

            }
        };
    }

    private String getUrl() {
        String url = isHttps ? Constants.PROTOCOL_HTTPS : Constants.PROTOCOL_HTTP;
        switch (type) {
            case HttpMethodEntity.GET:
                url += Constants.BASE_URL + Constants.URL_GET;
                break;
            case HttpMethodEntity.POST:
                url += Constants.BASE_URL + Constants.URL_POST;
                break;
            case HttpMethodEntity.PUT:
                url += Constants.BASE_URL + Constants.URL_PUT;
                break;
            case HttpMethodEntity.PATCH:
                url += Constants.BASE_URL + Constants.URL_PATCH;
                break;
            case HttpMethodEntity.DELETE:
                url += Constants.BASE_URL + Constants.URL_DELETE;
                break;
            default:
                url = null;
                break;
        }

        return url;
    }

    private RequestParams getParams() {
        return new RequestParams("key", "value");
    }

    private void clearList() {
        if (dataSet != null) {
            dataSet.clear();
            adapter.notifyDataSetChanged();
        } else {
            dataSet = new ArrayList<>();
        }
    }

    private void enableWidgets(boolean isEnable) {
        if (isEnable) {
            btRun.setEnabled(true);
            progressBar.setVisibility(View.GONE);
            editText.setEnabled(true);
        } else {
            btRun.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            editText.setEnabled(false);
        }
    }

    private void displayResult(int statusCode, Header[] headers, byte[] response, Throwable e) {
        //code
        String codeMsg = String.format(Locale.US, "Return Status Code: %d", statusCode);
        dataSet.add(new HttpResultEntity(HttpResultEntity.ITEM_TYPE_CODE, codeMsg));

        //Headers
        if (headers != null) {
            StringBuilder builder = new StringBuilder();
            for (Header h : headers) {
                String _h = String.format(Locale.US, "%s : %s", h.getName(), h.getValue());
                builder.append(_h);
                builder.append("\n");
            }
            String headerMsg = builder.toString();
            dataSet.add(new HttpResultEntity(HttpResultEntity.ITEM_TYPE_HEADER, headerMsg));
        }

        //Response
        if (response != null) {
            String responseMsg = new String(response);
            dataSet.add(new HttpResultEntity(HttpResultEntity.ITEM_TYPE_RESPONSE, responseMsg));
        }

        //Error
        if (e != null) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            dataSet.add(new HttpResultEntity(HttpResultEntity.ITEM_TYPE_ERROR, sw.toString()));
        }

        adapter.notifyItemRangeInserted(0, dataSet.size() - 1);
    }

    private void postEvent() {
        EventBus.getDefault().post(
                new HttpRequestEvent()
                        .setHandler(getResponseHandler())
                        .setParams(getParams())
                        .setType(type)
                        .setUrl(getUrl()));
    }
}
