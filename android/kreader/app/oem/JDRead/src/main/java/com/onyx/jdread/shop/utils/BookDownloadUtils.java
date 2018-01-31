package com.onyx.jdread.shop.utils;

import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.CommonUtils;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.shop.action.DownLoadWholeBookAction;
import com.onyx.jdread.shop.action.DownloadAction;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookExtraInfoBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.DownLoadWholeBookResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.model.ShopDataBundle;

import java.io.File;

import static com.liulishuo.filedownloader.util.FileDownloadHelper.getAppContext;

/**
 * Created by hehai on 17-4-27.
 */

public class BookDownloadUtils {
    private static final String TAG = BookDownloadUtils.class.getSimpleName();

    public static void download(final BookDetailResultBean.DetailBean bookDetailBean, final ShopDataBundle dataBundle) {
        bookDetailBean.bookExtraInfoBean.isWholeBookDownLoad = true;
        if (StringUtils.isNullOrEmpty(bookDetailBean.downLoadUrl)) {
            int downLoadType = CloudApiContext.BookDownLoad.TYPE_ORDER;
            if (!bookDetailBean.can_buy) {
                downLoadType = CloudApiContext.BookDownLoad.TYPE_ORDER;
            } else if (bookDetailBean.can_read) {
                downLoadType = CloudApiContext.BookDownLoad.TYPE_SMOOTH_READ;
            }
            DownLoadWholeBookAction action = new DownLoadWholeBookAction(bookDetailBean.ebook_id, downLoadType);
            action.execute(dataBundle, new RxCallback<DownLoadWholeBookAction>() {
                @Override
                public void onNext(DownLoadWholeBookAction action) {
                    DownLoadWholeBookResultBean resultBean = action.getResultBean();
                    if (resultBean.result_code == Constants.RESULT_CODE_SUCCESS) {
                        DownLoadWholeBookResultBean.DataBean data = resultBean.data;
                        bookDetailBean.key = data.key;
                        bookDetailBean.random = data.random;
                        bookDetailBean.downLoadUrl = data.content_url;
                        downloadBook(dataBundle, bookDetailBean);
                    } else {
                        ToastUtil.showToastErrorMsgForDownBook(resultBean.result_code);
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    super.onError(throwable);
                }
            });
        } else {
            BookExtraInfoBean bookExtraInfoBean = bookDetailBean.bookExtraInfoBean;
            if (bookExtraInfoBean != null && DownLoadHelper.isDownloading(bookExtraInfoBean.downLoadState)) {
                OnyxDownloadManager.getInstance().pauseTask(bookExtraInfoBean.downLoadTaskTag, false);
            } else {
                downloadBook(dataBundle, bookDetailBean);
            }
        }
    }

    private static void downloadBook(ShopDataBundle dataBundle, BookDetailResultBean.DetailBean bookDetailBean) {
        if (StringUtils.isNullOrEmpty(bookDetailBean.downLoadUrl)) {
            ToastUtil.showToast(ResManager.getString(R.string.empty_url));
            return;
        }
        String bookName = bookDetailBean.downLoadUrl.substring(bookDetailBean.downLoadUrl.lastIndexOf("/") + 1);
        String localPath = CommonUtils.getJDBooksPath() + File.separator + bookName;
        DownloadAction downloadAction = new DownloadAction(getAppContext(), bookDetailBean.downLoadUrl, localPath, bookName);
        downloadAction.setBookDetailBean(bookDetailBean);
        downloadAction.execute(dataBundle, new RxCallback() {
            @Override
            public void onNext(Object o) {

            }
        });
    }
}