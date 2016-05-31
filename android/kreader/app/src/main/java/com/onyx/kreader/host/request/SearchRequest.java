package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.host.impl.ReaderSearchOptionsImpl;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 2/15/16.
 */
public class SearchRequest extends BaseRequest {

    private ReaderSearchOptionsImpl searchOptions;
    private boolean searchForward;

    public SearchRequest(final String fromPage, final String text,  boolean caseSensitive, boolean match, boolean forward) {
        searchOptions = new ReaderSearchOptionsImpl(fromPage, text, caseSensitive, match);
        searchForward = forward;
    }

    // in document coordinates system. forward to layout manager to scale
    public void execute(final Reader reader) throws Exception {
        createReaderViewInfo();
        if (searchForward) {
            reader.getSearchManager().searchNext(searchOptions);
        } else {
            reader.getSearchManager().searchPrevious(searchOptions);
        }
        if (reader.getSearchManager().searchResults().size() > 0) {
            getReaderViewInfo().saveSearchResults(reader.getSearchManager().searchResults());
        }
    }
}
