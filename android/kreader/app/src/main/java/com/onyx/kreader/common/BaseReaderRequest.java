package com.onyx.kreader.common;

import android.util.Log;
import com.onyx.kreader.dataprovider.DocumentOptionsProvider;
import com.onyx.kreader.host.impl.ReaderBitmapImpl;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/4/15.
 */
public abstract class BaseReaderRequest extends BaseRequest {

    private static final String TAG = BaseReaderRequest.class.getSimpleName();
    private volatile boolean saveOptions = false;
    private volatile boolean loadShapeData = true;
    private volatile boolean loadAnnotationData = false;
    private volatile boolean loadBookmarkData = false;

    private ReaderBitmapImpl renderBitmap;
    private ReaderViewInfo readerViewInfo;
    private ReaderUserDataInfo readerUserDataInfo;
    private volatile boolean transferBitmap = true;

    public BaseReaderRequest() {
    }

    public boolean isTransferBitmap() {
        return transferBitmap;
    }

    public void setTransferBitmap(boolean sync) {
        transferBitmap = sync;
    }

    public void setSaveOptions(boolean save) {
        saveOptions = save;
    }

    public boolean isSaveOptions() {
        return saveOptions;
    }

    public boolean isLoadShapeData() {
        return loadShapeData;
    }

    public void setLoadShapeData(boolean load) {
        loadShapeData = load;
    }

    public boolean isLoadAnnotationData() {
        return loadAnnotationData;
    }

    public void setLoadAnnotationData(boolean load) {
        loadAnnotationData = load;
    }

    public boolean isLoadBookmarkData() {
        return loadBookmarkData;
    }

    public void setLoadBookmarkData(boolean load) {
        loadBookmarkData = load;
    }

    public ReaderBitmapImpl getRenderBitmap() {
        return renderBitmap;
    }

    public void useRenderBitmap(final Reader reader) {
        renderBitmap = reader.getReaderHelper().getRenderBitmap();
    }

    public void beforeExecute(final Reader reader) {
        reader.acquireWakeLock(getContext());
        benchmarkStart();
        if (isAbort()) {
            reader.getReaderHelper().setAbortFlag();
        }
        if (getCallback() == null) {
            return;
        }
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                getCallback().start(BaseReaderRequest.this);
            }
        };
        if (isRunInBackground()) {
            reader.getLooperHandler().post(runnable);
        } else {
            runnable.run();
        }
    }

    public abstract void execute(final Reader reader) throws Exception;

    /**
     * must not throw out exception from the method
     *
     * @param reader
     */
    public void afterExecute(final Reader reader) {
        try {
            afterExecuteImpl(reader);
        } catch (Throwable tr) {
            Log.w(TAG, tr);
        }
    }

    private void afterExecuteImpl(final Reader reader) throws Throwable {
        dumpException();
        benchmarkEnd();
        reader.getReaderHelper().clearAbortFlag();
        saveReaderOptions(reader);
        loadUserData(reader);
        copyBitmapToViewport(reader);
    }

    private void dumpException() {
        if (hasException()) {
            Log.w(TAG, getException());
        }
    }

    private void copyBitmapToViewport(final Reader reader) {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (isTransferBitmap()) {
                        reader.getBitmapCopyCoordinator().copyRenderBitmapToViewport();
                    }
                    if (getCallback() != null) {
                        // we can't foresee what's will be in done(), so we protect it with catch clause
                        getCallback().done(BaseReaderRequest.this, getException());
                    }
                    reader.releaseWakeLock();
                } catch (Exception e) {
                }
        }};

        if (isRunInBackground()) {
            reader.getLooperHandler().post(runnable);
        } else {
            runnable.run();
        }
        reader.getBitmapCopyCoordinator().waitCopy();
    }

    public final ReaderViewInfo getReaderViewInfo() {
        return readerViewInfo;
    }

    public final ReaderUserDataInfo getReaderUserDataInfo() {
        if (readerUserDataInfo == null) {
            readerUserDataInfo = new ReaderUserDataInfo();
        }
        return readerUserDataInfo;
    }

    public ReaderViewInfo createReaderViewInfo() {
        readerViewInfo = new ReaderViewInfo();
        return readerViewInfo;
    }

    private void saveReaderOptions(final Reader reader) {
        if (hasException() || !isSaveOptions()) {
            return;
        }

        reader.saveOptions();
        DocumentOptionsProvider.saveDocumentOptions(getContext(),
                reader.getDocumentPath(),
                reader.getDocumentMd5(),
                reader.getDocumentOptions());
    }

    private void loadUserData(final Reader reader) {
        if (isLoadAnnotationData()) {
            getReaderUserDataInfo().loadAnnotations(getContext(), reader, readerViewInfo.getVisiblePages());
        }
        if (isLoadBookmarkData()) {
            getReaderUserDataInfo().loadBookmarks(getContext(), reader, readerViewInfo.getVisiblePages());
        }
    }
}
