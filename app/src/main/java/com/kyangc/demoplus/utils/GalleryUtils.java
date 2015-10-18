package com.kyangc.demoplus.utils;

import com.kyangc.demoplus.app.DemoApp;
import com.kyangc.demoplus.image.ILocalImageLoader;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import java.util.TreeMap;

/**
 * Created by chengkangyang on 十月.16.2015
 */
public class GalleryUtils {

    public final static String[] PROJECTION_PIC = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_MODIFIED
    };

    public final static String[] PROJECTION_PIC_THUMBNAIL = {
            MediaStore.Images.Thumbnails._ID,
            MediaStore.Images.Thumbnails.IMAGE_ID,
            MediaStore.Images.Thumbnails.DATA
    };

    public final static String SCHEME_TYPE_FILE = "file://";

    private static Cursor getPhotoCursor(String selection, String[] selectionArgs) {
        return DemoApp.getAppContext().getContentResolver()
                .query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        PROJECTION_PIC,
                        selection,
                        selectionArgs,
                        null);
    }

    private static Cursor getThumbCursor(String selection, String[] selectionArgs) {
        return DemoApp.getAppContext().getContentResolver()
                .query(
                        MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                        PROJECTION_PIC_THUMBNAIL,
                        selection,
                        selectionArgs,
                        null);
    }

    private static Long getPhotoId(Cursor cursor) {
        return cursor == null || cursor.getCount() == 0 ? null
                : cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));
    }

    private static String getPhotoPath(Cursor cursor) {
        return (cursor == null || cursor.getCount() == 0) ? null
                : SCHEME_TYPE_FILE + cursor
                        .getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
    }

    private static Long getPhotoModifiedTime(Cursor cursor) {
        return (cursor == null || cursor.getCount() == 0) ? null
                : cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));
    }

    private static Long getThumbId(Cursor cursor) {
        return cursor == null || cursor.getCount() == 0 ? null
                : cursor.getLong(
                        cursor.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID));
    }

    private static String getThumbPath(Cursor cursor) {
        return (cursor == null || cursor.getCount() == 0) ? null
                : SCHEME_TYPE_FILE + cursor.getString(
                        cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
    }

    public static ILocalImageLoader.LocalImageEntity getPhoto(@NonNull Long id) {

        Cursor photoCursor = getPhotoCursor(MediaStore.Images.Media._ID + "=?",
                new String[]{id.toString()});

        Cursor thumbCursor = getThumbCursor(MediaStore.Images.Thumbnails.IMAGE_ID + "=?",
                new String[]{id.toString()});

        try {
            if (photoCursor.getCount() == 0) {
                return null;
            } else {
                photoCursor.moveToFirst();
                thumbCursor.moveToFirst();
                return new ILocalImageLoader.LocalImageEntity()
                        .setId(id)
                        .setOriginPath(getPhotoPath(photoCursor))
                        .setThumbPath(getThumbPath(thumbCursor));
            }
        } finally {
            photoCursor.close();
            thumbCursor.close();
        }
    }

    public static void prepareThumbnail(@NonNull Long id) {
        Bitmap bitmap = MediaStore.Images.Thumbnails
                .getThumbnail(
                        DemoApp.getAppContext().getContentResolver(),
                        id,
                        MediaStore.Images.Thumbnails.MICRO_KIND,
                        new BitmapFactory.Options());
        if (bitmap != null) {
            bitmap.recycle();
        }
    }

    public static void cancelPrepareThumbnail(@NonNull Long id) {
        MediaStore.Images.Thumbnails
                .cancelThumbnailRequest(DemoApp.getAppContext().getContentResolver(), id);
    }

    public static TreeMap<Long, ILocalImageLoader.LocalImageEntity> getPhotoList() {

        Cursor thumbCursor = GalleryUtils.getThumbCursor(null, null);
        Cursor photoCursor = GalleryUtils
                .getPhotoCursor(MediaStore.Images.Media.SIZE + ">=?", new String[]{"20480"});

        TreeMap<Long, ILocalImageLoader.LocalImageEntity> dataMap = new TreeMap<>();

        for (int i = 0; i < photoCursor.getCount(); i++) {
            photoCursor.moveToPosition(i);
            Long id = GalleryUtils.getPhotoId(photoCursor);
            String path = GalleryUtils.getPhotoPath(photoCursor);
            dataMap.put(id, new ILocalImageLoader.LocalImageEntity()
                    .setId(id)
                    .setOriginPath(path));
        }

        for (int i = 0; i < thumbCursor.getCount(); i++) {
            thumbCursor.moveToPosition(i);
            Long id = GalleryUtils.getThumbId(thumbCursor);
            if (dataMap.containsKey(id)) {
                dataMap.get(id).setThumbPath(GalleryUtils.getThumbPath(thumbCursor));
            }
        }

        thumbCursor.close();
        photoCursor.close();

        return dataMap;
    }
}
