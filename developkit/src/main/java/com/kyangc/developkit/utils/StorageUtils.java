package com.kyangc.developkit.utils;

import android.os.Environment;
import android.os.StatFs;

import java.io.File;

/**
 * Created by chengkangyang on 十月.27.2015
 */
public class StorageUtils {

    /**
     * 获取SD卡是否可用
     */
    public static boolean getIsSDAvalable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SD卡路径
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator;
    }

    /**
     * 获取SD卡的剩余容量 单位byte
     */
    public static long getSDCardAllSize() {
        if (getIsSDAvalable()) {
            StatFs stat = new StatFs(getSDCardPath());
            // 获取空闲的数据块的数量
            long availableBlocks = (long) stat.getAvailableBlocks() - 4;
            // 获取单个数据块的大小（byte）
            long freeBlocks = stat.getAvailableBlocks();
            return freeBlocks * availableBlocks;
        }
        return 0;
    }

    /**
     * 获取指定路径所在空间的剩余可用容量字节数，单位byte
     */
    public static long getFreeBytes(String filePath) {
        // 如果是sd卡的下的路径，则获取sd卡可用容量
        if (filePath.startsWith(getSDCardPath())) {
            filePath = getSDCardPath();
        } else {// 如果是内部存储的路径，则获取内存存储的可用容量
            filePath = Environment.getDataDirectory().getAbsolutePath();
        }
        StatFs stat = new StatFs(filePath);
        long availableBlocks = (long) stat.getAvailableBlocks() - 4;
        return stat.getBlockSize() * availableBlocks;
    }

    /**
     * 获取系统存储路径
     */
    public static String getRootDirectoryPath() {
        return Environment.getRootDirectory().getAbsolutePath();
    }

    public static long getAvailableExternalStorageSize(){
        long size=0;
        if(SysUtils.SDK_VERSION>=18){
            if (Environment.getExternalStorageDirectory() != null) {
                StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
                size = stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
            }
            return size;
        }else if(Environment.getExternalStorageDirectory()!=null){
            try {
                StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
                size=(long)stat.getAvailableBlocks()*(long)stat.getBlockSize();
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
        return size;
    }
}
