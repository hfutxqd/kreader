package com.onyx.kreader.common;

import android.graphics.RectF;
import com.onyx.kreader.api.ReaderSelection;
import com.onyx.kreader.host.math.PageInfo;
import com.onyx.kreader.host.options.ReaderConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zengzhu on 2/22/16.
 */
public class ReaderViewInfo {

    private final static String SEARCH_TAG = "search";

    private List<PageInfo> visiblePages = new ArrayList<PageInfo>();
    private Map<String, List<ReaderSelection>> selectionMap = new HashMap<String, List<ReaderSelection>>();
    public boolean canGoBack;
    public boolean canGoForward;
    public boolean supportReflow;
    public boolean supportScalable;
    public int scale;
    public RectF viewportInDoc = new RectF();
    public RectF pagesBoundingRect = new RectF();

    public final List<PageInfo> getVisiblePages() {
        return visiblePages;
    }

    public final PageInfo getFirstVisiblePage() {
        if (visiblePages.size() <= 0) {
            return null;
        }
        return visiblePages.get(0);
    }

    public final PageInfo getPageInfo(final String pageName) {
        for(PageInfo pageInfo : getVisiblePages()) {
            if (pageInfo.getName().equals(pageName)) {
                return pageInfo;
            }
        }
        return null;
    }

    public void copyPageInfo(final PageInfo pageInfo) {
        PageInfo copy = new PageInfo(pageInfo);
        visiblePages.add(copy);
    }

    public List<ReaderSelection> getSearchResults() {
        return selectionMap.get(SEARCH_TAG);
    }

    public void saveSearchResults(List<ReaderSelection> list) {
        selectionMap.put(SEARCH_TAG, list);
    }

    public boolean canPan() {
        if (isSpecialScale()) {
            return false;
        }
        return pagesBoundingRect.width() > viewportInDoc.width() || pagesBoundingRect.height() > viewportInDoc.height();
    }

    public boolean isSpecialScale() {
        return ReaderConstants.isSpecialScale(scale);
    }
}
