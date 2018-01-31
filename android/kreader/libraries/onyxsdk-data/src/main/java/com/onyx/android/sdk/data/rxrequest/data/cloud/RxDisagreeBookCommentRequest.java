package com.onyx.android.sdk.data.rxrequest.data.cloud;

import android.util.Log;

import com.onyx.android.sdk.data.model.Comment;
import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseBookStoreRequest;
import com.onyx.android.sdk.data.rxrequest.data.cloud.bean.DisagreeBookCommentRequestBean;

import retrofit2.Response;

/**
 * Created by jackdeng on 2017/11/13.
 */

public class RxDisagreeBookCommentRequest extends RxBaseBookStoreRequest {
    private final DisagreeBookCommentRequestBean requestBean;
    private Comment result;

    public RxDisagreeBookCommentRequest(DisagreeBookCommentRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    @Override
    public RxDisagreeBookCommentRequest call() throws Exception {
        try {
            Response<Comment> response = getService().disagreeBookComment(requestBean.bookId, requestBean.commentId, requestBean.sessionToken).execute();
            if (response != null && response.isSuccessful()) {
                result = response.body();
            }
        } catch (Exception e) {
            Log.i(TAG, e.toString());
        }
        return this;
    }

    public Comment getResult() {
        return result;
    }
}