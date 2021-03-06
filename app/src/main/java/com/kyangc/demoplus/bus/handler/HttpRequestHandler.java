package com.kyangc.demoplus.bus.handler;

import com.kyangc.demoplus.app.DemoApp;
import com.kyangc.demoplus.bus.event.HttpRequestEvent;
import com.kyangc.demoplus.entities.HttpMethodEntity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpRequest;
import com.loopj.android.http.ResponseHandlerInterface;

import android.content.Context;

import cz.msebera.android.httpclient.client.methods.HttpUriRequest;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.protocol.HttpContext;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import timber.log.Timber;

/**
 * Created by chengkangyang on 七月.30.2015
 */
public class HttpRequestHandler {

    /**
     * TAG
     */
    public static final String TAG = "HttpRequestHandler";

    /**
     * Client
     */
    private AsyncHttpClient client;

    private HttpRequestHandler() {
        register();
        client = new AsyncHttpClient() {
            @Override
            protected AsyncHttpRequest newAsyncHttpRequest(DefaultHttpClient client,
                    HttpContext httpContext,
                    HttpUriRequest uriRequest, String contentType,
                    ResponseHandlerInterface responseHandler, Context context) {
                return super.newAsyncHttpRequest(client, httpContext, uriRequest, contentType,
                        responseHandler, context);
            }
        };
    }

    public static HttpRequestHandler getInstance() {
        return HttpRequestHandlerHolder.INSTANCE;
    }

    public void register() {
        Timber.d(TAG, "Handler registered!");
        EventBus.getDefault().register(this);
    }

    public void unregister() {
        Timber.d(TAG, "Handler unregistered!");
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.BackgroundThread)
    public void handle(HttpRequestEvent event) {
        switch (event.type) {
            case HttpMethodEntity.GET:
                client.get(DemoApp.getAppContext(), event.url, null, event.params, event.handler);
                break;
            case HttpMethodEntity.POST:
                client.post(DemoApp.getAppContext(), event.url, event.params, event.handler);
                break;
            case HttpMethodEntity.PUT:
                client.put(DemoApp.getAppContext(), event.url, event.params, event.handler);
                break;
            case HttpMethodEntity.PATCH:
                client.patch(DemoApp.getAppContext(), event.url, event.params, event.handler);
                break;
            case HttpMethodEntity.DELETE:
                client.delete(DemoApp.getAppContext(), event.url, null, event.params,
                        event.handler);
                break;
            default:
                break;
        }
    }

    private static class HttpRequestHandlerHolder {

        private static final HttpRequestHandler INSTANCE = new HttpRequestHandler();
    }
}
