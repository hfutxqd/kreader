package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.reader.common.ReaderViewInfo;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.OpenDocumentSuccessEvent;
import com.onyx.jdread.reader.event.ReaderActivityEventHandler;
import com.onyx.jdread.reader.menu.event.ReaderErrorEvent;
import com.onyx.jdread.reader.menu.request.GetViewSettingRequest;

/**
 * Created by huxiaomao on 2018/1/6.
 */

public class GetViewSettingAction extends BaseReaderAction {
    private ReaderViewInfo readerViewInfo;

    public GetViewSettingAction(ReaderViewInfo readerViewInfo) {
        this.readerViewInfo = readerViewInfo;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, RxCallback baseCallback) {
        final GetViewSettingRequest request = new GetViewSettingRequest(readerViewInfo, readerDataHolder.getReader());
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                notifySaveViewSetting(readerDataHolder, request);
            }

            @Override
            public void onError(Throwable throwable) {
                ReaderErrorEvent.onErrorHandle(throwable, this.getClass().getSimpleName(), readerDataHolder.getEventBus());
            }
        });
    }

    public void notifySaveViewSetting(ReaderDataHolder readerDataHolder, GetViewSettingRequest request) {
        OpenDocumentSuccessEvent event = new OpenDocumentSuccessEvent();
        readerDataHolder.getEventBus().post(event);
        ReaderActivityEventHandler.updateReaderViewInfo(readerDataHolder, request);
    }
}