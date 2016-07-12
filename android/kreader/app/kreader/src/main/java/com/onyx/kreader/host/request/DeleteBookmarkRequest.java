package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.dataprovider.Bookmark;
import com.onyx.kreader.dataprovider.BookmarkProvider;
import com.onyx.kreader.host.layout.LayoutProviderUtils;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/15/15.
 */
public class DeleteBookmarkRequest extends BaseReaderRequest {

    private Bookmark bookmark;

    public DeleteBookmarkRequest(Bookmark bookmark) {
        this.bookmark = bookmark;
    }

    public void execute(final Reader reader) throws Exception {
        if (bookmark != null) {
            BookmarkProvider.deleteBookmark(bookmark);
        }
        LayoutProviderUtils.updateReaderViewInfo(createReaderViewInfo(), reader.getReaderLayoutManager());
    }
}
