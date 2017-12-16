package com.onyx.android.sdk.device;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.PowerManager;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;

import com.onyx.android.sdk.api.device.epd.EPDMode;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.api.device.epd.UpdateScheme;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Joy on 2016/5/10.
 */
public class BaseDevice {
    private static final String TAG = "BaseDevice";
    private final int ICE_CREAM_SANDWICH = 14;
    private final String SHOW_STATUS_BAR_ACTION = "show_status_bar";
    private final String HIDE_STATUS_BAR_ACTION = "hide_status_bar";
    private final static String ENABLE_WIFI_CONNECT_STATUS_DETECTION_ACTION = "enable_wifi_connect_status_detection_status";
    private final static String ARGS_WIFI_CONNECT_DETECTION_FLAG = "args_wifi_connect_detection_flag";

    public File getStorageRootDirectory() {
        return android.os.Environment.getExternalStorageDirectory();
    }

    public File getExternalStorageDirectory() {
        return android.os.Environment.getExternalStorageDirectory();
    }

    public File getRemovableSDCardDirectory() {
        File storage_root = getExternalStorageDirectory();

        // if system has an emulated SD card(/mnt/sdcard) provided by device's NAND flash,
        // then real SD card will be mounted as a child directory(/mnt/sdcard/extsd) in it, which names "extsd" here
        final String SDCARD_MOUNTED_FOLDER = "extsd";

        File extsd = new File(storage_root, SDCARD_MOUNTED_FOLDER);
        if (extsd.exists()) {
            return extsd;
        } else {
            return storage_root;
        }
    }

    //TODO:for FileUtil to parse uri.
    public File getBluetoothRootDirectory() {
        return new File(getExternalStorageDirectory().getPath() + File.separator + "bluetooth");
    }

    public boolean isFileOnRemovableSDCard(File file) {
        return file.getAbsolutePath().startsWith(getRemovableSDCardDirectory().getAbsolutePath());
    }

