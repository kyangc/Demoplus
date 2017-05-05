package com.kyangc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Author:  kyangc
 * Email:   kyangc@gmail.com
 * Date:    2016-04-15
 * Project: Demoplus
 */
public class AnnouncementInfo {

    @Expose
    @SerializedName("id")
    private int id = 0;

    @Expose
    @SerializedName("content_type")
    private String contentType = "url";

    @Expose
    @SerializedName("content")
    private String content;

    @Expose
    @SerializedName("rich_text")
    private String richText;

    @Expose
    @SerializedName("style")
    private String style = "bottom_up";

    @Expose
    @SerializedName("repeat")
    private int repeat = 10000;

    @Expose
    @SerializedName("repeat_since")
    private long repeatSince = 1459440000;

    @Expose
    @SerializedName("repeat_until")
    private long repeatUntil = 1462032000;

    @Expose
    @SerializedName("interval")
    private int interval = 10;

    @Expose
    @SerializedName("scene")
    private String scene = "launch2";

    @Expose
    @SerializedName("valid_date_since")
    private long validDateSince = 1459440000;

    @Expose
    @SerializedName("valid_date_until")
    private long validDateUntil = 1462032000;

    @Expose
    @SerializedName("extra")
    private Extra extra = new Extra();

    public int getId() {
        return id;
    }

    public String getContentType() {
        return contentType;
    }

    public String getContent() {
        return content;
    }

    public String getRichText() {
        return richText;
    }

    public String getStyle() {
        return style;
    }

    public int getRepeat() {
        return repeat;
    }

    public long getRepeatSince() {
        return repeatSince;
    }

    public long getRepeatUntil() {
        return repeatUntil;
    }

    public int getInterval() {
        return interval;
    }

    public String getScene() {
        return scene;
    }

    public long getValidDateSince() {
        return validDateSince;
    }

    public long getValidDateUntil() {
        return validDateUntil;
    }

    public Extra getExtra() {
        return extra;
    }

    public static class Extra {

        @SerializedName("target")
        private String target;

        @SerializedName("scene_text")
        private String text = "test";

        @SerializedName("duration")
        private int duration = 100;

        @SerializedName("image_url")
        private String imageUrl;

        public String getTarget() {
            return target;
        }

        public String getText() {
            return text;
        }

        public int getDuration() {
            return duration;
        }

        public String getImageUrl() {
            return imageUrl;
        }
    }
}
