package com.onyx.kreader.ui.data;

import android.content.Context;
import android.graphics.Rect;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.scribble.request.ShapeDataInfo;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.common.ReaderUserDataInfo;
import com.onyx.kreader.common.ReaderViewInfo;
import com.onyx.kreader.host.math.PageUtils;
import com.onyx.kreader.host.request.CloseRequest;
import com.onyx.kreader.host.request.PreRenderRequest;
import com.onyx.kreader.host.request.RenderRequest;
import com.onyx.kreader.host.request.SaveDocumentOptionsRequest;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.host.wrapper.ReaderManager;
import com.onyx.kreader.tts.ReaderTtsManager;
import com.onyx.kreader.ui.actions.ShowReaderMenuAction;
import com.onyx.kreader.ui.events.ChangeEpdUpdateMode;
import com.onyx.kreader.ui.events.DocumentOpenEvent;
import com.onyx.kreader.ui.events.RequestFinishEvent;
import com.onyx.kreader.ui.events.ResetEpdUpdateMode;
import com.onyx.kreader.ui.events.ShowReaderSettingsEvent;
import com.onyx.kreader.ui.handler.HandlerManager;
import com.onyx.kreader.ui.highlight.ReaderSelectionManager;
import com.onyx.kreader.utils.PagePositionUtils;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by ming on 16/7/27.
 */
public class ReaderDataHolder {
    private static final String TAG = ReaderDataHolder.class.getSimpleName();

    private Context context;
    private String documentPath;
    private Reader reader;
    private ReaderViewInfo readerViewInfo;
    private ReaderUserDataInfo readerUserDataInfo;
    private ShapeDataInfo shapeDataInfo;
    private boolean preRender = true;
    private boolean preRenderNext = true;
    private boolean documentOpened = false;

    private int displayWidth;
    private int displayHeight;

    private HandlerManager handlerManager;
    private ReaderSelectionManager selectionManager;
    private ReaderTtsManager ttsManager;
    private NoteViewHelper noteViewHelper;
    private EventBus eventBus = new EventBus();

    private int optionsSkippedTimes = 0;

    public ReaderDataHolder(Context context){
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void saveReaderViewInfo(final BaseReaderRequest request) {
        readerViewInfo = request.getReaderViewInfo();
    }

    public void saveReaderUserDataInfo(final BaseReaderRequest request) {
        readerUserDataInfo = request.getReaderUserDataInfo();
    }

    public void saveShapeDataInfo(final BaseNoteRequest request) {
        shapeDataInfo = request.getShapeDataInfo();
    }

    public boolean hasShapes() {
        if (shapeDataInfo == null) {
            return false;
        }
        return shapeDataInfo.hasShapes();
    }

    public void resetShapeData() {
        shapeDataInfo = null;
    }

    public final ReaderViewInfo getReaderViewInfo() {
        return readerViewInfo;
    }

    public final ReaderUserDataInfo getReaderUserDataInfo() {
        return readerUserDataInfo;
    }

    public final ShapeDataInfo getShapeDataInfo() {
        return shapeDataInfo;
    }

    public void setPreRenderNext(boolean preRenderNext) {
        this.preRenderNext = preRenderNext;
    }

    public Reader getReader() {
        return reader;
    }

    public void initReaderFromPath(final String path) {
        documentOpened = false;
        documentPath = path;
        reader = ReaderManager.getReader(documentPath);
    }

    public void onDocumentOpened() {
        documentOpened = true;
        getEventBus().post(new DocumentOpenEvent(documentPath));
    }

    public boolean isDocumentOpened() {
        return documentOpened;
    }

    public String getCurrentPageName() {
        return getReaderViewInfo().getFirstVisiblePage().getName();
    }

    public int getCurrentPage() {
        return PagePositionUtils.getPosition(getCurrentPageName());
    }

    public int getPageCount() {
        return reader.getNavigator().getTotalPage();
    }

    public final PageInfo getFirstPageInfo() {
        return getReaderViewInfo().getFirstVisiblePage();
    }

    public int getDisplayWidth() {
        return displayWidth;
    }

    public int getDisplayHeight() {
        return displayHeight;
    }

    public Rect getDisplayRect() {
        return new Rect(0, 0, getDisplayWidth(), getDisplayHeight());
    }

    public void setDisplaySize(int width, int height) {
        displayWidth = width;
        displayHeight = height;
    }

    public boolean canCurrentPageScaleDown() {
        final float toPageScale = PageUtils.scaleToPage(getFirstPageInfo().getOriginWidth(),
                getFirstPageInfo().getOriginHeight(),
                getReader().getViewOptions().getViewWidth(),
                getReader().getViewOptions().getViewHeight());
        return getReaderViewInfo().getFirstVisiblePage().getActualScale() > toPageScale;
    }

    public boolean canCurrentPageScaleUp() {
        return getReaderViewInfo().getFirstVisiblePage().getActualScale() < PageConstants.MAX_SCALE;
    }

    public final HandlerManager getHandlerManager() {
        if (handlerManager == null) {
            handlerManager = new HandlerManager(this);
        }
        return handlerManager;
    }

    public ReaderSelectionManager getSelectionManager() {
        if (selectionManager == null) {
            selectionManager = new ReaderSelectionManager();
        }
        return selectionManager;
    }

    public ReaderTtsManager getTtsManager() {
        if (ttsManager == null) {
            ttsManager = new ReaderTtsManager(context);
        }
        return ttsManager;
    }

    private void updateReaderMenuState() {
        if (ShowReaderMenuAction.isReaderMenuShown()) {
            new ShowReaderMenuAction().execute(ReaderDataHolder.this);
        }
    }

    public NoteViewHelper getNoteViewHelper() {
        if (noteViewHelper == null) {
            noteViewHelper = new NoteViewHelper();
        }
        return noteViewHelper;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public String getBookName() {
        return FileUtils.getFileName(getDocumentPath());
    }

    public boolean hasBookmark() {
        return getReaderUserDataInfo().hasBookmark(getFirstPageInfo());
    }

    public void submitNonRenderRequest(final BaseReaderRequest request) {
        submitNonRenderRequest(request, null);
    }

    public void submitNonRenderRequest(final BaseReaderRequest request, final BaseCallback callback) {
        beforeSubmitRequest();
        reader.submitRequest(context, request, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                callback.invoke(callback, request, e);
            }
        });
    }

