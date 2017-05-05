package com.kyangc;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Author:  kyangc
 * Email:   kyangc@gmail.com
 * Date:    2016-04-11
 * Project: Demoplus
 */
public class LogParser {

    public static final String TAG = "LogParser";

    String content = "";

    public LogModel[] getLogModel(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            try {
                StringBuilder sb = new StringBuilder();
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line = "";
                while (line != null) {
                    line = br.readLine();
                    if (line != null) {
                        sb.append(line);
                    }
                }

                return new Gson().fromJson(sb.toString(), new TypeToken<LogModel[]>() {
                }.getType());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        return null;
    }

    public static class LogModel {

        @SerializedName("remote_addr")
        @Expose
        private String remoteAddr;

//        @SerializedName("username")
//        @Expose
//        private String username;

//        @SerializedName("sessionid")
//        @Expose
//        private String sessionid;
//
//        @SerializedName("time_local")
//        @Expose
//        private String timeLocal;

//        @SerializedName("time_iso8601")
//        @Expose
//        private String timeIso8601;

//        @SerializedName("request_method")
//        @Expose
//        private String requestMethod;

        @SerializedName("request_uri")
        @Expose
        private String requestUri;

//        @SerializedName("request_length")
//        @Expose
//        private Integer requestLength;

//        @SerializedName("server_protocol")
//        @Expose
//        private String serverProtocol;

//        @SerializedName("server_name")
//        @Expose
//        private String serverName;
//
//        @SerializedName("host_domain")
//        @Expose
//        private String hostDomain;
//
//        @SerializedName("msec")
//        @Expose
//        private Double msec;
//
//        @SerializedName("status")
//        @Expose
//        private Integer status;

//        @SerializedName("body_bytes_sent")
//        @Expose
//        private Integer bodyBytesSent;
//
//        @SerializedName("http_referer")
//        @Expose
//        private String httpReferer;

        @SerializedName("http_user_agent")
        @Expose
        private String httpUserAgent;

//        @SerializedName("request_time")
//        @Expose
//        private Double requestTime;

        @SerializedName("upstream_addr")
        @Expose
        private String upstreamAddr;
//
//        @SerializedName("upstream_response_time")
//        @Expose
//        private Double upstreamResponseTime;
//
//        @SerializedName("scheme")
//        @Expose
//        private String scheme;
//
//        @SerializedName("pipe")
//        @Expose
//        private String pipe;
//
//        @SerializedName("host")
//        @Expose
//        private String host;

        @SerializedName("@timestamp")
        @Expose
        private String Timestamp;
//
//        public String getRemoteAddr() {
//            return remoteAddr;
//        }
//
//        public String getUsername() {
//            return username;
//        }
//
//        public String getSessionid() {
//            return sessionid;
//        }
//
//        public String getTimeLocal() {
//            return timeLocal;
//        }
//
//        public String getTimeIso8601() {
//            return timeIso8601;
//        }
//
//        public String getRequestMethod() {
//            return requestMethod;
//        }
//
//        public String getRequestUri() {
//            return requestUri;
//        }
//
//        public Integer getRequestLength() {
//            return requestLength;
//        }
//
//        public String getServerProtocol() {
//            return serverProtocol;
//        }
//
//        public String getServerName() {
//            return serverName;
//        }
//
//        public String getHostDomain() {
//            return hostDomain;
//        }
//
//        public Double getMsec() {
//            return msec;
//        }
//
//        public Integer getStatus() {
//            return status;
//        }
//
//        public Integer getBodyBytesSent() {
//            return bodyBytesSent;
//        }
//
//        public String getHttpReferer() {
//            return httpReferer;
//        }
//
//        public String getHttpUserAgent() {
//            return httpUserAgent;
//        }
//
//        public Double getRequestTime() {
//            return requestTime;
//        }
//
//        public String getUpstreamAddr() {
//            return upstreamAddr;
//        }
//
//        public Double getUpstreamResponseTime() {
//            return upstreamResponseTime;
//        }
//
//        public String getScheme() {
//            return scheme;
//        }
//
//        public String getPipe() {
//            return pipe;
//        }
//
//        public String getHost() {
//            return host;
//        }
//
//        public String getTimestamp() {
//            return Timestamp;
//        }
    }
}
