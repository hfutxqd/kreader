package com.onyx.edu.reader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.request.cloud.SaveDocumentDataToCloudRequest;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

/**
 * Created by ming on 2017/6/10.
 */

public class SaveDocumentDataToCloudAction extends BaseAction {

    private StringBuffer exportDBPath;
    private StringBuffer fileFullMd5;
    private String cloudDocId;
    private StringBuffer token;

    private String errorMessage;

    public SaveDocumentDataToCloudAction(StringBuffer exportDBPath, StringBuffer fileFullMd5, String cloudDocId, StringBuffer token) {
        this.exportDBPath = exportDBPath;
        this.fileFullMd5 = fileFullMd5;
        this.cloudDocId = cloudDocId;
        this.token = token;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {
        final SaveDocumentDataToCloudRequest saveDocumentDataToCloudRequest = new SaveDocumentDataToCloudRequest(exportDBPath.toString(),
                readerDataHolder.getContext(),
                Constant.SYNC_API_BASE,
                fileFullMd5.toString(),
                cloudDocId,
                token.toString());
        readerDataHolder.getCloudManager().submitRequest(readerDataHolder.getContext(), saveDocumentDataToCloudRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                errorMessage = saveDocumentDataToCloudRequest.getErrorMessage();
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
