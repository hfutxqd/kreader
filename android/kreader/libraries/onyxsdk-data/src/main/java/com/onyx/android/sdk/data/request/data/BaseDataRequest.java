package com.onyx.android.sdk.data.request.data;

import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.compatability.OnyxThumbnail.ThumbnailKind;
import com.onyx.android.sdk.data.provider.DataProviderBase;

/**
 * Created by zhuzeng on 5/31/16.
 */
public class BaseDataRequest extends BaseRequest {
    public int thumbnailLimit = 20;
    public ThumbnailKind thumbnailKind = ThumbnailKind.Middle;

    public void execute(final DataManager dataManager) throws Exception {
    }

    public void afterExecute(final DataManager dataManager) {
        if (getException() != null) {
            getException().printStackTrace();
        }
        benchmarkEnd();

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (getCallback() != null) {
                    getCallback().done(BaseDataRequest.this, getException());
                }
                dataManager.getRequestManager().releaseWakeLock();
            }};

        if (isRunInBackground()) {
            dataManager.getRequestManager().getLooperHandler().post(runnable);
        } else {
            runnable.run();
        }
    }

    protected DataProviderBase getDataProviderBase(final DataManager dataManager){
        return dataManager.getDataProviderManager().getDataProvider();
    }

}
