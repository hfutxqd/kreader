package com.onyx.einfo.action;

import android.content.Context;

import com.onyx.einfo.R;
import com.onyx.einfo.holder.LibraryDataHolder;
import com.onyx.einfo.utils.StudentPreferenceManager;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.request.cloud.v2.CloudLibraryListLoadRequest;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.List;

/**
 * Created by suicheng on 2017/5/18.
 */

public class CloudLibraryListLoadAction extends BaseAction<LibraryDataHolder> {

    private String parentId;
    private List<Library> libraryList;

    public CloudLibraryListLoadAction() {
    }

    public CloudLibraryListLoadAction(String parentId) {
        this.parentId = parentId;
    }

    @Override
    public void execute(final LibraryDataHolder dataHolder, final BaseCallback baseCallback) {
        CloudManager cloudManager = dataHolder.getCloudManager();
        final CloudLibraryListLoadRequest loadRequest = new CloudLibraryListLoadRequest(parentId);
        cloudManager.submitRequest(dataHolder.getContext(), loadRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    ToastUtils.showToast(dataHolder.getContext(), R.string.online_library_load_error);
                    return;
                }
                libraryList = loadRequest.getLibraryList();
                saveLibraryParentId(dataHolder.getContext(), libraryList);
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

    private void saveLibraryParentId(Context context, List<Library> libraryList) {
        if (CollectionUtils.isNullOrEmpty(libraryList)) {
            return;
        }
        StudentPreferenceManager.saveLibraryParentId(context, libraryList.get(0).getParentUniqueId());
    }

    public List<Library> getLibraryList() {
        return libraryList;
    }
}