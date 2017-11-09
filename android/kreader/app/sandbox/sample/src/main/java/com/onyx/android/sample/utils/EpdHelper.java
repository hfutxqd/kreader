package com.onyx.android.sample.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by john on 17/8/2017.
 */

public class EpdHelper {

    private static final String TAG = EpdHelper.class.getSimpleName();

    static private class UpdateEntry {
        public Bitmap updBuffer;
        public boolean fullMerged = false;
        public UpdateEntry(final Bitmap bitmap) {
            updBuffer = bitmap;
            fullMerged = false;
        }
    }

    static public class Lut {
        public ArrayList<Point> pixels = new ArrayList<>();
        public Rect boudingRect = new Rect();
        public int frameIndex;
        public int maxFrame;
    }

    public static class Conflict {
        public Rect boudingRect = new Rect();
        public ArrayList<Point> pixels = new ArrayList<>();
    }

    private List<UpdateEntry> updBufferList = new ArrayList<>();
    private List<Bitmap> mergedUpdBufferLst = new ArrayList<>();
    private Bitmap workingBuffer;
    private Bitmap mcu;
    private int maxFrame = 30;
    private int currentFrame = 0;
    private int frameStep = 5;
    private String finalPath;

    public void init(final List<String> pathList) {
        for(String path : pathList) {
            updBufferList.add(new UpdateEntry(ImageUtils.loadBitmapFromFile(path)));
        }
        finalPath = pathList.get(pathList.size() - 1);

        workingBuffer = ImageUtils.loadBitmapFromFile(pathList.get(0));
        mcu = ImageUtils.loadBitmapFromFile(pathList.get(0));
        mcu.eraseColor(Color.argb(0xff, 0, 0, maxFrame));
    }

    public void merge() {
        ListIterator<UpdateEntry> it = updBufferList.listIterator();
        while (it.hasNext()) {
            int index = it.nextIndex();
            UpdateEntry updateEntry = it.next();
            if (updateEntry.fullMerged) {
                continue;
            }
            Bitmap upd = updateEntry.updBuffer;
            Bitmap originUpd = ImageUtils.create(upd);
            Bitmap originWb = ImageUtils.create(workingBuffer);
            int state = ImageUtils.merge(upd, workingBuffer, mcu, maxFrame);
            Log.e(TAG, "Frame index: " + currentFrame +  " merge state: " + state + " update buffer index: " + index);
            if ((state & ImageUtils.SOMETHING_MERGED) > 0) {
                dump(originUpd, originWb, upd, workingBuffer, index);
                break;
            }
            if (state == ImageUtils.NOTHING_TO_MERGE) {
                mergedUpdBufferLst.add(upd);
                updateEntry.fullMerged = true;
                Log.e(TAG, "removed upd buffer: " + index);
            }
        }
    }

    public void flush(final Bitmap finalUpdateBitmap) {
        while (!ImageUtils.isFinished(finalUpdateBitmap)) {
            int state = ImageUtils.merge(finalUpdateBitmap, workingBuffer, mcu, maxFrame);
            Log.e(TAG, "Flush frame index: " + currentFrame + " merge state: " + state + " update buffer last");
            if ((state & ImageUtils.SOMETHING_MERGED) > 0) {
                dump(finalUpdateBitmap, workingBuffer, finalUpdateBitmap, workingBuffer, 1000);
            }
            nextFrame();
        }
    }

    public boolean isFinished() {
        for(UpdateEntry updateEntry : updBufferList) {
            if (!updateEntry.fullMerged) {
                return false;
            }
        }
        return true;
    }

    public int verify() {
        final Bitmap src = ImageUtils.loadBitmapFromFile(finalPath);
        final Bitmap dst = workingBuffer;

        if (src.getWidth() != dst.getWidth() || src.getHeight() != dst.getHeight()) {
            return Integer.MAX_VALUE;
        }

        int diff = 0;
        for(int y = 0; y < src.getHeight(); ++y) {
            for (int x = 0; x < src.getWidth(); ++x) {
                int v1 = src.getPixel(x, y);
                int v2 = dst.getPixel(x, y);
                if (v1 != v2) {
                    ++diff;
                }
            }
        }
        return diff;
    }

    public void dump(final Bitmap originUpd, final Bitmap originWb, final Bitmap mergedUpd, final Bitmap mergedWb, int index) {
        String path;

        boolean dumpUpd = false;
        if (dumpUpd) {
            path = String.format("/mnt/sdcard/merged-upd-frame-" + currentFrame + "-update-buffer-" + index + ".png");
            FileUtils.deleteFile(path);
            BitmapUtils.saveBitmap(mergedUpd, path);
            Log.e(TAG, "save upd buffer: " + path);
        }

        path = String.format("/mnt/sdcard/result-frame-" + currentFrame + "-update-buffer-index-" + index + ".png");
        FileUtils.deleteFile(path);
        Bitmap result = ImageUtils.merge(originUpd, originWb, mergedUpd, mergedWb);
        BitmapUtils.saveBitmap(result, path);
        Log.e(TAG, "save result buffer: " + path);

//        Bitmap result = ImageUtils.merge(upd, workingBuffer);
//        path = String.format("/mnt/sdcard/result-" + currentFrame + "-update-buffer-" + updIndex + ".png");
//        FileUtils.deleteFile(path);
//        BitmapUtils.saveBitmap(result, path);
//        Log.e(TAG, "save result bitmap: " + path);
    }

    public void nextFrame() {
        currentFrame += frameStep;
        ImageUtils.nextFrame(mcu, maxFrame, frameStep);
    }
}