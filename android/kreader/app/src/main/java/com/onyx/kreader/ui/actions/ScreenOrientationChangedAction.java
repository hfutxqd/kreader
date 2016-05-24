package com.onyx.kreader.ui.actions;

import com.onyx.kreader.common.BaseCallback;
import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.host.request.CreateViewRequest;
import com.onyx.kreader.ui.ReaderActivity;

/**
 * Created by Joy on 2016/5/24.
 */
public class ScreenOrientationChangedAction extends BaseAction {
    @Override
    public void execute(final ReaderActivity readerActivity) {
        BaseRequest config = new CreateViewRequest(readerActivity.getDisplayWidth(), readerActivity.getDisplayHeight());
        readerActivity.getReader().submitRequest(readerActivity, config, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Exception e) {
                if (e != null) {
                    return;
                }
                readerActivity.redrawPage();
            }
        });
    }
}
