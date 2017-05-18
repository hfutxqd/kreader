package com.onyx.edu.reader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.request.ChangeViewConfigRequest;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

/**
 * Created by Joy on 2016/5/24.
 */
public class ChangeViewConfigAction extends BaseAction {

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        BaseReaderRequest config = new ChangeViewConfigRequest(readerDataHolder.getDisplayWidth(),
                readerDataHolder.getDisplayHeight());
        readerDataHolder.submitRenderRequest(config, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                BaseCallback.invoke(callback, request, e);
            }
        });
    }
}
