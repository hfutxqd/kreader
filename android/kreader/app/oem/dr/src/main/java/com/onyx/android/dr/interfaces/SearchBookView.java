package com.onyx.android.dr.interfaces;


import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.model.Metadata;

import java.util.List;

/**
 * Created by hehai on 2016/12/16.
 */
public interface SearchBookView {
    void setResult(QueryResult<Metadata> result);

    void setHint(List<String> books);

    void setHistory(List<String> books);
}