    public PowerManager.WakeLock newWakeLock(Context context, String tag) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, tag);
    }

    public PowerManager.WakeLock newWakeLockWithFlags(Context context, int flags, String tag) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return pm.newWakeLock(flags, tag);
    }

    public void useBigPen(boolean use) {
    }

    public void stopTpd() {
    }

    public void startTpd() {
    }

    public void enableTpd(boolean enable) {
    }

    public float getTouchWidth() {
        return 0;
    }

    public float getTouchHeight() {
        return 0;
    }

    public float getEpdWidth() {
        return 0;
    }

    public float getEpdHeight() {
        return 0;
    }

    public void mapToView(View view, float[] src, float[] dst) {
    }

    public void mapToEpd(View view, float[] src, float[] dst) {
    }

    public Rect mapToEpd(View view, Rect srcRect) {
        float src[] = new float[]{srcRect.left, srcRect.top};
        float dst[] = new float[2];
        float dst2[] = new float[2];
        mapToEpd(view, src, dst);
        src[0] = srcRect.right;
        src[1] = srcRect.bottom;
        mapToEpd(view, src, dst2);
        return new Rect(
                (int)Math.min(dst[0], dst2[0]),
                (int)Math.min(dst[1], dst2[1]),
                (int)Math.max(dst[0], dst2[0]),
                (int)Math.max(dst[1], dst2[1]));
    }

    public void mapFromRawTouchPoint(View view, float[] src, float[] dst) {
    }

    public void mapToRawTouchPoint(View view, float[] src, float[] dst) {
    }

    public RectF mapToRawTouchPoint(View view, RectF rect) {
        float src[] = new float[]{rect.left, rect.top};
        float dst[] = new float[2];
        float dst2[] = new float[2];
        mapToRawTouchPoint(view, src, dst);

        src[0] = rect.right;
        src[1] = rect.bottom;
        mapToRawTouchPoint(view, src, dst2);
        return new RectF(
                Math.min(dst[0], dst2[0]),
                Math.min(dst[1], dst2[1]),
                Math.max(dst[0], dst2[0]),
                Math.max(dst[1], dst2[1]));
    }

    public int getFrontLightBrightnessMinimum(Context context) {
        return 0;
    }

    public int getFrontLightBrightnessMaximum(Context context) {
        return 0;
    }

    public int getFrontLightBrightnessDefault(Context context) {
        return 0;
    }

    public boolean openFrontLight(Context context) {
        return false;
    }

    public boolean closeFrontLight(Context context) {
        return false;
    }

    public boolean setLedColor(final String ledColor, final int on) {
        return false;
    }

    public int getFrontLightDeviceValue(Context context) {
        return 0;
    }

    public List<Integer> getFrontLightValueList(Context context) {
        return new ArrayList<Integer>();
    }

    public boolean setFrontLightDeviceValue(Context context, int value) {
        return false;
    }

    public boolean setNaturalLightConfigValue(Context context, int value) {
        return false;
    }

    public int getFrontLightConfigValue(Context context) {
        return 0;
    }

    public boolean setFrontLightConfigValue(Context context, int value) {
        return false;
    }

    public EPDMode getEpdMode() {
        return EPDMode.AUTO;
    }

    public boolean setEpdMode(Context context, EPDMode mode) {
        return false;
    }

    public boolean setEpdMode(View view, EPDMode mode) {
        return false;
    }

    public UpdateMode getViewDefaultUpdateMode(View view) {
        return UpdateMode.GU;
    }

    public boolean setViewDefaultUpdateMode(View view, UpdateMode mode) {
        return false;
    }

    public void resetViewUpdateMode(View view) {
    }

    public UpdateMode getSystemDefaultUpdateMode() {
        return UpdateMode.GU;
    }

    public boolean setSystemDefaultUpdateMode(UpdateMode mode) {
        return false;
    }

    public boolean applyApplicationFastMode(final String application, boolean enable, boolean clear) {
        return false;
    }

    public boolean applyApplicationFastMode(final String application, boolean enable, boolean clear, UpdateMode repeatMode, int repeatLimit) {
        return false;
    }

    public boolean setDisplayScheme(int scheme) {
        return false;
    }

    public void waitForUpdateFinished() {
    }

    public void invalidate(View view, UpdateMode mode) {
        view.invalidate();
    }

    public void invalidate(View view, int left, int top, int right, int bottom, UpdateMode mode) {
    }

    public boolean enableScreenUpdate(View view, boolean enable) {
        return false;
    }


    public void refreshScreen(View view, UpdateMode mode) {
    }

    public void refreshScreenRegion(View view, int left, int top, int width, int height, UpdateMode mode) {
    }

    public void screenshot(View view, int r, final String path) {
    }

    public boolean supportDFB() {
        return false;
    }

    public boolean supportRegal() {
        return false;
    }

    public void holdDisplay(boolean hold, UpdateMode updateMode, int ignoreFrame) {}

    public void setStrokeColor(int color) {
    }

    public void setStrokeStyle(int style) {
    }

    public void setStrokeWidth(float width) {}

    public void setPainterStyle(boolean antiAlias, Paint.Style strokeStyle, Paint.Join joinStyle, Paint.Cap capStyle) {
    }

    public void moveTo(float x, float y, float width) {
    }

    public void moveTo(View view, float x, float y, float width) {
    }

    public void lineTo(float x, float y, UpdateMode mode) {
    }

    public void lineTo(View view, float x, float y, UpdateMode mode) {
    }

    public void quadTo(float x, float y, UpdateMode mode) {
    }

    public void quadTo(View view, float x, float y, UpdateMode mode) {
    }

    public float startStroke(float baseWidth, float x, float y, float pressure, float size, float time) {
        return baseWidth;
    }

    public float addStrokePoint(float baseWidth, float x, float y, float pressure, float size, float time) {
        return baseWidth;
    }

    public float finishStroke(float baseWidth, float x, float y, float pressure, float size, float time) {
        return baseWidth;
    }

    public void enterScribbleMode(View view) {
    }

    public void leaveScribbleMode(View view) {
    }

    public void enablePost(View view, int enable) {
    }

    public void resetEpdPost() {
    }

    public void setScreenHandWritingPenState(View view, int penState) {
    }

    public void setScreenHandWritingRegionLimit(View view) {
    }

    public void setScreenHandWritingRegionLimit(View view, int left, int top, int right, int bottom) {
    }

    public void setScreenHandWritingRegionLimit(View view, int[] array) {
    }


    public void setScreenHandWritingRegionLimit(View view, Rect[] regions) {
    }

    public void setScreenHandWritingRegionExclude(View view, int[] array) {
    }

    public void setScreenHandWritingRegionExclude(View view, Rect[] regions) {
    }

    public void postInvalidate(View view, UpdateMode mode) {
        view.postInvalidate();
    }

    public boolean setSystemUpdateModeAndScheme(UpdateMode mode, UpdateScheme scheme, int count) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean clearSystemUpdateModeAndScheme() {
        // TODO Auto-generated method stub
        return false;
    }

    public void wifiLock(Context context, String className) {
        // TODO Auto-generated method stub

    }

    public void wifiUnlock(Context context, String className) {
        // TODO Auto-generated method stub

    }

    public void wifiLockClear(Context context) {
        // TODO Auto-generated method stub

    }

    public Map<String, Integer> getWifiLockMap(Context context) {
        // TODO Auto-generated method stub
        return null;
    }

    public void setWifiLockTimeout(Context context, long ms) {
        // TODO Auto-generated method stub

    }

    public String getEncryptedDeviceID() {
        return null;
    }

    public void led(Context context, boolean on) {

    }

    public void setVCom(Context context, int mv, String path) {

    }

    public void updateWaveform(Context context, String path, String target) {

    }

    public int getVCom(Context context, String path) {
        return 0;
    }

    public String readSystemConfig(Context context, String key) {
        return "";
    }

    public boolean saveSystemConfig(Context context, String key, String mv) {
        return false;
    }

    public void updateMetadataDB(Context context, String path, String target) {
    }

    public Point getWindowWidthAndHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        point.x = wm.getDefaultDisplay().getWidth();
        point.y = wm.getDefaultDisplay().getHeight();
        return point;
    }

    public void hideSystemStatusBar(Context context) {
        sendIntentToSystemStatusBar(context, HIDE_STATUS_BAR_ACTION);
    }

    public void showSystemStatusBar(Context context) {
        sendIntentToSystemStatusBar(context, SHOW_STATUS_BAR_ACTION);
    }

    public void enableWifiDetect(Context context) {
        enableWifiDetect(context, true);
    }

    public void enableWifiDetect(Context context, boolean enableDetect) {
        Intent intent = new Intent(ENABLE_WIFI_CONNECT_STATUS_DETECTION_ACTION);
        intent.putExtra(ARGS_WIFI_CONNECT_DETECTION_FLAG, enableDetect);
        sendIntentToSystemStatusBar(context, intent);
    }

    private void sendIntentToSystemStatusBar(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= ICE_CREAM_SANDWICH) {
            context.sendBroadcast(intent);
        }
    }

    private void sendIntentToSystemStatusBar(Context context, String action) {
        Intent intent = new Intent(action);
        sendIntentToSystemStatusBar(context, intent);
    }

    public void stopBootAnimation() {
    }

    public void disableA2ForSpecificView(View view) {
    }

    public void enableA2ForSpecificView(View view) {
    }

    public void setWebViewContrastOptimize(WebView view, boolean enabled) {

    }

    public boolean isLegalSystem(final Context context){
        return true;
    }

    public boolean isTouchable(Context context) {
        return true;
    }

    public void gotoSleep(final Context context) {}

    public void enableRegal(boolean enable) {
    }

    public boolean hasWifi(Context context) {
        return true;
    }

    public void setQRShowConfig(int orientation, int startX, int startY) {}

    public void setInfoShowConfig(int orientation, int startX, int startY) {}

    public void setUpdListSize(int size) {
    }

    public boolean inSystemFastMode() {
        return false;
    }

    public String getUpgradePackageName() {
        return "update.zip";
    }

    public boolean shouldVerifyUpdateModel() {
        return true;
    }

    public void enableCapacitanceTp(boolean enable) {
    }

    public void enableElectromagneticTp(boolean enable) {
    }

    public void mergeDisplayUpdate(int timeout, UpdateMode mode) {}
}
