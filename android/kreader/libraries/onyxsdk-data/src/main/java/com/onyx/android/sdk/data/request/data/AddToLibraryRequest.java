package com.onyx.android.sdk.data.request.data;

import com.onyx.android.sdk.data.DataCacheManager;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.utils.DataProviderUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2016/9/8.
 */
public class AddToLibraryRequest extends BaseDataRequest {
    private Library library;
    private List<Metadata> addList = new ArrayList<>();

    public AddToLibraryRequest(Library library, List<Metadata> addList) {
        this.library = library;
        this.addList.addAll(addList);
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        DataProviderUtils.addCollections(getContext(), getDataProviderBase(dataManager), library, addList);
        DataCacheManager cacheManager = dataManager.getDataCacheManager();
        cacheManager.removeAll(library.getParentUniqueId(), addList);
        cacheManager.addAll(library.getIdString(), addList);
    }
}
