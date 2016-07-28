package com.onyx.kreader.ui.actions;

import com.onyx.kreader.host.request.ForwardRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by zhuzeng on 6/2/16.
 */
public class ForwardAction extends BaseAction {

    public void execute(final ReaderDataHolder readerDataHolder) {
        if (!readerDataHolder.getReaderViewInfo().canGoForward) {
            return;
        }

        final ForwardRequest forwardRequest = new ForwardRequest();
        readerDataHolder.submitRequest(forwardRequest);
    }

}
