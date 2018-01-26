package com.onyx.jdread.reader.data;

import android.content.Context;

import com.onyx.jdread.reader.common.DocumentInfo;
import com.onyx.jdread.reader.highlight.ReaderSelectionHelper;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 2017/12/20.
 */

public class Reader {
    private ReaderHelper readerHelper;
    private DocumentInfo documentInfo;
    private ReaderViewHelper readerViewHelper;
    private ReaderSelectionHelper readerSelectionHelper;

    public Reader(DocumentInfo documentInfo,Context context) {
        this.documentInfo = documentInfo;
        this.readerHelper = new ReaderHelper(context);
        this.readerViewHelper = new ReaderViewHelper(context);
    }

    public ReaderHelper getReaderHelper() {
        return readerHelper;
    }

    public DocumentInfo getDocumentInfo() {
        return documentInfo;
    }

    public ReaderViewHelper getReaderViewHelper() {
        return readerViewHelper;
    }

    public ReaderSelectionHelper getReaderSelectionHelper() {
        if (readerSelectionHelper == null) {
            readerSelectionHelper = new ReaderSelectionHelper();
        }
        return readerSelectionHelper;
    }
}
