package com.kyangc.demoplus.entities;

/**
 * Created by chengkangyang on 七月.30.2015
 */
public class HttpMethodEntity {

    public static final int GET = 0;
    public static final int POST = 1;
    public static final int PUT = 2;
    public static final int PATCH = 3;
    public static final int DELETE = 4;

    public String name;
    public int method;

    public HttpMethodEntity(String name, int method) {
        this.name = name;
        this.method = method;
    }
}
