package com.onyx.android.dr.manager;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.bean.GoodSentenceBean;
import com.onyx.android.dr.bean.NewWordBean;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.data.GoodSentenceData;
import com.onyx.android.dr.data.NewWordData;
import com.onyx.android.dr.data.database.GoodSentenceNoteEntity;
import com.onyx.android.dr.data.database.NewWordNoteBookEntity;
import com.onyx.android.dr.request.local.GoodSentenceInsert;
import com.onyx.android.dr.request.local.GoodSentenceQueryByPageNumber;
import com.onyx.android.dr.request.local.GoodSentenceQueryByReadingMatter;
import com.onyx.android.dr.request.local.NewWordInsert;
import com.onyx.android.dr.request.local.NewWordQueryByPageNumber;
import com.onyx.android.dr.request.local.NewWordQueryByReadingMatter;
import com.onyx.android.dr.util.TimeUtils;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/8/10.
 */
public class OperatingDataManager {
    private static OperatingDataManager instance = null;
    private List<NewWordNoteBookEntity> newWordList = new ArrayList<NewWordNoteBookEntity>();
    private List<GoodSentenceNoteEntity> goodSentenceList = new ArrayList<GoodSentenceNoteEntity>();

    private OperatingDataManager() {
    }

    public static OperatingDataManager getInstance() {
        if (instance == null) {
            synchronized (OperatingDataManager.class) {
                if (instance == null) {
                    instance = new OperatingDataManager();
                }
            }
        }
        return instance;
    }

    public void insertGoodSentence(GoodSentenceBean goodSentenceBean) {
        GoodSentenceData goodSentenceData = new GoodSentenceData();
        GoodSentenceNoteEntity bean = new GoodSentenceNoteEntity();
        bean.currentTime = TimeUtils.getCurrentTimeMillis();
        bean.details = goodSentenceBean.getDetails();
        bean.readingMatter = goodSentenceBean.getReadingMatter();
        bean.pageNumber = goodSentenceBean.getPageNumber();
        bean.goodSentenceType = goodSentenceBean.getGoodSentenceType();
        GoodSentenceInsert req = new GoodSentenceInsert(bean);
        if (req.whetherInsert()) {
            CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance().getString(R.string.good_sentence_already_exist));
        } else {
            CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance().getString(R.string.already_add_good_sentence_notebook));
        }
        goodSentenceData.insertGoodSentence(DRApplication.getInstance(), req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }

    public void insertNewWord(NewWordBean newWordBean) {
        NewWordData newWordData = new NewWordData();
        NewWordNoteBookEntity bean = new NewWordNoteBookEntity();
        bean.currentTime = TimeUtils.getCurrentTimeMillis();
        bean.newWord = newWordBean.getNewWord();
        bean.dictionaryLookup = newWordBean.getDictionaryLookup();
        bean.readingMatter = newWordBean.getReadingMatter();
        bean.newWordType = newWordBean.getNewWordType();
        bean.pageNumber = newWordBean.getPageNumber();
        final NewWordInsert req = new NewWordInsert(bean);
        if (req.whetherInsert()) {
            CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance().getString(R.string.new_word_notebook_already_exist));
        } else {
            CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance().getString(R.string.already_add_new_word_notebook));
        }
        newWordData.insertNewWord(DRApplication.getInstance(), req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }

    public void getAllNewWordByReadingMatter(String readingMatter, final BaseCallback callback) {
        NewWordData newWordData = new NewWordData();
        final NewWordQueryByReadingMatter req = new NewWordQueryByReadingMatter(readingMatter);
        newWordData.getAllNewWordByReadingMatter(DRApplication.getInstance(), req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                newWordList = req.getNewWordList();
                invoke(callback, request, e);
            }
        });
    }

    public void getAllNewWordByPageNumber(String pageNumber, final BaseCallback callback) {
        NewWordData newWordData = new NewWordData();
        final NewWordQueryByPageNumber req = new NewWordQueryByPageNumber(pageNumber);
        newWordData.getAllNewWordByPageNumber(DRApplication.getInstance(), req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                newWordList = req.getNewWordList();
                invoke(callback, request, e);
            }
        });
    }

    public void getAllGoodSentenceByReadingMatter(String readingMatter, final BaseCallback callback) {
        GoodSentenceData goodSentenceData = new GoodSentenceData();
        final GoodSentenceQueryByReadingMatter req = new GoodSentenceQueryByReadingMatter(readingMatter);
        goodSentenceData.getGoodSentenceByReadingMatter(DRApplication.getInstance(), req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                goodSentenceList = req.getGoodSentenceList();
                invoke(callback, request, e);
            }
        });
    }

    public void getAllGoodSentenceByPageNumber(String pageNumber, final BaseCallback callback) {
        GoodSentenceData goodSentenceData = new GoodSentenceData();
        final GoodSentenceQueryByPageNumber req = new GoodSentenceQueryByPageNumber(pageNumber);
        goodSentenceData.getGoodSentenceByPageNumber(DRApplication.getInstance(), req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                goodSentenceList = req.getGoodSentenceList();
                invoke(callback, request, e);
            }
        });
    }

    public List<NewWordNoteBookEntity> getNewWordList() {
        return newWordList;
    }

    public List<GoodSentenceNoteEntity> getGoodSentenceList() {
        return goodSentenceList;
    }
}