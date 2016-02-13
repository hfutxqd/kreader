package com.onyx.kreader.api;

import android.graphics.PointF;
import android.graphics.RectF;

/**
 * Created by zhuzeng on 10/3/15.
 */
public interface ReaderHitTestManager {

    /**
     * Select word by the point. The plugin should automatically extend the selection to word boundary.
     * @param hitTest the user input point in document coordinates system.
     * @param splitter the text splitter.
     * @return the selection.
     */
    public ReaderSelection selectWord(final ReaderHitTestArgs hitTest, final ReaderTextSplitter splitter);

    /**
     * Get document position for specified point.
     * @param hitTest the hit test args.
     * @return
     */
    public String position(final ReaderHitTestArgs hitTest);

    /**
     * Select text between start point and end point.
     * @param start The start view point.
     * @param start The end view point.
     * @return the selection.
     */
    public ReaderSelection select(final ReaderHitTestArgs start, final ReaderHitTestArgs end);


}
