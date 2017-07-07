package com.onyx.kreader.ui;

import com.onyx.kreader.ui.data.SingletonSharedPreference;
import com.onyx.kreader.ui.events.ChangeOrientationEvent;

/**
 * Created by joy on 6/30/17.
 */

public class ReaderIPCManager {

    public static boolean isIgnoreLoadingOrientation() {
        return false;
    }

    public static void onChangeOrientation(final ReaderActivity activity, final ChangeOrientationEvent event) {
        activity.setRequestedOrientation(event.getOrientation());
        SingletonSharedPreference.setScreenOrientation(event.getOrientation());
    }

    public static void onOpenDocumentFailed(final ReaderActivity activity, final String path) {
    }

    public static void onFullScreenChanged(final ReaderActivity activity, final boolean fullScreen) {
        if (fullScreen) {
        } else {
        }
    }

    public static void onBackPressed(final ReaderActivity activity) {
        activity.finish();
    }

    public static void onShowTabHostWidget(final ReaderActivity activity) {
    }

}