package com.onyx.jdread.reader.request;

import android.graphics.RectF;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.dataprovider.ContentSdkDataUtils;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;
import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.highlight.SelectionInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by huxiaomao on 2018/1/20.
 */

public class AddAnnotationRequest extends ReaderBaseRequest {
    private Reader reader;
    private Map<String, SelectionInfo> readerSelectionInfos;

    public AddAnnotationRequest(Reader reader, Map<String, SelectionInfo> readerSelectionInfos) {
        this.reader = reader;
        this.readerSelectionInfos = readerSelectionInfos;
    }

    @Override
    public AddAnnotationRequest call() throws Exception {
        saveAnnotation();
        reader.getReaderViewHelper().updatePageView(reader, getReaderUserDataInfo(), getReaderViewInfo());
        updateSetting(reader);
        return this;
    }

    private void saveAnnotation() {
        for (SelectionInfo readerSelectionInfo : readerSelectionInfos.values()) {
            ReaderSelection selection = readerSelectionInfo.getCurrentSelection();
            Annotation annotation = createAnnotation(reader,readerSelectionInfo.pageInfo,
                    selection.getStartPosition(), selection.getEndPosition(),
                    selection.getRectangles(), selection.getText(), "");

            ContentSdkDataUtils.getDataProvider().addAnnotation(annotation);
        }
    }

    public static Annotation createAnnotation(Reader reader,PageInfo pageInfo, String locationBegin, String locationEnd,
                                        List<RectF> rects, String quote, String note) {
        Annotation annotation = new Annotation();
        annotation.setIdString(reader.getReaderHelper().getDocumentMd5());
        annotation.setApplication(reader.getReaderHelper().getPlugin().displayName());
        annotation.setPosition(pageInfo.getPosition());
        annotation.setPageNumber(PagePositionUtils.getPageNumber(pageInfo.getName()));
        annotation.setLocationBegin(locationBegin);
        annotation.setLocationEnd(locationEnd);
        annotation.setQuote(quote);
        annotation.setNote(note);
        annotation.setRectangles(rects);
        return annotation;
    }
}