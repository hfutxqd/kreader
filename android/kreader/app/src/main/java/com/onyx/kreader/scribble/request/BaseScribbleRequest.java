package com.onyx.kreader.scribble.request;

import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.common.RequestManager;
import com.onyx.kreader.scribble.ScribbleManager;

/**
 * Created by zhuzeng on 6/3/16.
 */
public class BaseScribbleRequest extends BaseRequest {

    public void execute(final ScribbleManager scribbleManager) throws Exception {
    }

    public void afterExecute(final RequestManager requestManager) {
        if (getException() != null) {
            getException().printStackTrace();
        }
        benchmarkEnd();

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (getCallback() != null) {
                    getCallback().done(BaseScribbleRequest.this, getException());
                }
                requestManager.releaseWakeLock();
            }};

        if (isRunInBackground()) {
            requestManager.getLooperHandler().post(runnable);
        } else {
            runnable.run();
        }
    }

}
