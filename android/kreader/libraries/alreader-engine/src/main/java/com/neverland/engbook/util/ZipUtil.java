package com.neverland.engbook.util;

import android.util.Log;


import com.jingdong.app.reader.epub.paging.JDDecryptUtil;
import com.neverland.engbook.level1.AlFileZipEntry;
import com.neverland.engbook.level1.JEBFilesZIP;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by huxiaomao on 17/3/23.
 */

public class ZipUtil {
    private static final String TAG = ZipUtil.class.getCanonicalName();
    private static final List<String> images = new ArrayList<>();
    static {
        images.add(".jpg");
        images.add(".jpeg");
        images.add(".png");
        images.add(".gif");
        images.add(".bmp");
    }

    public static boolean isImage(String zipPath){
        for (String suffix : images){
            if(zipPath.toLowerCase().endsWith(suffix)){
                return true;
            }
        }
        return false;
    }

    public static Map<String,JEBFilesZIP.JEBFileInfo> unzipFile(String dataZip, String dstFolder, final String key,
                                                                final String deviceUUID, final String random) {
        Map<String,JEBFilesZIP.JEBFileInfo> maps = new HashMap<>();

        ZipInputStream zis = null;
        InputStream is = null;

        JDDecryptUtil.key = key;
        JDDecryptUtil.deviceUUID = deviceUUID;
        JDDecryptUtil.random = random;

        try {
            File zipFile = new File(dataZip);
            is = new FileInputStream(zipFile);
            zis = new ZipInputStream(is);
            ZipEntry entry;
            //Log.d(TAG, "start unzip:" + dataZip + "...");
            while ((entry = zis.getNextEntry()) != null) {
                String zipPath = entry.getName();
                int fileSize = 0;
                try {
                    if (entry.isDirectory() || isImage(zipPath)) {

                    } else {
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        byte[] buf = new byte[64 * 1024];
                        int n;
                        int length = 0;
                        while ((n = zis.read(buf, 0, buf.length)) > 0) {
                            os.write(buf, 0, n);
                            length += n;
                        }
                        InputStream inputStream = new ByteArrayInputStream(os.toByteArray());
                        fileSize = JDDecryptUtil.getDecryptFileSize(inputStream,length);
                        inputStream.close();
                        os.close();
                        JEBFilesZIP.JEBFileInfo jebFileInfo = new JEBFilesZIP.JEBFileInfo();
                        jebFileInfo.fileDecryptLength = fileSize;
                        jebFileInfo.uLength = length;
                        maps.put(File.separator + zipPath,jebFileInfo);
                        //Log.d(TAG, "unzip zipPath:" + zipPath + ",fileSize:" + fileSize);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "", e);
                    continue;
                }
            }
            //Log.d(TAG, "unzip over");
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(zis != null){
                    zis.close();
                }
                if(is != null){
                    is.close();
                }
            }catch (Exception e){

            }
        }
        return maps;
    }
}
