package com.onyx.kreader.plugins.adobe;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import com.onyx.kreader.api.*;
import com.onyx.kreader.utils.JniUtils;
import com.onyx.kreader.utils.PagePositionUtils;
import com.onyx.kreader.utils.StringUtils;

import java.util.List;

/**
 * Created by zhuzeng on 10/5/15.
 */
public class AdobeReaderPlugin implements ReaderPlugin,
        ReaderDocument,
        ReaderView,
        ReaderRenderer,
        ReaderNavigator,
        ReaderSearchManager,
        ReaderTextStyleManager,
        ReaderDrmManager,
        ReaderHitTestManager,
        ReaderRendererFeatures
{

    private AdobeJniWrapper impl;
    private String documentPath;

    public AdobeReaderPlugin(final Context context, final ReaderPluginOptions pluginOptions) {
        ReaderDeviceInfo.init(context);
    }

    public AdobeJniWrapper getPluginImpl() {
        if (impl == null) {
            impl = new AdobeJniWrapper();
        }
        return impl;
    }

    public String displayName() {
        return AdobeReaderPlugin.class.getSimpleName();
    }

    static public boolean accept(final String path) {
        String string = path.toLowerCase();
        if (string.endsWith(".epub") || string.endsWith(".pdf")) {
            return true;
        }
        return false;
    }

    public ReaderDocument open(final String path, final ReaderDocumentOptions documentOptions, final ReaderPluginOptions pluginOptions) throws ReaderException {
        String docPassword = "";
        String archivePassword = "";
        documentPath = path;
        if (documentOptions != null) {
            docPassword = documentOptions.getDocumentPassword();
            archivePassword = documentOptions.getDocumentPassword();
        }
        long ret = getPluginImpl().openFile(path, docPassword, archivePassword);
        if (ret == 0) {
            return this;
        }
        return null;
    }

    public boolean supportDrm() {
        return true;
    }

    public ReaderDrmManager createDrmManager() {
        return this;
    }

    public void abortCurrentJob() {
        getPluginImpl().setAbortFlagNative(true);
    }

    public void clearAbortFlag() {
        getPluginImpl().setAbortFlagNative(false);
    }

    public boolean readMetadata(final ReaderDocumentMetadata metadata) {
        return false;
    }

    public boolean readCover(final ReaderBitmap bitmap) {
        return false;
    }

    public RectF getPageOriginSize(final String position) {
        float size [] = {0, 0};
        getPluginImpl().pageSizeNative(PagePositionUtils.getPageNumber(position), size);
        return new RectF(0, 0, size[0], size[1]);
    }

    public boolean readTableOfContent(final ReaderDocumentTableOfContent toc) {
        return false;
    }

    public ReaderView getView(final ReaderViewOptions viewOptions) {
        return this;
    }

    public ReaderRendererFeatures getRendererFeatures() {
        return this;
    }

    public void close() {
        getPluginImpl().closeFile();
    }

    public ReaderViewOptions getViewOptions() {
        return null;
    }

    /**
     * Retrieve renderer.
     * @return the renderer.
     */
    public ReaderRenderer getRenderer() {
        return this;
    }

    /**
     * Retrieve the navigator.
     * @return
     */
    public ReaderNavigator getNavigator() {
        return this;
    }

    /**
     * Retrieve text style interface.
     */
    public ReaderTextStyleManager getTextStyleManager() {
        return this;
    }

    /**
     * Retrieve reader hit test.
     */
    public ReaderHitTestManager getReaderHitTestManager() {
        return this;
    }

    /**
     * Retrieve current visible links.
     * @return
     */
    public List<ReaderLink> getLinks(final String position) {
        return null;
    }

    /**
     * Retrieve search interface.
     * @return
     */
    public ReaderSearchManager getSearchManager() {
        return this;
    }

    public boolean clear(final ReaderBitmap bitmap) {
        return getPluginImpl().clear(bitmap.getBitmap());
    }

    public boolean draw(final String page, final float scale, final int rotation, final ReaderBitmap bitmap) {
        return getPluginImpl().drawVisiblePages(bitmap.getBitmap(), 0, 0, bitmap.getBitmap().getWidth(), bitmap.getBitmap().getHeight(), true);
    }

    public boolean draw(final String page, final float scale, final int rotation, final ReaderBitmap bitmap, int xInBitmap, int yInBitmap, int widthInBitmap, int heightInBitmp) {
        return getPluginImpl().drawVisiblePages(bitmap.getBitmap(), xInBitmap, yInBitmap, widthInBitmap, heightInBitmp,  false);
    }

    /**
     * Retrieve the default init position.
     * @return
     */
    public String getInitPosition() {
        return PagePositionUtils.fromPageNumber(0);
    }


    /**
     * Get position from page number
     * @param pageNumber The 0 based page number.
     * @return
     */
    public String getPositionByPageNumber(int pageNumber) {
        return PagePositionUtils.fromPageNumber(pageNumber);
    }

    public String getPositionByPageName(final String pageName) {
        return pageName;
    }

    /**
     * Return total page number.
     * @return 1 based total page number.
     */
    public int getTotalPage() {
        return getPluginImpl().countPagesInternal();
    }

    /**
     * Navigate to next screen.
     */
    public String nextScreen(final String position) {
        return null;
    }

    /**
     * Navigate to previous screen.
     */
    public String prevScreen(final String position) {
        return null;
    }

    /**
     * Navigate to next page.
     * @return
     */
    public String nextPage(final String position) {
        int pn = PagePositionUtils.getPageNumber(position);
        if (pn + 1 < getTotalPage()) {
            return PagePositionUtils.fromPageNumber(pn + 1);
        }
        return null;
    }

    /**
     * Navigate to previous page.
     * @return
     */
    public String prevPage(final String position) {
        int pn = PagePositionUtils.getPageNumber(position);
        if (pn > 0) {
            return PagePositionUtils.fromPageNumber(pn - 1);
        }
        return null;

    }

    /**
     * Navigate to first page.
     * @return
     */
    public String firstPage() {
        return null;
    }

    /**
     * Navigate to last page.
     * @return
     */
    public String lastPage() {
        return null;
    }

    /**
     * Navigate to specified position.
     * @return
     */
    public boolean gotoPosition(final String position) {
        return getPluginImpl().gotoLocationInternal(PagePositionUtils.getPageNumber(position), null);
    }

    public boolean searchPrevious(final ReaderSearchOptions options) {
        return false;
    }

    public boolean searchNext(final ReaderSearchOptions options) {
        return false;
    }

    public List<ReaderSelection> searchResults() {
        return null;
    }

    /**
     * Scale to page.
     */
    public void setScaleToPage() {}

    /**
     * Check if scale to page.
     * @return
     */
    public boolean isScaleToPage() {
        return false;
    }

    public void setScaleToWidth() {

    }

    public boolean isScaleToWidth() {
        return false;
    }

    public void setScaleToHeight() {}

    public boolean isScaleToHeight() {
        return false;
    }

    public boolean isCropPage() {
        return false;
    }

    public void setCropPage() {}

    public boolean isCropWidth() {
        return false;
    }

    public void setCropWidth() {}

    public float getActualScale() {
        return 0;
    }

    public void setActualScale(final float scale) {}

    /**
     * Set viewportInPage. The behavior is different on different page layout.
     * @param viewport
     */
    public boolean setViewport(final RectF viewport) {
        return false;
    }

    /**
     * Retrieve current viewportInPage.
     * @return the current viewportInPage.
     */
    public RectF getViewport() {
        return null;
    }


    /**
     * Convinent method to set scale and viewportInPage directly.
     * @param actualScale the actual scale
     * @return
     */
    public boolean setScale(float actualScale) {
        return getPluginImpl().changeNavigationMatrix(actualScale, 0, 0);
    }

    public boolean setViewport(final float x, final float y) {
        return getPluginImpl().setNavigationMatrix(0, x, y);
    }

    /**
     * Return the page display rect on view coordinates.
     * @param position the page position.
     * @return
     */
    public RectF getPageDisplayRect(final String position) {
        return null;
    }

    public boolean acceptDRMFile(final String path) {
        return false;
    }

    public boolean registerDRMCallback(final ReaderDRMCallback callback) {
        return false;
    }

    public boolean activateDeviceDRM(String user, String password) {
        return false;
    }

    public boolean deactivateDeviceDRM() {
        return false;
    }

    public String getDeviceDRMAccount() {
        return "";
    }
    public boolean fulfillDRMFile(String path) {
        return false;
    }

    public boolean supportSinglePageLayout() {
        return true;
    }

    public void setSinglePageLayout() {

    }

    public boolean isSinglePageLayout() {
        return false;
    }

    public boolean supportContinuousPageLayout() {
        return true;
    }

    public void setContinuousPageLayout() {
    }

    public boolean isContinuousPageLayout() {
        return false;
    }

    public boolean supportReflowLayout() {
        return true;
    }
    public void setReflowLayout() {

    }

    public boolean isReflowLayout() {
        return false;
    }

    public boolean viewToDoc(final PointF viewPoint, final PointF documentPoint) {
        return false;
    }


    public ReaderSelection selectWord(final ReaderHitTestArgs args, final ReaderTextSplitter splitter) {

        return null;
    }

    public String position(final ReaderHitTestArgs args) {
        final String position = getPluginImpl().locationNative(args.point.x, args.point.y);
        return position;
    }

    public ReaderSelection select(final ReaderHitTestArgs startArgs, final ReaderHitTestArgs endArgs) {
        final String start = getPluginImpl().locationNative(startArgs.point.x, startArgs.point.y);
        final String end = getPluginImpl().locationNative(endArgs.point.x, endArgs.point.y);
        AdobeSelectionImpl selection = new AdobeSelectionImpl();
        selection.setStartPosition(start);
        selection.setEndPosition(end);
        selection.setText(getPluginImpl().getTextNative(start, end));
        selection.setRectangles(JniUtils.rectangles(getPluginImpl().rectangles(start, end)));
        return selection;
    }

    public boolean supportScale() {
        if (StringUtils.isNullOrEmpty(documentPath)) {
            return false;
        }
        return documentPath.toLowerCase().endsWith("pdf");
    }

    public boolean supportFontSizeAdjustment() {
        return true;
    }

    public boolean supportTypefaceAdjustment() {
        if (StringUtils.isNullOrEmpty(documentPath)) {
            return false;
        }
        return documentPath.toLowerCase().endsWith("epub");
    }


}
