package com.onyx.edu.note;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.View;

import com.onyx.android.sdk.ui.compat.AppCompatImageViewCollection;
import com.onyx.android.sdk.ui.compat.AppCompatUtils;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.edu.note.util.NotePreference;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.config.ShapeGeneratedDatabaseHolder;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by solskjaer49 on 2017/5/17 17:06.
 */

public class NoteApplication extends Application {
    private static NoteManager noteManager;
    private static NoteApplication instance;

    public static NoteManager getNoteManager() {
        if (noteManager == null) {
            noteManager = NoteManager.sharedInstance(instance);
        }
        return noteManager;
    }

    public static void initWithAppConfig(final Activity activity) {
        DeviceUtils.setFullScreenOnCreate(activity, true);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        // Normal app init code...
        instance = this;
        initDataProvider(this);
        installExceptionHandler();
        initCompatColorImageConfig();
    }

    public static NoteApplication getInstance() {
        return instance;
    }

    private void initDataProvider(final Context context) {
        FlowConfig.Builder builder = new FlowConfig.Builder(context);
        builder.addDatabaseHolder(ShapeGeneratedDatabaseHolder.class);
        FlowManager.init(builder.build());
        NotePreference.init(this);
    }

    private void installExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                e.printStackTrace();
                final View view = getNoteManager().getView();
                getNoteManager().reset(view);
            }
        });
    }

    private void initCompatColorImageConfig() {
        AppCompatImageViewCollection.setAlignView(AppCompatUtils.isColorDevice(this));
    }
}
