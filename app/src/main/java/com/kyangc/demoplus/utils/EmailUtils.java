package com.kyangc.demoplus.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**
 * Created by chengkangyang on 八月.11.2015
 */
public class EmailUtils {

    public static void sendEmailWithAttach(Context context, String addr, String body, String title, File attachment) {
        String[] tos = {addr};
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, tos);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(attachment));
        intent.setType("application/octet-stream");
        context.startActivity(intent);
    }
}
