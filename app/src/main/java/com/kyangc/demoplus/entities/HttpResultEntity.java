package com.kyangc.demoplus.entities;

/**
 * Created by chengkangyang on 七月.30.2015
 */
public class HttpResultEntity {

    public static final int ITEM_TYPE_HEADER = 0;

    public static final int ITEM_TYPE_CODE = 1;

    public static final int ITEM_TYPE_RESPONSE = 2;

    public static final int ITEM_TYPE_ERROR = 3;

    public int type;

    public String content;

    public HttpResultEntity(int type, String content) {
        this.type = type;
        this.content = content;
    }
}
