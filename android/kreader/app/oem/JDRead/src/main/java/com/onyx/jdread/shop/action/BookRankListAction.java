package com.onyx.jdread.shop.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.shop.cloud.entity.BookRankListRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.RecommendListResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestBookRankList;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jackdeng on 2018/1/19.
 */

public class BookRankListAction extends BaseAction<ShopDataBundle> {

    private int rankId;
    private int currentPage;
    private RecommendListResultBean bookModelResultBean;

    public RecommendListResultBean getBookModelResultBean() {
        return bookModelResultBean;
    }

    public BookRankListAction(int rankId, int currentPage) {
        this.rankId = rankId;
        this.currentPage = currentPage;
    }

    @Override
    public void execute(final ShopDataBundle shopDataBundle, final RxCallback rxCallback) {
        BookRankListRequestBean requestBean = new BookRankListRequestBean();
        JDAppBaseInfo appBaseInfo = new JDAppBaseInfo();
        requestBean.setModuleType(rankId);
        requestBean.setType(CloudApiContext.BookRankList.RANK_LIST_TIME_TYPE);
        Map<String, String> queryArgs = new HashMap();
        queryArgs.put(CloudApiContext.SearchBook.PAGE_SIZE, Constants.BOOK_PAGE_SIZE);
        queryArgs.put(CloudApiContext.SearchBook.CURRENT_PAGE, String.valueOf(currentPage));
        appBaseInfo.addRequestParams(queryArgs);
        String sign = String.format(CloudApiContext.BookShopURI.BOOK_RANK_LIST_URI, String.valueOf(rankId), CloudApiContext.BookRankList.RANK_LIST_TIME_TYPE);
        appBaseInfo.setSign(appBaseInfo.getSignValue(sign));
        requestBean.setAppBaseInfo(appBaseInfo);
        RxRequestBookRankList request = new RxRequestBookRankList();
        request.setRequestBean(requestBean);
        request.execute(new RxCallback<RxRequestBookRankList>() {

            @Override
            public void onSubscribe() {
                super.onSubscribe();
                showLoadingDialog(shopDataBundle, R.string.loading);
            }

            @Override
            public void onFinally() {
                super.onFinally();
                hideLoadingDialog(shopDataBundle);
            }

            @Override
            public void onNext(RxRequestBookRankList request) {
                bookModelResultBean = request.getResultBean();
                if (rxCallback != null) {
                    rxCallback.onNext(BookRankListAction.this);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                if (rxCallback != null) {
                    rxCallback.onError(throwable);
                }
            }

            @Override
            public void onComplete() {
                super.onComplete();
                if (rxCallback != null) {
                    rxCallback.onComplete();
                }
            }
        });
    }
}