package com.onyx.kreader.host.request;

import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.common.BaseRequest;

/**
 * Created by zhuzeng on 10/6/15.
 */
public class ScaleToPageRequest extends BaseRequest {

    private String pageName;

    public ScaleToPageRequest(final String name) {
        pageName = name;
    }

    // in document coordinates system. forward to layout manager to scale
    public void execute(final Reader reader) throws Exception {
        useRenderBitmap(reader);
        reader.getReaderLayoutManager().scaleToPage(pageName);
        reader.getReaderLayoutManager().drawVisiblePages(getRenderBitmap());
    }

}
