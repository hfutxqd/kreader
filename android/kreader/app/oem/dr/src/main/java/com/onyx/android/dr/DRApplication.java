package com.onyx.android.dr;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.onyx.android.dr.device.DeviceConfig;
import com.onyx.android.dr.holder.LibraryDataHolder;
import com.onyx.android.dr.manager.LeanCloudManager;
import com.onyx.android.dr.util.DRPreferenceManager;
import com.onyx.android.sdk.data.CloudStore;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.data.utils.CloudConf;
import com.raizlabs.android.dbflow.config.DRGeneratedDatabaseHolder;
import com.raizlabs.android.dbflow.config.DatabaseHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hehai on 17-6-26.
 */

public class DRApplication extends MultiDexApplication {

    private static DRApplication sInstance;
    private static CloudStore cloudStore;
    private static LibraryDataHolder libraryDataHolder;

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(DRApplication.this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initConfig();
    }

    private void initConfig() {
        try {
            sInstance = this;
            DRPreferenceManager.init(this);
            initDownloadManager();
            initCloudStore();
            initLeanCloud();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initDownloadManager() {
        OnyxDownloadManager.init(sInstance.getApplicationContext());
        OnyxDownloadManager.getInstance();
    }

    private void initCloudStore() {
        CloudStore.init(sInstance.getApplicationContext());
    }

    private void initLeanCloud() {
        LeanCloudManager.initialize(this, DeviceConfig.sharedInstance(this).getLeanCloudApplicationId(),
                DeviceConfig.sharedInstance(this).getLeanCloudClientKey());
    }

    public static CloudStore getCloudStore() {
        if (cloudStore == null) {
            cloudStore = new CloudStore();
            cloudStore.setCloudConf(getCloudConf());
        }
        return cloudStore;
    }

    public static DRApplication getInstance(){
        return sInstance;
    }

    private List<Class<? extends DatabaseHolder>> databaseHolderList() {
        List<Class<? extends DatabaseHolder>> list = new ArrayList<>();
        list.add(DRGeneratedDatabaseHolder.class);
        return list;
    }

    private static CloudConf getCloudConf() {
        String host = DeviceConfig.sharedInstance(sInstance).getCloudContentHost();
        String api = DeviceConfig.sharedInstance(sInstance).getCloudContentApi();
        CloudConf cloudConf = new CloudConf(host, api, Constant.DEFAULT_CLOUD_STORAGE);
        return cloudConf;
    }

    public static LibraryDataHolder getLibraryDataHolder() {
        if (libraryDataHolder == null) {
            libraryDataHolder = new LibraryDataHolder(sInstance);
            libraryDataHolder.setCloudManager(getCloudStore().getCloudManager());
        }
        return libraryDataHolder;
    }
}
