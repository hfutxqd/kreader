package com.onyx.phone.reader.utils;

import android.content.Context;

import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by suicheng on 2017/2/13.
 */
public class TextUtils {
    public static final String NEW_LINE_DOUBLE = "\r\n\r\n";

    public static boolean writeTextToFile(File file, String text) {
        return writeTextToFile(file, text, "GBK");
    }

    public static boolean writeTextToFile(File file, String text, String charsetName) {
        if (file == null || StringUtils.isNullOrEmpty(text)) {
            return false;
        }
        boolean result = false;
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            os.write(text.getBytes(charsetName));
            os.flush();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FileUtils.closeQuietly(os);
        }
        return result;
    }

    public static File getTextFile(Context context, String content) {
        String fileName;
        if (content.length() < 20) {
            fileName = content;
        } else {
            fileName = content.substring(0, 20);
        }
        fileName = FileUtils.fixNotAllowFileName(fileName + ".txt");
        if (StringUtils.isNullOrEmpty(fileName)) {
            fileName += ".txt";
        }
        return new File(context.getExternalCacheDir(), fileName);
    }
}