    public void submitRenderRequest(final BaseReaderRequest renderRequest) {
        submitRenderRequest(renderRequest, null);
    }

    public void submitRenderRequest(final BaseReaderRequest renderRequest, final BaseCallback callback) {
        beforeSubmitRequest();
        reader.submitRequest(context, renderRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                onRenderRequestFinished(renderRequest, e);
                callback.invoke(callback, request, e);
                onPageDrawFinished(renderRequest, e);

                updateReaderMenuState();
            }
        });
    }

    private void beforeSubmitRequest() {
        resetShapeData();
    }

    private void onPageDrawFinished(BaseReaderRequest request, Throwable e) {
        if (e != null || request.isAbort()) {
            return;
        }
        saveDocumentOptions(request);
        preRenderNext();
    }

    private void saveDocumentOptions(final BaseReaderRequest request) {
        if (!request.isSaveOptions()) {
            return;
        }
        final int MAX_SKIP_TIMES = 5;
        if (optionsSkippedTimes < MAX_SKIP_TIMES) {
            optionsSkippedTimes++;
        } else {
            submitNonRenderRequest(new SaveDocumentOptionsRequest());
            optionsSkippedTimes = 0;
        }
    }

    public void preRenderNext() {
        if (!preRender) {
            return;
        }
        final PreRenderRequest request = new PreRenderRequest(preRenderNext);
        getReader().submitRequest(context, request, null);
    }

    public void onRenderRequestFinished(final BaseReaderRequest request, Throwable e) {
        onRenderRequestFinished(request, e, true);
    }

    public void changeEpdUpdateMode(final UpdateMode mode) {
        eventBus.post(new ChangeEpdUpdateMode(mode));
    }

    public void resetEpdUpdateMode() {
        eventBus.post(new ResetEpdUpdateMode());
    }

    public void onRenderRequestFinished(final BaseReaderRequest request, Throwable e, boolean applyGCIntervalUpdate) {
        if (e != null || request.isAbort()) {
            return;
        }
        saveReaderViewInfo(request);
        saveReaderUserDataInfo(request);
        eventBus.post(RequestFinishEvent.fromRequest(request, e, applyGCIntervalUpdate));
    }


    public void redrawPage(boolean render) {
        if (getReader() != null) {
            if (render){
                submitRenderRequest(new RenderRequest());
            }else {
                submitNonRenderRequest(new RenderRequest());
            }
        }
    }

    public void showReaderSettings() {
        eventBus.post(new ShowReaderSettingsEvent());
    }

    public void destroy() {
        closeDocument();
        closeTts();
    }

    private void closeDocument() {
        documentOpened = false;
        if (reader != null && reader.getDocument() != null) {
            CloseRequest closeRequest = new CloseRequest();
            submitNonRenderRequest(closeRequest);
        }
        ReaderManager.releaseReader(documentPath);
    }

    private void closeTts() {
        if (ttsManager != null) {
            ttsManager.shutdown();
        }
    }
}
