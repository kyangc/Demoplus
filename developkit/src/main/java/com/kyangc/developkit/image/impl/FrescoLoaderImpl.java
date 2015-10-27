package com.kyangc.developkit.image.impl;

import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Supplier;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.kyangc.developkit.image.internal.IFrescoLoader;
import com.kyangc.developkit.image.internal.IImageLoader;
import com.kyangc.developkit.utils.StorageUtils;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import java.io.File;

/**
 * Created by chengkangyang on 十月.15.2015
 */
public class FrescoLoaderImpl implements IFrescoLoader, IImageLoader {

    public static String FRESCO_CACHE_NORMAL = "image_cache_normal";

    public static String FRESCO_CACHE_SMALL = "image_cache_small";

    private static FrescoLoaderImpl ourInstance = new FrescoLoaderImpl();

    public static FrescoLoaderImpl getInstance() {
        return ourInstance;
    }

    private FrescoLoaderImpl() {

    }

    @Override
    public void init(final Context context) {
        long availableStorage = StorageUtils.getAvailableExternalStorageSize();
        int maxDiskCacheSize = Math.min(300 * 1024 * 1024, (int) (availableStorage / 10));
        int smallMaxDiskCacheSize = Math.min(30 * 1024 * 1024, (int) (availableStorage / 30));

        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder()
                .setBaseDirectoryPathSupplier(new Supplier<File>() {
                    public File get() {
                        return StorageUtils.getIsSDAvalable() ?
                                context.getApplicationContext().getExternalCacheDir() :
                                context.getApplicationContext().getCacheDir();
                    }
                })
                .setBaseDirectoryName(FRESCO_CACHE_NORMAL)
                .setMaxCacheSize(maxDiskCacheSize)
                .setMaxCacheSizeOnLowDiskSpace(10485760L)
                .setMaxCacheSizeOnVeryLowDiskSpace(2097152L)
                .build();
        DiskCacheConfig smallDiskCacheConfig = DiskCacheConfig.newBuilder()
                .setBaseDirectoryPathSupplier(new Supplier<File>() {
                    public File get() {
                        return StorageUtils.getIsSDAvalable() ?
                                context.getApplicationContext().getExternalCacheDir() :
                                context.getApplicationContext().getCacheDir();
                    }
                })
                .setBaseDirectoryName(FRESCO_CACHE_SMALL)
                .setMaxCacheSize(smallMaxDiskCacheSize)
                .setMaxCacheSizeOnLowDiskSpace(10485760L)
                .setMaxCacheSizeOnVeryLowDiskSpace(2097152L)
                .build();
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(context)
                .setMainDiskCacheConfig(diskCacheConfig)
                .setSmallImageDiskCacheConfig(smallDiskCacheConfig)
                .setNetworkFetcher(new ImageNetworkFetcher())
                .build();
        Fresco.initialize(context, config);
    }

    @Override
    public void loadImage(@NonNull View v, @NonNull String uri) {
        if (v instanceof SimpleDraweeView) {
            SimpleDraweeView draweeView = ((SimpleDraweeView) v);
            draweeView.setImageURI(Uri.parse(uri));
        }
    }

    @Override
    public void loadDetailImage(@NonNull View v, @NonNull String uri) {
        loadDetailImage(v, uri, null);
    }

    @Override
    public void loadDetailImage(@NonNull View v, @NonNull String uri,
            ControllerListener<ImageInfo> listener) {
        if (v instanceof SimpleDraweeView) {
            SimpleDraweeView draweeView = (SimpleDraweeView) v;
            Uri lUri = Uri.parse(uri);
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setUri(lUri)
                    .setAutoPlayAnimations(true)
                    .setControllerListener(listener)
                    .setOldController(draweeView.getController())
                    .build();
            draweeView.setController(controller);
        }
    }

    @Override
    public void loadSmallImage(@NonNull View v, @NonNull String uri) {
        try {
            SimpleDraweeView draweeView = ((SimpleDraweeView) v);
            Uri lUri = Uri.parse(uri);
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(lUri)
                    .setImageType(ImageRequest.ImageType.SMALL)
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setUri(lUri)
                    .setImageRequest(request)
                    .setOldController(draweeView.getController())
                    .build();
            draweeView.setController(controller);
        } catch (ClassCastException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public File getCachedImageOnDisk(@NonNull String uri) {
        File localFile = null;
        if (!TextUtils.isEmpty(uri)) {
            CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(
                    ImageRequest.fromUri(uri));
            if (ImagePipelineFactory.getInstance().getMainDiskStorageCache().hasKey(cacheKey)) {
                BinaryResource resource = ImagePipelineFactory.getInstance()
                        .getMainDiskStorageCache().getResource(cacheKey);
                localFile = ((FileBinaryResource) resource).getFile();
            } else if (ImagePipelineFactory.getInstance().getSmallImageDiskStorageCache()
                    .hasKey(cacheKey)) {
                BinaryResource resource = ImagePipelineFactory.getInstance()
                        .getSmallImageDiskStorageCache().getResource(cacheKey);
                localFile = ((FileBinaryResource) resource).getFile();
            }
        }
        return localFile;
    }
}
