package com.onyx.kreader.host.layout;

import android.graphics.RectF;

import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.common.ReaderDrawContext;
import com.onyx.kreader.common.ReaderViewInfo;
import com.onyx.kreader.host.navigation.NavigationArgs;
import com.onyx.android.sdk.data.PageConstants;
import com.onyx.kreader.host.options.ReaderStyle;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.utils.PagePositionUtils;

/**
 * Created by zhuzeng on 10/7/15.
 * For reflow stream document.
 */
public class LayoutTextReflowProvider extends LayoutProvider {


    public LayoutTextReflowProvider(final ReaderLayoutManager lm) {
        super(lm);
    }

    public String getProviderName() {
        return PageConstants.TEXT_REFLOW_PAGE;
    }

    public void activate() {
        getPageManager().setPageRepeat(0);
        getPageManager().scaleToPage(null);
    }

    public boolean setNavigationArgs(final NavigationArgs args) throws ReaderException {
        return false;
    }

    @Override
    public boolean canPrevScreen() throws ReaderException {
        return !getLayoutManager().getNavigator().isFirstPage();
    }

    public boolean prevScreen() throws ReaderException {
        return prevPage();
    }

    @Override
    public boolean canNextScreen() throws ReaderException {
        return !getLayoutManager().getNavigator().isLastPage();
    }

    public boolean nextScreen() throws ReaderException {
        return nextPage();
    }

    public boolean prevPage() throws ReaderException {
        LayoutProviderUtils.prevPage(getLayoutManager());
        return gotoPosition(getLayoutManager().getNavigator().getCurrentPosition());
    }

    public boolean nextPage() throws ReaderException {
        LayoutProviderUtils.nextPage(getLayoutManager());
        return gotoPosition(getLayoutManager().getNavigator().getCurrentPosition());
    }

    public boolean firstPage() throws ReaderException {
        LayoutProviderUtils.firstPage(getLayoutManager());
        return gotoPosition(getLayoutManager().getNavigator().getCurrentPosition());
    }

    public boolean lastPage() throws ReaderException {
        LayoutProviderUtils.lastPage(getLayoutManager());
        return gotoPosition(getLayoutManager().getNavigator().getCurrentPosition());
    }

    public boolean drawVisiblePages(final Reader reader, final ReaderDrawContext drawContext, final ReaderViewInfo readerViewInfo) throws ReaderException {
        LayoutProviderUtils.drawVisiblePages(reader, getLayoutManager(), drawContext, readerViewInfo, false);
        return true;
    }

    public boolean setScale(float scale, float left, float top) throws ReaderException {
        return false;
    }

    public boolean changeScaleWithDelta(float delta) throws ReaderException {
        return false;
    }

    public boolean changeScaleByRect(final String position, final RectF rect) throws ReaderException  {
        return false;
    }

    public boolean gotoPosition(final String location) throws ReaderException {
        if (StringUtils.isNullOrEmpty(location)) {
            return false;
        }

        getLayoutManager().getNavigator().gotoPosition(location);

        String page = PagePositionUtils.fromPageNumber(getLayoutManager().getNavigator().getCurrentPageNumber());
        final RectF viewportBeforeChange = new RectF(getPageManager().getViewportRect());
        LayoutProviderUtils.addSinglePage(getLayoutManager(), page, location);
        if (!getPageManager().gotoPage(location)) {
            return false;
        }

        onPageChanged(viewportBeforeChange);
        return true;
    }

    public boolean pan(int dx, int dy) throws ReaderException {
        return false;
    }

    public boolean supportPreRender() throws ReaderException {
        return false;
    }

    @Override
    public boolean supportScale() throws ReaderException {
        return false;
    }

    public boolean supportSubScreenNavigation() {
        return false;
    }

    public boolean setStyle(final ReaderStyle style) throws ReaderException {
        return false;
    }

    public RectF getPageRectOnViewport(final String position) throws ReaderException {
        return null;
    }

    public float getActualScale() throws ReaderException {
        return 0.0f;
    }

    public RectF getPageBoundingRect() throws ReaderException {
        return getViewportRect();
    }

    public RectF getViewportRect() throws ReaderException {
        return new RectF(0, 0,
                getLayoutManager().getReaderViewOptions().getViewWidth(),
                getLayoutManager().getReaderViewOptions().getViewHeight());
    }

    public void scaleToPage() throws ReaderException {

    }

    public void scaleToWidth() throws ReaderException {

    }

    public void scaleByRect(final RectF child) throws ReaderException {

    }

    private void onPageChanged(final RectF viewportBeforeChange) {
        if (PageConstants.isSpecialScale(getLayoutManager().getSpecialScale())) {
            return;
        }
        getPageManager().setAbsoluteViewportPosition(viewportBeforeChange.left,
                getPageManager().getViewportRect().top);
    }

}
