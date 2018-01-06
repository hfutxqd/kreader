package com.onyx.jdread.personal.cloud.api;

import com.onyx.jdread.main.common.AppBaseInfo;
import com.onyx.jdread.personal.cloud.entity.jdbean.ReadTotalInfoBean;
import com.onyx.jdread.shop.common.CloudApiContext;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by li on 2018/1/2.
 */

public interface GetReadTotalService {
    @POST("client.action")
    Call<ReadTotalInfoBean> getReadTotalBook(@Query(CloudApiContext.NewBookDetail.FUNCTION_ID) String functionID,
                                             @Query(AppBaseInfo.JD_USER_NAME) String userName,
                                             @QueryMap Map<String, String> map);
}
