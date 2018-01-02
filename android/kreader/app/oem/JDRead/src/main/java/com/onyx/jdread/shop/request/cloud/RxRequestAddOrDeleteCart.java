package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.main.common.CommonUtils;
import com.onyx.jdread.shop.cloud.api.AddOrDeleteFromCartService;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.AddOrDelFromCartBean;
import com.onyx.jdread.shop.common.CloudApiContext;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by 12 on 2017/4/24.
 */

public class RxRequestAddOrDeleteCart extends RxBaseCloudRequest {
    private BaseRequestBean requestBean;
    private AddOrDelFromCartBean resultBean;

    public AddOrDelFromCartBean getResultBean() {
        return resultBean;
    }

    public void setRequestBean(BaseRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    @Override
    public Object call() throws Exception {
        if (CommonUtils.isNetworkConnected(JDReadApplication.getInstance())) {
            executeCloudRequest();
        }
        return this;
    }

    private void executeCloudRequest() throws IOException {
        AddOrDeleteFromCartService service = init(CloudApiContext.getJdBaseUrl());
        Call<AddOrDelFromCartBean> call = getCall(service);
        Response<AddOrDelFromCartBean> response = call.execute();
        if (response != null) {
            resultBean = response.body();
            checkQuestResult();
        }
    }

    private void checkQuestResult() {
        if (resultBean != null && !StringUtils.isNullOrEmpty(resultBean.getCode())) {

        }
    }

    private Call<AddOrDelFromCartBean> getCall(AddOrDeleteFromCartService service) {
        return service.addOrDeleteFromCart(CloudApiContext.NewBookDetail.SHOPPING_CART,
                requestBean.getBody(),
                requestBean.getAppBaseInfo().getRequestParamsMap());
    }

    private AddOrDeleteFromCartService init(String url) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(CloudApiContext.getClient())
                .build();
        return retrofit.create(AddOrDeleteFromCartService.class);
    }
}