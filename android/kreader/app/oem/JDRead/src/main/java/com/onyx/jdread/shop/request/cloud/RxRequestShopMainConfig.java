package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.ShopMainConfigRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelConfigResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.ResultBookBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.ReadContentService;
import com.onyx.jdread.shop.model.BannerViewModel;
import com.onyx.jdread.shop.model.BaseSubjectViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.model.SubjectType;
import com.onyx.jdread.shop.model.SubjectViewModel;
import com.onyx.jdread.shop.model.TitleSubjectViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by jackdeng on 2017/12/8.
 */

public class RxRequestShopMainConfig extends RxBaseCloudRequest {

    private ShopMainConfigRequestBean requestBean;
    private BookModelConfigResultBean resultBean;
    private List<BookModelConfigResultBean.DataBean.ModulesBean> subjectDataList;
    private List<BaseSubjectViewModel> mainConfigSubjectList;
    private List<BaseSubjectViewModel> mainConfigFinalSubjectList = new ArrayList<>();

    public List<BookModelConfigResultBean.DataBean.ModulesBean> getSubjectDataList() {
        return subjectDataList;
    }

    public BookModelConfigResultBean getResultBean() {
        return resultBean;
    }

    public void setRequestBean(ShopMainConfigRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    @Override
    public Object call() throws Exception {
        executeCloudRequest();
        return this;
    }

    private void executeCloudRequest() {
        ReadContentService getCommonService = CloudApiContext.getServiceNoCookie(CloudApiContext.getJDBooxBaseUrl());
        Call<BookModelConfigResultBean> call = getCall(getCommonService);
        resultBean = done(call);
        checkRequestResult();
    }

    private void checkRequestResult() {
        if (resultBean != null) {
            parseResult();
        }
    }

    private void parseResult() {
        BookModelConfigResultBean.DataBean data = resultBean.data;
        if (requestBean.getCid() == Constants.BOOK_SHOP_MAIN_CONFIG_CID) {
            parseMainConfigDataList(data);
        } else {
            for (int i = 0; i < data.modules.size(); i++) {
                parseCommonSubjectDataList(data, i);
            }
        }
    }

    private void parseMainConfigDataList(BookModelConfigResultBean.DataBean dataBean) {
        if (dataBean.modules != null && dataBean.ebook != null) {
            initMainConfigSubjectContainer();
            ShopDataBundle shopDataBundle = ShopDataBundle.getInstance();
            List<BookModelConfigResultBean.DataBean.ModulesBean> subjectTitleList = new ArrayList<>();
            for (BookModelConfigResultBean.DataBean.ModulesBean modulesBean : dataBean.modules) {
                if (modulesBean.module_type == Constants.MODULE_TYPE_ADV_FIX_TWO) {// banner subject
                    List<BookModelConfigResultBean.DataBean.ModulesBean.ItemsBean> items = modulesBean.items;
                    BannerViewModel viewModel = new BannerViewModel(shopDataBundle.getEventBus());
                    viewModel.setBannerList(getbannerSubItems(dataBean, items));
                    mainConfigSubjectList.add(viewModel);
                } else if (modulesBean.module_type == Constants.MODULE_TYPE_RECOMMEND) {
                    if (modulesBean.show_type == 1) {// cover subject
                        List<BookModelConfigResultBean.DataBean.ModulesBean.ItemsBean> items = modulesBean.items;
                        modulesBean.bookList = getCommonSubjectSubItems(dataBean, items);
                        SubjectViewModel viewModel = new SubjectViewModel(shopDataBundle.getEventBus());
                        viewModel.setModelBean(modulesBean);
                        mainConfigSubjectList.add(viewModel);
                    } else {// title subject
                        subjectTitleList.add(modulesBean);
                    }
                }
            }
            TitleSubjectViewModel viewModel = new TitleSubjectViewModel(shopDataBundle.getEventBus());
            viewModel.setTilteList(subjectTitleList);
            mainConfigSubjectList.add(viewModel);
            if (mainConfigFinalSubjectList != null) {
                mainConfigFinalSubjectList.clear();
                mainConfigFinalSubjectList.add(shopDataBundle.getShopViewModel().getTopFunctionViewModel());
                mainConfigFinalSubjectList.addAll(mainConfigSubjectList);
                mainConfigFinalSubjectList.add(shopDataBundle.getShopViewModel().getMainConfigEndViewModel());
                shopDataBundle.getShopViewModel().setMainConfigSubjcet(mainConfigFinalSubjectList);
                calculateTotalPages(shopDataBundle, mainConfigFinalSubjectList);
            }
        }
    }

    private void calculateTotalPages(ShopDataBundle dataBundle, List<BaseSubjectViewModel> mainConfigFinalSubjectList) {
        if (mainConfigFinalSubjectList != null) {
            List<BaseSubjectViewModel> tempList = new ArrayList<>();
            tempList.addAll(mainConfigFinalSubjectList);
            int totalPage = 1;
            int itemHeight = 0;
            for (int i = 0; i < tempList.size(); i++) {
                int subjectType = tempList.get(i).getSubjectType();
                switch (subjectType) {
                    case SubjectType.TYPE_TOP_FUNCTION:
                        itemHeight = itemHeight + Constants.SHOP_VIEW_TOP_FUNCTION_HEIGHT;
                        break;
                    case SubjectType.TYPE_BANNER:
                        itemHeight = itemHeight + Constants.SHOP_VIEW_BANNER_HEIGHT;
                        break;
                    case SubjectType.TYPE_TITLE:
                        itemHeight = itemHeight + Constants.SHOP_VIEW_TITLE_HEIGHT;
                        break;
                    case SubjectType.TYPE_COVER:
                        itemHeight = itemHeight + Constants.SHOP_VIEW_SUBJECT_HEIGHT;
                        break;
                    case SubjectType.TYPE_END:
                        itemHeight = itemHeight + Constants.SHOP_VIEW_END_VIEW_HEIGHT;
                        break;
                }
                if (itemHeight > Constants.SHOP_VIEW_RECYCLE_HEIGHT) {
                    tempList.add(i, tempList.get(i));
                    itemHeight = 0;
                    totalPage++;
                }
            }
            dataBundle.getShopViewModel().setTotalPages(Math.max(totalPage, 1));
        }
    }

    private List<BookModelConfigResultBean.DataBean.AdvBean> getbannerSubItems(BookModelConfigResultBean.DataBean dataBean, List<BookModelConfigResultBean.DataBean.ModulesBean.ItemsBean> items) {
        List<BookModelConfigResultBean.DataBean.AdvBean> bannerItemList = new ArrayList<>();
        for (BookModelConfigResultBean.DataBean.ModulesBean.ItemsBean itemsBean : items) {
            if (Constants.MAIN_CONFIG_TYPE_ADV.equals(itemsBean.type)) {
                BookModelConfigResultBean.DataBean.AdvBean advBean = dataBean.adv.get(itemsBean.id);
                bannerItemList.add(advBean);
            }
        }
        return bannerItemList;
    }

    private List<ResultBookBean> getCommonSubjectSubItems(BookModelConfigResultBean.DataBean dataBean, List<BookModelConfigResultBean.DataBean.ModulesBean.ItemsBean> items) {
        List<ResultBookBean> bookItemList = new ArrayList<>();
        for (BookModelConfigResultBean.DataBean.ModulesBean.ItemsBean itemsBean : items) {
            if (Constants.MAIN_CONFIG_TYPE_EBOOK.equals(itemsBean.type)) {
                ResultBookBean bookBean = dataBean.ebook.get(itemsBean.id);
                bookItemList.add(bookBean);
            }
        }
        return bookItemList;
    }

    private void initMainConfigSubjectContainer() {
        if (mainConfigSubjectList == null) {
            mainConfigSubjectList = new ArrayList<>();
        } else {
            mainConfigSubjectList.clear();
        }
    }

    private void initDataContainer() {
        if (subjectDataList == null) {
            subjectDataList = new ArrayList<>();
        } else {
            subjectDataList.clear();
        }
    }

    private void parseCommonSubjectDataList(BookModelConfigResultBean.DataBean dataBean, int index) {
        if (dataBean.ebook != null && dataBean.modules != null) {
            initDataContainer();
            ArrayList<ResultBookBean> bookList = new ArrayList<>();
            BookModelConfigResultBean.DataBean.ModulesBean modulesBean = dataBean.modules.get(index);
            List<BookModelConfigResultBean.DataBean.ModulesBean.ItemsBean> items = modulesBean.items;
            for (BookModelConfigResultBean.DataBean.ModulesBean.ItemsBean itemsBean : items) {
                ResultBookBean bookBean = dataBean.ebook.get(itemsBean.id);
                bookList.add(bookBean);
            }
            modulesBean.bookList = bookList;
            if (index % 2 == 1) {
                if (dataBean.modules.size() - 1 >= (index + 1)) {
                    BookModelConfigResultBean.DataBean.ModulesBean modulesBeanNext = dataBean.modules.get(index + 1);
                    modulesBean.show_name_next = modulesBeanNext.show_name;
                    modulesBean.f_type_next = modulesBeanNext.f_type;
                    modulesBean.id_next = modulesBeanNext.id;
                    modulesBean.showNextTitle = true;
                }
            }
            subjectDataList.add(modulesBean);
        }
    }

    private BookModelConfigResultBean done(Call<BookModelConfigResultBean> call) {
        EnhancedCall<BookModelConfigResultBean> enhancedCall = new EnhancedCall<>(call);
        return enhancedCall.execute(call, BookModelConfigResultBean.class);
    }

    private Call<BookModelConfigResultBean> getCall(ReadContentService getCommonService) {
        return getCommonService.getShopMainConfig(requestBean.getCid(),
                requestBean.getAppBaseInfo().getRequestParamsMap());
    }
}
