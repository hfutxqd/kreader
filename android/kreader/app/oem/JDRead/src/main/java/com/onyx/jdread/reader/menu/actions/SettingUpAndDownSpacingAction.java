package com.onyx.jdread.reader.menu.actions;

import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.actions.BaseReaderAction;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.ReaderActivityEventHandler;
import com.onyx.jdread.reader.menu.event.ReaderErrorEvent;
import com.onyx.jdread.reader.menu.request.SettingTextStyleRequest;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class SettingUpAndDownSpacingAction extends BaseReaderAction {
    private int margin;
    private ReaderTextStyle style;

    public SettingUpAndDownSpacingAction(ReaderTextStyle style, int margin) {
        this.margin = margin;
        this.style = style;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, RxCallback baseCallback) {
        ReaderTextStyle.PageMargin pageMargin = style.getPageMargin();

        ReaderTextStyle.Percentage topMargin = pageMargin.getTopMargin();
        topMargin.setPercent(margin);
        ReaderTextStyle.Percentage bottomMargin = pageMargin.getBottomMargin();
        bottomMargin.setPercent(margin);
        final SettingTextStyleRequest request = new SettingTextStyleRequest(readerDataHolder.getReader(), style);
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                ReaderActivityEventHandler.updateReaderViewInfo(readerDataHolder, request);
            }

            @Override
            public void onError(Throwable throwable) {
                ReaderErrorEvent.onErrorHandle(throwable,this.getClass().getSimpleName(),readerDataHolder.getEventBus());
            }
        });
    }
}