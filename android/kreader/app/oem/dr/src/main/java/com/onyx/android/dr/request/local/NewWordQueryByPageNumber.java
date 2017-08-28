package com.onyx.android.dr.request.local;

import com.onyx.android.dr.data.database.NewWordNoteBookEntity;
import com.onyx.android.dr.data.database.NewWordNoteBookEntity_Table;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/6.
 */
public class NewWordQueryByPageNumber extends BaseDataRequest {
    private List<NewWordNoteBookEntity> newWordList;
    private String pageNumber;

    public NewWordQueryByPageNumber(String pageNumber) {
        this.pageNumber = pageNumber;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        super.execute(dataManager);
        queryNewWordList();
    }

    public List<NewWordNoteBookEntity> getNewWordList() {
        return newWordList;
    }

    public void setNewWordList(List<NewWordNoteBookEntity> newWordList) {
        this.newWordList = newWordList;
    }

    public void queryNewWordList() {
        List<NewWordNoteBookEntity> newWordList = new Select().from(NewWordNoteBookEntity.class).
                where(NewWordNoteBookEntity_Table.pageNumber.eq(pageNumber)).orderBy(NewWordNoteBookEntity_Table.currentTime, false).queryList();
        if (newWordList != null && newWordList.size() > 0) {
            setNewWordList(newWordList);
        }
    }
}