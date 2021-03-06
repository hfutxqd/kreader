package com.onyx.android.sdk.data.request.data;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.Library;

/**
 * Created by suicheng on 2016/9/9.
 */
public class DeleteLibraryRequest extends BaseDataRequest {

    private Library library;

    public DeleteLibraryRequest(Library library) {
        this.library = library;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        dataManager.deleteLibrary(getContext(), library);
    }
}
