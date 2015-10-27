package com.kyangc.developkit.helper;

import com.kyangc.developkit.image.impl.ImageLoaderImpl;
import com.kyangc.developkit.image.internal.ILocalImageLoader;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by chengkangyang on 十月.19.2015
 */
public class GalleryHelper implements ILocalImageLoader {

    public static final String TAG = "Gallery Helper";

    private final static String[] PROJECTION_PIC = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_MODIFIED
    };

    private final static String[] PROJECTION_PIC_THUMBNAIL = {
            MediaStore.Images.Thumbnails._ID,
            MediaStore.Images.Thumbnails.IMAGE_ID,
            MediaStore.Images.Thumbnails.DATA
    };

    private final static String SCHEME_TYPE_FILE = "file://";

    private static final String MIN_HEIGHT = "0";

    private static final String MIN_WIDTH = "0";

    private static final String MIN_SIZE = "20480";

    private Context mContext;

    private static GalleryHelper instance = new GalleryHelper();

    private static ImageLoaderImpl mImageL;

    public static synchronized GalleryHelper getInstance() {
        return instance;
    }

    public GalleryHelper() {
        mImageL = ImageLoaderImpl.getInstance();
    }

    public GalleryHelper init(Context context) {
        if (this.mContext == null) {
            this.mContext = context;
        }
        return this;
    }

    private Cursor getPhotoCursor(String selection, String[] selectionArgs) {
        return mContext.getContentResolver()
                .query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        PROJECTION_PIC,
                        selection,
                        selectionArgs,
                        MediaStore.Images.Media.DATE_MODIFIED + " DESC");
    }

    private Cursor getThumbCursor(String selection, String[] selectionArgs) {
        return mContext.getContentResolver()
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

    public ILocalImageLoader.LocalImageEntity getPhoto(@NonNull Long id) {

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

    public void prepareThumbnail(@NonNull Long id) {
        Bitmap bitmap = MediaStore.Images.Thumbnails
                .getThumbnail(
                        mContext.getContentResolver(),
                        id,
                        MediaStore.Images.Thumbnails.MICRO_KIND,
                        new BitmapFactory.Options());
        if (bitmap != null) {
            bitmap.recycle();
        }
    }

    public void cancelPrepareThumbnail(@NonNull Long id) {
        MediaStore.Images.Thumbnails
                .cancelThumbnailRequest(mContext.getContentResolver(), id);
    }

    public List<LocalImageEntity> getLocalPhotoList() {

        Cursor thumbCursor = getThumbCursor(null, null);
        Cursor photoCursor = getPhotoCursor(MediaStore.Images.Media.SIZE + ">=?",
                new String[]{MIN_SIZE});

        LinkedHashMap<Long, LocalImageEntity> dataMap = new LinkedHashMap<>();

        for (int i = 0; i < photoCursor.getCount(); i++) {
            photoCursor.moveToPosition(i);
            Long id = getPhotoId(photoCursor);
            String path = getPhotoPath(photoCursor);
            dataMap.put(id, new LocalImageEntity()
                    .setId(id)
                    .setOriginPath(path));
        }

        for (int i = 0; i < thumbCursor.getCount(); i++) {
            thumbCursor.moveToPosition(i);
            Long id = getThumbId(thumbCursor);
            if (dataMap.containsKey(id)) {
                dataMap.get(id).setThumbPath(getThumbPath(thumbCursor));
            }
        }

        thumbCursor.close();
        photoCursor.close();

        Collection<LocalImageEntity> values = dataMap.values();
        return Arrays.asList(values.toArray(new LocalImageEntity[values.size()]));
    }

    @Override
    public void loadLocalImage(View v, final LocalImageEntity imageEntity) {
        if (!TextUtils.isEmpty(imageEntity.getThumbPath())) {
            mImageL.loadImage(v, imageEntity.getThumbPath());
            return;
        }

        mImageL.loadImage(v, imageEntity.getOriginPath());

        if (workingMap.containsKey(imageEntity.getId())
                || workingMap.size() > 10) {
            return;
        }

        Observable.create(
                new Observable.OnSubscribe<LocalImageEntity>() {
                    @Override
                    public void call(Subscriber<? super LocalImageEntity> subscriber) {
                        workingMap.put(imageEntity.getId(), true);
                        prepareThumbnail(imageEntity.getId());
                        subscriber.onNext(getPhoto(imageEntity.getId()));
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<LocalImageEntity>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e.toString());
                    }

                    @Override
                    public void onNext(LocalImageEntity localImageEntity) {
                        if (localImageEntity != null) {
                            workingMap.remove(localImageEntity.getId());
                            imageEntity.setThumbPath(localImageEntity.getThumbPath());
                            Timber.i(localImageEntity.getId().toString() + " prepared");
                        }
                    }
                });
    }
}
