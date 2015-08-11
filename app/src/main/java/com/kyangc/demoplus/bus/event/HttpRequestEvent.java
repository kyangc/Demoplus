package com.kyangc.demoplus.bus.event;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

/**
 * Created by chengkangyang on 七月.30.2015
 */
public class HttpRequestEvent {

    public int type;
    public String url;
    public RequestParams params;
    public ResponseHandlerInterface handler;

    public int getType() {
        return type;
    }

    public HttpRequestEvent setType(int type) {
        this.type = type;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public HttpRequestEvent setUrl(String url) {
        this.url = url;
        return this;
    }

    public RequestParams getParams() {
        return params;
    }

    public HttpRequestEvent setParams(RequestParams params) {
        this.params = params;
        return this;
    }

    public ResponseHandlerInterface getHandler() {
        return handler;
    }

    public HttpRequestEvent setHandler(ResponseHandlerInterface handler) {
        this.handler = handler;
        return this;
    }
}
