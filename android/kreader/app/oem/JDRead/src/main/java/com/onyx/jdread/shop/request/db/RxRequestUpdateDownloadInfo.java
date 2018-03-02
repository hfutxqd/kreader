package com.onyx.jdread.shop.request.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.rxrequest.data.db.RxBaseDBRequest;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookExtraInfoBean;

/**
 * Created by jackdeng on 2017/12/21.
 */

public class RxRequestUpdateDownloadInfo extends RxBaseDBRequest {

    private String  localPath;
    private BookExtraInfoBean extraInfo;

    public RxRequestUpdateDownloadInfo(DataManager dm) {
        super(dm);
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public void setExtraInfo(BookExtraInfoBean extraInfo) {
        this.extraInfo = extraInfo;
    }

    @Override
    public Object call() throws Exception {
        updateInfo();
        return this;
    }

    public void updateInfo() {
        if (extraInfo != null) {
            Metadata findMeta = getDataProvider().findMetadataByIdString(getAppContext(), localPath);
            BookExtraInfoBean findExtraInfoBean = JSONObjectParseUtils.toBean(findMeta.getDownloadInfo(), BookExtraInfoBean.class);
            if (findMeta != null && findMeta.hasValidId() && findExtraInfoBean != null) {
                findExtraInfoBean.progress = extraInfo.progress;
                findExtraInfoBean.percentage = extraInfo.percentage;
                findExtraInfoBean.downLoadState = extraInfo.downLoadState;
                findMeta.setDownloadInfo(JSONObjectParseUtils.toJson(findExtraInfoBean));
                findMeta.setSize((long) extraInfo.totalSize);
                getDataProvider().updateMetadata(getAppContext(), findMeta);
            }
        }
    }
}
