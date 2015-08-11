package com.kyangc.demoplus.utils;

import android.content.Context;
import android.os.Environment;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by chengkangyang on 八月.07.2015
 */
public class AssetsUtils {

    public static void copyToSD(Context context, String fileName) throws IOException {
        InputStream myInput;
        OutputStream myOutput = null;

        myOutput = new FileOutputStream(Environment.getExternalStorageDirectory() + java.io.File.separator + fileName);
        myInput = context.getAssets().open(fileName);
        byte[] buffer = new byte[1024];
        int length = myInput.read(buffer);
        while (length > 0) {
            myOutput.write(buffer, 0, length);
            length = myInput.read(buffer);
        }

        myOutput.flush();
        myInput.close();
        myOutput.close();
    }
}
