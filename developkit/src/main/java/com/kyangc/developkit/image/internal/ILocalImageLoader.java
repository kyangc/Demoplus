package com.kyangc.developkit.image.internal;

import android.view.View;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by chengkangyang on 十月.18.2015
 */
public interface ILocalImageLoader {

    ConcurrentHashMap<Long, Boolean> workingMap = new ConcurrentHashMap<>();

    void loadLocalImage(View v, LocalImageEntity image);

    class LocalImageEntity {

        String originPath;

        String thumbPath;

        Long id;

        public LocalImageEntity() {
        }

        public LocalImageEntity(String originPath, String thumbPath, Long id) {
            this.originPath = originPath;
            this.thumbPath = thumbPath;
            this.id = id;
        }

        public String getOriginPath() {
            return originPath;
        }

        public LocalImageEntity setOriginPath(String originPath) {
            this.originPath = originPath;
            return this;
        }

        public String getThumbPath() {
            return thumbPath;
        }

        public LocalImageEntity setThumbPath(String thumbPath) {
            this.thumbPath = thumbPath;
            return this;
        }

        public Long getId() {
            return id;
        }

        public LocalImageEntity setId(Long id) {
            this.id = id;
            return this;
        }

        public String getPath() {
            return getThumbPath() == null ? (getOriginPath() == null ? null : getOriginPath())
                    : getThumbPath();
        }

        @Override
        public String toString() {
            return "LocalImageEntity{" +
                    "originPath='" + originPath + '\'' +
                    ", thumbPath='" + thumbPath + '\'' +
                    ", id=" + id +
                    '}';
        }
    }
}
