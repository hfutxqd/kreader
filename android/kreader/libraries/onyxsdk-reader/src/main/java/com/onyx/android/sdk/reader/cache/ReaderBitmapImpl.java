package com.onyx.android.sdk.reader.cache;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.facebook.common.references.CloseableReference;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory;
import com.onyx.android.sdk.api.ReaderBitmap;
import com.onyx.android.sdk.reader.host.options.BaseOptions;

/**
 * Created by joy on 8/9/16.
 */
public class ReaderBitmapImpl implements ReaderBitmap {
    private static PlatformBitmapFactory bitmapFactory = Fresco.getImagePipelineFactory().getPlatformBitmapFactory();

    private String key;
    private CloseableReference<Bitmap> bitmap;
    private float gammaCorrection = BaseOptions.getLowerGammaLimit();
    private int emboldenLevel;

    public static ReaderBitmapImpl create(int width, int height, Bitmap.Config config) {
        ReaderBitmapImpl readerBitmap = new ReaderBitmapImpl(width, height, config);
        return readerBitmap;
    }

    public ReaderBitmapImpl() {
        super();
    }

    public ReaderBitmapImpl(int width, int height, Bitmap.Config config) {
        super();
        bitmap = bitmapFactory.createBitmap(width, height, config);
    }

    public boolean isValid() {
        return bitmap != null && bitmap.isValid();
    }

    public void clear() {
        bitmap.get().eraseColor(Color.WHITE);
    }

    public void eraseColor(int white) {
        bitmap.get().eraseColor(white);
    }

    public CloseableReference<Bitmap> getBitmapReference() {
        return bitmap;
    }

    /**
     * add reference of internal bitmap
     * @return
     */
    public ReaderBitmapImpl clone() {
        ReaderBitmapImpl copy = new ReaderBitmapImpl();
        copy.key = key;
        copy.bitmap = bitmap.clone();
        copy.gammaCorrection = gammaCorrection;
        copy.emboldenLevel = emboldenLevel;
        return copy;
    }

    /**
     * subtract reference of internal bitmap
     */
    public void close() {
        bitmap.close();
    }

    public String getKey() {
        return key;
    }

    public Bitmap getBitmap() {
        return bitmap.get();
    }

    public void setGammaCorrection(float correction) {
        this.gammaCorrection = correction;
    }

    public float gammaCorrection() {
        return gammaCorrection;
    }

    public boolean isGammaApplied(final float targetGammaCorrection) {
        return (Float.compare(gammaCorrection, targetGammaCorrection) == 0);
    }

    public boolean isEmboldenApplied(final float targetEmboldenLevel) {
        return emboldenLevel == targetEmboldenLevel;
    }

    public int getEmboldenLevel() {
        return emboldenLevel;
    }

    public void setEmboldenLevel(int emboldenLevel) {
        this.emboldenLevel = emboldenLevel;
    }

    public void attachWith(String key, final CloseableReference<Bitmap> src) {
        this.key = key;
        bitmap = src.clone();
    }
}
